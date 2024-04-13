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
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasourceDao;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.service.IBaseDatasourceService;
import com.gccloud.dataset.utils.DBUtils;
import com.gccloud.dataset.vo.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wangkang
 * @version 1.0.1
 * @date 2023/8/25
 */
@Slf4j
@Service(DatasetConstant.DatasourceType.SQLSERVER)
public class SqlServerDatasourceServiceImpl extends ServiceImpl<DatasourceDao, DatasourceEntity> implements IBaseDatasourceService {
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
        Object count = countData.getData().get(0).get("COUNT");
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
        String pageSql = "select " + columnStr + " from (" + sql + ") as t order by 1 offset " + start + " rows fetch next " + 10 + " rows only";
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
    public List<TableInfoVO> getTableList(DatasourceEntity datasource) {
        // 从url中获取模式名称，可能不存在
        String schema = this.getSchemaFromUrl(datasource.getUrl());
        String sql = "SELECT TABLE_CATALOG as tableCatalog, TABLE_SCHEMA as tableSchema, TABLE_NAME as name\n" +
                "FROM INFORMATION_SCHEMA.TABLES\n" +
                "WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_CATALOG = DB_NAME()";
        if (StringUtils.isNotBlank(schema)) {
            sql += " AND TABLE_SCHEMA = '" + schema + "'";
        }
        DbDataVO dataVO = DBUtils.getSqlValue(sql, datasource);
        List<Map<String, Object>> data = dataVO.getData();
        List<TableInfoVO> tableList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(data)) {
            return tableList;
        }
        for (Map<String, Object> map : data) {
            TableInfoVOExtend tableInfoVO = new TableInfoVOExtend();
            String name = String.valueOf(map.get("name"));
            if (map.get("tableSchema") != null) {
                name = map.get("tableSchema") + "." + name;
            }
            tableInfoVO.setName(name);
            tableInfoVO.setStatus(0);
            tableInfoVO.setTableCatalog(String.valueOf(map.get("tableCatalog")));
            tableInfoVO.setTableSchema(String.valueOf(map.get("tableSchema")));
            tableList.add(tableInfoVO);
        }
        return tableList;
    }

    @Override
    public List<TableInfoVO> getViewList(DatasourceEntity datasource) {
        // 从url中获取模式名称，可能不存在
        String schema = this.getSchemaFromUrl(datasource.getUrl());
        String sql = "SELECT TABLE_CATALOG as tableCatalog, TABLE_SCHEMA as tableSchema, TABLE_NAME as name\n" +
                "FROM INFORMATION_SCHEMA.TABLES\n" +
                "WHERE TABLE_TYPE = 'VIEW' AND TABLE_CATALOG = DB_NAME()";
        if (StringUtils.isNotBlank(schema)) {
            sql += " AND TABLE_SCHEMA = '" + schema + "'";
        }
        DbDataVO dataVO = DBUtils.getSqlValue(sql, datasource);
        List<Map<String, Object>> data = dataVO.getData();
        List<TableInfoVO> tableInfoVOS = Lists.newArrayList();
        if (CollectionUtils.isEmpty(data)) {
            return tableInfoVOS;
        }
        for (Map<String, Object> map : data) {
            TableInfoVOExtend tableInfoVO = new TableInfoVOExtend();
            String name = String.valueOf(map.get("name"));
            if (map.get("tableSchema") != null) {
                name = map.get("tableSchema") + "." + name;
            }
            tableInfoVO.setName(name);
            tableInfoVO.setStatus(0);
            tableInfoVO.setTableCatalog(String.valueOf(map.get("tableCatalog")));
            tableInfoVO.setTableSchema(String.valueOf(map.get("tableSchema")));
            tableInfoVOS.add(tableInfoVO);
        }
        return tableInfoVOS;
    }

    @Override
    public List<FieldInfoVO> getTableColumnList(DatasourceEntity datasource, String fullTableName) {
        String schema = "";
        String tableName = "";
        // fullTableName 可能是 database.schema.tableName、schema.tableName、tableName
        String[] split = fullTableName.split("\\.");
        if (split.length == 1) {
            tableName = split[0];
        } else if (split.length == 2) {
            schema = split[0];
            tableName = split[1];
        } else if (split.length == 3) {
            schema = split[1];
            tableName = split[2];
        }
        String sql = "select COLUMN_NAME,DATA_TYPE from information_schema.columns where table_name = '" + tableName + "'";
        if (StringUtils.isNotBlank(schema)) {
            sql += " and table_schema = '" + schema + "'";
        }
        DbDataVO dataVO = DBUtils.getSqlValue(sql, datasource);
        List<Map<String, Object>> data = dataVO.getData();
        List<FieldInfoVO> fieldInfoVOS = Lists.newArrayList();
        if (CollectionUtils.isEmpty(data)) {
            return fieldInfoVOS;
        }
        // 获取表中字段注释
        String columnDescSql = "select a.name  table_name,b.name  column_name, c.value  column_description from sys.tables a inner join sys.columns b on b.object_id = a.object_id left join sys.extended_properties c on c.major_id = b.object_id and c.minor_id = b.column_id where a.name = '" + tableName + "'";
        if (StringUtils.isNotBlank(schema)) {
            columnDescSql += " and a.schema_id = schema_id('" + schema + "')";
        }
        DbDataVO sqlValue = DBUtils.getSqlValue(columnDescSql, datasource);
        List<Map<String, Object>> columnDescMap = sqlValue.getData();
        List<FieldInfoVO> columnDescList = Lists.newArrayList();
        for (Map<String, Object> columnDesc : columnDescMap) {
            FieldInfoVO fieldInfoVO = new FieldInfoVO();
            fieldInfoVO.setColumnName(String.valueOf(columnDesc.get("column_name")));
            fieldInfoVO.setColumnComment(String.valueOf(columnDesc.get("column_description")));
            columnDescList.add(fieldInfoVO);
        }
        Map<String, String> collect = columnDescList.stream().collect(Collectors.toMap(FieldInfoVO::getColumnName, FieldInfoVO::getColumnComment));
        for (Map<String, Object> map : data) {
            FieldInfoVO fieldInfoVO = new FieldInfoVO();
            fieldInfoVO.setColumnName(String.valueOf(map.get("COLUMN_NAME")));
            fieldInfoVO.setColumnType(String.valueOf(map.get("DATA_TYPE")));
            fieldInfoVO.setColumnComment(collect.get(String.valueOf(map.get("COLUMN_NAME"))));
            fieldInfoVOS.add(fieldInfoVO);
        }
        return fieldInfoVOS;
    }

    @Override
    public DataVO executeProcedure(DatasourceEntity datasource, String procedure, Integer current, Integer size) {
        DbDataVO dbDataVO = DBUtils.call(procedure, datasource, current, size);
        boolean pageFlag = current != null && size != null;
        if (pageFlag) {
            return new DataVO(dbDataVO.getPageData(), dbDataVO.getStructure());
        }
        return new DataVO(dbDataVO.getData(), dbDataVO.getStructure());
    }


    /**
     * 从url中获取schema
     *
     * @param url
     * @return
     */
    private String getSchemaFromUrl(String url) {
        String[] split = url.split(";");
        String schema = "";
        for (String s : split) {
            if (s.contains("schema=")) {
                schema = s.split("=")[1];
            }
        }
        return schema;
    }
}