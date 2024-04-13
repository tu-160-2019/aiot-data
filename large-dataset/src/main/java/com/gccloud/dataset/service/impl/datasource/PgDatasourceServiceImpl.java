/*
 * Copyright 2023 http://gcpaas.gccloud.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gccloud.dataset.service.impl.datasource;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasourceDao;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.service.IBaseDatasourceService;
import com.gccloud.dataset.utils.DBUtils;
import com.gccloud.dataset.vo.DataVO;
import com.gccloud.dataset.vo.DbDataVO;
import com.gccloud.dataset.vo.FieldInfoVO;
import com.gccloud.dataset.vo.TableInfoVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Postgresql数据源服务实现类
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/6/2 11:06
 */
@Slf4j
@Service(DatasetConstant.DatasourceType.POSTGRESQL)
public class PgDatasourceServiceImpl extends ServiceImpl<DatasourceDao, DatasourceEntity> implements IBaseDatasourceService {

    @Override
    public DataVO executeSql(DatasourceEntity datasource, String sql) {
        DbDataVO dbDataVO = DBUtils.getSqlValue(sql, datasource);
        return new DataVO(dbDataVO.getData(), dbDataVO.getStructure());
    }

    @Override
    public DataVO executeSqlPage(DatasourceEntity datasource, String sql, Integer current, Integer size) {
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        // 组装计算总条数的sql
        String countSql = "SELECT COUNT(1) AS COUNT FROM (" + sql + ") AS t";
        log.info("数据集数据详情计算总条数 sql语句：{}", countSql);
        DbDataVO countData = DBUtils.getSqlValue(countSql, datasource);
        // pg数据库会自动将count转为小写
        Object count = countData.getData().get(0).get("count");
        int total = Integer.parseInt(count.toString());
        // 组装分页sql
        int start = (current - 1) * size;
        List<String> columns = DBUtils.getColumns(sql, datasource.getSourceType());
        String columnStr;
        if (columns == null || columns.size() == 0 || columns.contains("*")) {
            columnStr = "*";
        } else {
            columnStr = String.join(",", columns);
        }
        String pageSql = "SELECT " + columnStr + " FROM (" + sql + ") AS t LIMIT " + size + " OFFSET " + start;
        log.info("数据集数据详情分页 sql语句：{}", pageSql);
        DbDataVO pageData = DBUtils.getSqlValue(pageSql, datasource);
        PageVO<Map<String, Object>> page = new PageVO<>();
        page.setCurrent(current);
        page.setSize(size);
        page.setTotalCount(total);
        page.setTotalPage((total + size - 1) / size);
        page.setList(pageData.getData());
        return new DataVO(page, pageData.getStructure());
    }

    @Override
    public DataVO executeProcedure(DatasourceEntity datasource, String procedure, Integer current, Integer size) {
        // jdbc执行sql时，如果以{}包裹，表示是一个存储过程
        if (!procedure.startsWith("{") && !procedure.endsWith("}")) {
            procedure = "{" + procedure + "}";
        }
        DbDataVO call = DBUtils.call(procedure, datasource, current, size);
        boolean pageFlag = current != null && size != null;
        if (pageFlag) {
            return new DataVO(call.getPageData(), call.getStructure());
        }
        return new DataVO(call.getData(), call.getStructure());
    }

    @Override
    public List<TableInfoVO> getTableList(DatasourceEntity datasource) {
        String sql = "select tablename from pg_tables where schemaname = 'public' order by tablename ";
        String currentSchema = "";
        // 通过url获取当前schema信息
        String url = datasource.getUrl();
        if (url.contains("currentSchema")) {
            String substring = url.substring(url.lastIndexOf("?") + 1);
            String[] strings = substring.split("&");
            for (String string : strings) {
                if (string.contains("currentSchema=")) {
                    currentSchema = string.substring(string.lastIndexOf("=") + 1);
                    break;
                }
            }
        }
        if (StringUtils.isNotBlank(currentSchema)) {
            sql = " select tablename from pg_tables where schemaname = '" + currentSchema + "' order by tablename ";
        }
        DbDataVO dataVO = DBUtils.getSqlValue(sql, datasource);
        List<Map<String, Object>> data = dataVO.getData();
        List<TableInfoVO> tableList = Lists.newArrayList();
        if (data == null || data.size() == 0) {
            return tableList;
        }
        for (Map<String, Object> map : data) {
            TableInfoVO tableInfoVO = new TableInfoVO();
            tableInfoVO.setName(map.get("tablename").toString());
            tableInfoVO.setStatus(0);
            tableList.add(tableInfoVO);
        }
        return tableList;
    }

    @Override
    public List<FieldInfoVO> getTableColumnList(DatasourceEntity datasource, String tableName) {
        Connection conn = DBUtils.getConnection(datasource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<FieldInfoVO> columnList = Lists.newArrayList();
        try {
            String sql = "select * from " + tableName + " limit 0";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            // 获取字段注释
            String commentSql = "SELECT A.attname as COLUMN_NAME,col_description ( A.attrelid,A.attnum ) as COLUMN_COMMENT FROM pg_class as C,pg_attribute as A WHERE C.relname = '" + tableName + "' and A.attrelid = C.oid and A.attnum > 0";
            PreparedStatement commentPs = conn.prepareStatement(commentSql);
            ResultSet commentRs = commentPs.executeQuery();
            Map<String, String> commentMap = new HashMap<>();
            while (commentRs.next()) {
                commentMap.put(commentRs.getString("COLUMN_NAME"), commentRs.getString("COLUMN_COMMENT"));
            }
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                FieldInfoVO fieldInfoVO = new FieldInfoVO();
                fieldInfoVO.setColumnName(metaData.getColumnName(i));
                fieldInfoVO.setColumnType(metaData.getColumnTypeName(i));
                fieldInfoVO.setColumnComment(commentMap.get(metaData.getColumnName(i)));
                columnList.add(fieldInfoVO);
            }
            return columnList;
        } catch (SQLException e) {
            log.error("获取表字段信息失败，tableName：{}", tableName);
            log.error(ExceptionUtils.getStackTrace(e));
            throw new GlobalException("获取表字段信息失败");
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("数据源连接关闭异常,{}", e.getMessage());
            }
        }
    }

    @Override
    public List<TableInfoVO> getViewList(DatasourceEntity datasource) {
        String sql = " select viewname from pg_views where schemaname='public'";
        String currentSchema = "";
        // 通过url获取当前schema信息
        String url = datasource.getUrl();
        if (url.contains("currentSchema")) {
            String substring = url.substring(url.lastIndexOf("?") + 1);
            String[] strings = substring.split("&");
            for (String string : strings) {
                if (string.contains("currentSchema=")) {
                    currentSchema = string.substring(string.lastIndexOf("=") + 1);
                    break;
                }
            }
        }
        if (StringUtils.isNotBlank(currentSchema)) {
            sql = " select viewname from pg_views where schemaname='" + currentSchema + "'";
        }
        DbDataVO dataVO = DBUtils.getSqlValue(sql, datasource);
        List<Map<String, Object>> data = dataVO.getData();
        List<TableInfoVO> tableList = Lists.newArrayList();
        if (data == null || data.size() == 0) {
            return tableList;
        }
        for (Map<String, Object> map : data) {
            TableInfoVO tableInfoVO = new TableInfoVO();
            tableInfoVO.setName(map.get("viewname").toString());
            tableInfoVO.setStatus(0);
            tableList.add(tableInfoVO);
        }
        return tableList;
    }

    @Override
    public List<FieldInfoVO> getViewColumnList(DatasourceEntity datasource, String viewName) {
        // 视图的字段信息和表的字段信息查询方法一致
        return getTableColumnList(datasource, viewName);
    }
}