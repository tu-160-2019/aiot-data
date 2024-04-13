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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Mysql数据源服务实现类
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/6/2 11:06
 */
@Slf4j
@Service(DatasetConstant.DatasourceType.MYSQL)
public class MysqlDatasourceServiceImpl extends ServiceImpl<DatasourceDao, DatasourceEntity> implements IBaseDatasourceService {

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
        String pageSql = "SELECT " + columnStr + " FROM (" + sql + ") AS t LIMIT " + start + "," + size;
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
        // 如果包含 ,加任意空格加?加任意空格加),则将其替换为),因为mysql的存储过程不支持这种写法，这种写法是用于兼容oracle的
        procedure = procedure.replaceAll(",\\s*\\?\\s*\\)", ")");
        // jdbc执行sql时，如果以{}包裹，表示是一个存储过程
        if (!procedure.startsWith("{") && !procedure.endsWith("}")) {
            procedure = "{" + procedure + "}";
        }
        DbDataVO dbDataVO = DBUtils.call(procedure, datasource, current, size);
        boolean pageFlag = current != null && size != null;
        if (pageFlag) {
            return new DataVO(dbDataVO.getPageData(), dbDataVO.getStructure());
        }
        return new DataVO(dbDataVO.getData(), dbDataVO.getStructure());
    }

    @Override
    public List<TableInfoVO> getTableList(DatasourceEntity datasource) {
        String sql = "show tables";
        DbDataVO dataVO = DBUtils.getSqlValueWithoutCheck(sql, datasource);
        List<Map<String, Object>> data = dataVO.getData();
        List<TableInfoVO> tableList = Lists.newArrayList();
        if (data == null || data.size() == 0) {
            return tableList;
        }
        // show tables的结果集中，第一列的列名是"Tables_in_数据库名"，所以这里取第一列的列名
        List<Map<String, Object>> structure = dataVO.getStructure();
        String columnName = structure.get(0).get(DatasetInfoVO.FIELD_NAME).toString();
        for (Map<String, Object> map : data) {
            TableInfoVO tableInfoVO = new TableInfoVO();
            tableInfoVO.setName(map.get(columnName).toString());
            tableInfoVO.setStatus(0);
            tableList.add(tableInfoVO);
        }
        return tableList;
    }

    @Override
    public List<FieldInfoVO> getTableColumnList(DatasourceEntity datasource, String tableName) {
        String sql = "show full columns from " + tableName;
        DbDataVO dataVO = DBUtils.getSqlValueWithoutCheck(sql, datasource);
        List<Map<String, Object>> data = dataVO.getData();
        List<FieldInfoVO> fieldList = Lists.newArrayList();
        if (data == null || data.size() == 0) {
            return fieldList;
        }
        // NOTE 不同版本的驱动中，metaData中的列名定义不一样，这里做兼容处理
        boolean flag = data.get(0).containsKey("Field");
        String columnName = flag ? "Field" : "COLUMN_NAME";
        String columnType = flag ? "Type" : "COLUMN_TYPE";
        String columnComment = flag ? "Comment" : "COLUMN_COMMENT";
        for (Map<String, Object> map : data) {
            FieldInfoVO fieldInfoVO = new FieldInfoVO();
            fieldInfoVO.setColumnName(String.valueOf(map.get(columnName)));
            fieldInfoVO.setColumnType(String.valueOf(map.get(columnType)));
            fieldInfoVO.setColumnComment(String.valueOf(map.get(columnComment)));
            fieldList.add(fieldInfoVO);
        }
        return fieldList;
    }

    @Override
    public List<TableInfoVO> getViewList(DatasourceEntity datasource) {
        String sql = "show table status where comment='view'";
        DbDataVO dataVO = DBUtils.getSqlValueWithoutCheck(sql, datasource);
        List<Map<String, Object>> data = dataVO.getData();
        List<TableInfoVO> tableList = Lists.newArrayList();
        if (data == null || data.size() == 0) {
            return tableList;
        }
        for (Map<String, Object> map : data) {
            TableInfoVO tableInfoVO = new TableInfoVO();
            tableInfoVO.setName(String.valueOf(map.get("Name")));
            tableInfoVO.setStatus(0);
            tableList.add(tableInfoVO);
        }
        return tableList;
    }

    @Override
    public List<FieldInfoVO> getViewColumnList(DatasourceEntity datasource, String viewName) {
        // 对于mysql来说，视图的字段信息和表的字段信息是一样的
        return getTableColumnList(datasource, viewName);
    }
}