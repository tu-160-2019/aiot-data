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

package com.gccloud.dataset.utils;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUseStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.vo.DatasetInfoVO;
import com.gccloud.dataset.vo.DbDataVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.internal.OracleTypes;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.CollectionUtils;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库工具类
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/6/5 10:46
 */
@Slf4j
public class DBUtils {


    /**
     * 为避免处理存储过程返回结果集时，数据量过大导致内存溢出，限制最大返回数据量
     * TODO 迁移到配置文件
     */
    private static final Integer STORED_PROCEDURE_MAX_HINT = 2000;


    /**
     * 调用存储过程
     *
     * @param procedure
     * @param datasource
     * @param current
     * @param size
     * @return
     */
    public static DbDataVO call(String procedure, DatasourceEntity datasource, Integer current, Integer size) {
        Connection connection = getConnection(datasource);
        if (connection == null) {
            throw new GlobalException("数据源连接建立失败");
        }
        // 数据集
        DbDataVO dataVO = new DbDataVO();
        List<Map<String, Object>> data = Lists.newArrayList();
        List<Map<String, Object>> structure = Lists.newArrayList();
        dataVO.setData(data);
        dataVO.setStructure(structure);
        try {
            CallableStatement proc;
            // 使用可滚动的结果集
            proc = connection.prepareCall(procedure, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (DatasetConstant.DatasourceType.ORACLE.equalsIgnoreCase(datasource.getSourceType())) {
                // oracle需要注册返回参数
                proc.registerOutParameter(1, OracleTypes.CURSOR);
            }
            if (DatasetConstant.DatasourceType.POSTGRESQL.equalsIgnoreCase(datasource.getSourceType())) {
                // postgresql需要注册返回参数
                proc.registerOutParameter(1, Types.OTHER);
            }
            // NOTE execute方法针对部分存储过程不会返回结果集，需要使用executeQuery方法
            proc.executeQuery();
            ResultSet rs;
            if (DatasetConstant.DatasourceType.ORACLE.equalsIgnoreCase(datasource.getSourceType()) || DatasetConstant.DatasourceType.POSTGRESQL.equals(datasource.getSourceType())) {
                // oracle和postgresql需要通过返回参数获取结果集
                rs = (ResultSet) proc.getObject(1);
            } else {
                rs = proc.getResultSet();
            }
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            // 获取列信息
            for (int i = 0; i < columnCount; i++) {
                Map<String, Object> structureMap = new ConcurrentHashMap<>();
                structureMap.put(DatasetInfoVO.FIELD_NAME, metaData.getColumnName(i + 1));
                structureMap.put(DatasetInfoVO.FIELD_TYPE, metaData.getColumnTypeName(i + 1));
                structure.add(structureMap);
            }
            // 获取总数
            int totalSize = 0;
            // 检查结果集类型，用于判断是否支持滚动
            int resultSetType = rs.getType();
            // 是否支持滚动
            boolean isScroll = resultSetType == ResultSet.TYPE_SCROLL_INSENSITIVE || resultSetType == ResultSet.TYPE_SCROLL_SENSITIVE;
            // 处理分页
            boolean isPage = current != null && size != null;
            if (!isPage) {
                // 不分页
                int resultCount = 0;
                // 遍历结果集
                while (rs.next()) {
                    resultCount++;
                    if (resultCount > STORED_PROCEDURE_MAX_HINT) {
                        // 到达最大限制，结束
                        break;
                    }
                    // 获取数据
                    Map<String, Object> map = new HashMap<>(columnCount);
                    for (int i = 1; i <= columnCount; i++) {
                        map.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    data.add(map);
                }
                return dataVO;
            }
            if (!isScroll) {
                // 分页但不支持滚动
                PageVO<Map<String, Object>> pageData = new PageVO<>();
                pageData.setTotalCount(0);
                pageData.setTotalPage(0);
                pageData.setCurrent(current);
                pageData.setSize(size);
                dataVO.setPageData(pageData);
                // 检查分页结尾是否超过限制
                if (current * size > STORED_PROCEDURE_MAX_HINT) {
                    // 超过限制，不处理
                    log.warn("分页数据位置超过最大限制，无法处理:" + STORED_PROCEDURE_MAX_HINT);
                    return dataVO;
                }
                int resultCount = 0;
                int start = (current - 1) * size;
                // 遍历结果集
                while (rs.next()) {
                    resultCount++;
                    if (resultCount > STORED_PROCEDURE_MAX_HINT) {
                        // 到达最大限制，结束
                        break;
                    }
                    if (resultCount < start) {
                        // 未到达分页起始位置，跳过
                        continue;
                    }
                    if (resultCount == start + size) {
                        // 到达分页结尾，跳过
                        continue;
                    }
                    // 获取数据
                    Map<String, Object> map = new HashMap<>(columnCount);
                    for (int i = 1; i <= columnCount; i++) {
                        map.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    data.add(map);
                }
                totalSize = resultCount;
                pageData.setTotalCount(totalSize);
                pageData.setTotalPage(totalSize % size == 0 ? totalSize / size : totalSize / size + 1);
                pageData.setList(data);
                return dataVO;
            }
            // 分页且支持滚动
            // 先获取总数
            // 将游标移动到最后一行
            rs.last();
            // 获取最后一行行号，即总数
            totalSize = rs.getRow();
            // 将游标移动到第一行之前，即准备遍历
            rs.beforeFirst();
            if (size > STORED_PROCEDURE_MAX_HINT) {
                // 分页大小超过最大限制，使用最大限制
                size = STORED_PROCEDURE_MAX_HINT;
                log.warn("分页大小超过最大限制，使用最大限制数:" + STORED_PROCEDURE_MAX_HINT);
            }
            int start = (current - 1) * size;
            if (start > totalSize) {
                // 超过总数，返回空
                PageVO<Map<String, Object>> pageData = new PageVO<>();
                pageData.setTotalCount(totalSize);
                pageData.setTotalPage(totalSize % size == 0 ? totalSize / size : totalSize / size + 1);
                pageData.setCurrent(current);
                pageData.setSize(size);
                dataVO.setPageData(pageData);
                return dataVO;
            }
            // 将游标移动到分页开始位置
            rs.absolute((current - 1) * size);
            // 计数
            int resultCount = 0;
            // 遍历结果集
            while (rs.next()) {
                resultCount++;
                if (resultCount > size) {
                    // 到达分页size，结束
                    break;
                }
                // 获取数据
                Map<String, Object> map = new HashMap<>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    map.put(metaData.getColumnName(i), rs.getObject(i));
                }
                data.add(map);
            }
            // 设置分页信息
            PageVO<Map<String, Object>> pageData = new PageVO<>();
            pageData.setTotalCount(totalSize);
            pageData.setTotalPage(totalSize % size == 0 ? totalSize / size : totalSize / size + 1);
            pageData.setCurrent(current);
            pageData.setSize(size);
            pageData.setList(data);
            dataVO.setPageData(pageData);
        } catch (Exception ex) {
            log.error("存储过程执行失败 ，{}", ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
            throw new GlobalException(ex.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("数据源连接关闭异常,{}", e.getMessage());
            }
        }
        return dataVO;
    }

    /**
     * 执行sql，获取数据以及列信息
     *
     * @param sql
     * @param datasource
     * @return
     */
    public static DbDataVO getSqlValue(String sql, DatasourceEntity datasource) {
        log.info("执行sql:" + sql);
        boolean onlySelectSql = DBUtils.onlySelectSql(sql, datasource.getSourceType());
        if (!onlySelectSql) {
            throw new GlobalException("只支持select语句");
        }
        return executeSql(sql, datasource);

    }

    /**
     * 执行sql，获取数据以及列信息，不检查sql是否为select语句
     *
     * @param sql
     * @param datasource
     * @return
     */
    public static DbDataVO getSqlValueWithoutCheck(String sql, DatasourceEntity datasource) {
        log.info("执行sql:" + sql);
        return executeSql(sql, datasource);
    }


    /**
     * 执行sql
     *
     * @param sql
     * @param datasource
     * @return
     */
    public static DbDataVO executeSql(String sql, DatasourceEntity datasource) {
        Connection connection = getConnection(datasource);
        if (connection == null) {
            throw new GlobalException("数据源连接建立失败");
        }
        // 数据集
        DbDataVO dataVO = new DbDataVO();
        List<Map<String, Object>> data = Lists.newArrayList();
        List<Map<String, Object>> structure = Lists.newArrayList();
        dataVO.setData(data);
        dataVO.setStructure(structure);
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet clickRs = ps.executeQuery();
            ResultSetMetaData metaData = clickRs.getMetaData();
            int columnCount = metaData.getColumnCount();
            // 获取列信息
            for (int i = 1; i <= columnCount; i++) {
                Map<String, Object> structureMap = new ConcurrentHashMap<>();
                structureMap.put(DatasetInfoVO.FIELD_NAME, metaData.getColumnName(i));
                structureMap.put(DatasetInfoVO.FIELD_TYPE, metaData.getColumnTypeName(i));
                structure.add(structureMap);
            }
            // 获取结果集
            while (clickRs.next()) {
                // 获取数据
                Map<String, Object> map = new HashMap<>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    map.put(metaData.getColumnName(i), clickRs.getObject(i));
                }
                data.add(map);
            }
        } catch (Exception e) {
            log.error("数据查询失败:{}", ExceptionUtils.getStackTrace(e));
            throw new GlobalException("数据查询失败" + e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("数据源连接关闭异常,{}", e.getMessage());
            }
        }
        return dataVO;
    }


    /**
     * 获取数据库连接
     *
     * @param datasource 数据源
     */
    public static Connection getConnection(DatasourceEntity datasource) {
        Connection connection;
        try {
            Class.forName(datasource.getDriverClassName());
            if (datasource.getUsername() != null && datasource.getPassword() != null) {
                connection = DriverManager.getConnection(
                        datasource.getUrl(), datasource.getUsername(), DESUtils.getDecryptString(datasource.getPassword()));
            } else {
                connection = DriverManager.getConnection(datasource.getUrl());
            }

        } catch (SQLSyntaxErrorException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new GlobalException("数据库不存在");
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new GlobalException(getExceptionMessage(e.getMessage()));
        }
        return connection;
    }

    /**
     * 异常提示信息汉化处理
     *
     * @param message 异常message
     */
    private static String getExceptionMessage(String message) {
        if (message.contains("Connection refused")) {
            return "数据源连接超时";
        } else if (message.contains("password")) {
            return "数据源用户名或密码有误";
        } else if (message.contains("Database")) {
            return "数据库不存在";
        } else if (message.contains("driver")) {
            return "数据源连接url格式有误";
        } else if (message.contains("TCP/IP connection")) {
            return "数据源连接超时";
        } else if (message.contains("does not exist")) {
            return "数据库不存在";
        } else {
            return message;
        }
    }


    /**
     * 将sql语句中中进行非空标签的处理，同时参数变量替换成实际值
     * 该方法只负责简单的${}参数替换，如果是用到了Mybatis的动态标签，请使用com.gccloud.dataset.utils.MybatisParameterUtils#updateParamsConfig
     *
     * @param sql    sql语句
     * @param params 参数配置
     */
    public static String updateParamsConfig(String sql, List<DatasetParamDTO> params) {
        if (CollectionUtils.isEmpty(params)) {
            return sql;
        }
        for (DatasetParamDTO param : params) {
            if (null == param.getStatus()) {
                continue;
            }
            if (!DatasetConstant.SqlParamsStatus.VARIABLE.equals(param.getStatus())) {
                continue;
            }
            if (sql.contains("<" + param.getName() + ">") && sql.contains("</" + param.getName() + ">")) {
                if (StringUtils.isEmpty(param.getValue())) {
                    // 具备非空判断标签中的内容给去掉
                    sql = subRangeString(sql, "<" + param.getName() + ">", "</" + param.getName() + ">");
                } else {
                    sql = sql.replaceAll("<" + param.getName() + ">", "").replaceAll("</" + param.getName() + ">", "");
                    sql = parameterReplace(param, sql);
                }
            } else {
                // 不具备非空判断标签
                sql = parameterReplace(param, sql);
            }
        }
//        params.removeIf(next -> next.getStatus() != null && next.getStatus().equals(DatasetConstant.SqlParamsStatus.VARIABLE));
        return sql;
    }

    /**
     * sq语句中参数变量赋值
     */
    public static String parameterReplace(DatasetParamDTO param, String sql) {
        if (DatasetConstant.SqlParamsType.STRING.equals(param.getType()) || DatasetConstant.SqlParamsType.DATE.equals(param.getType())) {
            sql = sql.replaceAll("\\$\\{" + param.getName() + "\\}", "'" + param.getValue() + "'");
        } else {
            sql = sql.replaceAll("\\$\\{" + param.getName() + "\\}", param.getValue());
        }
        return sql;
    }


    /**
     * 去除字符串中某两个字符串之间的内容
     *
     * @param body 字符串整体
     * @param str1 起始字符串
     * @param str2 结束字符串
     */
    public static String subRangeString(String body, String str1, String str2) {
        while (true) {
            int index1 = body.indexOf(str1);
            if (index1 != -1) {
                int index2 = body.indexOf(str2, index1);
                if (index2 != -1) {
                    body = body.substring(0, index1) + body.substring(index2 + str2.length());
                } else {
                    return body;
                }
            } else {
                return body;
            }
        }
    }


    /**
     * 获取sql语句中的表名
     *
     * @param sql
     * @return
     */
    public static List<String> getTableNames(String sql, String datasourceType) {
        DbType jdbcType = translateDbType(datasourceType);
        if (jdbcType == null) {
            return null;
        }
        List<String> tableNames = new ArrayList<>();
        try {
            List<SQLStatement> stmts = SQLUtils.parseStatements(sql, jdbcType);
            String database = "";
            for (SQLStatement stmt : stmts) {
                SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(jdbcType);
                if (stmt instanceof SQLUseStatement) {
                    database = ((SQLUseStatement) stmt).getDatabase().getSimpleName().toUpperCase();
                }
                stmt.accept(statVisitor);
                Map<TableStat.Name, TableStat> tables = statVisitor.getTables();
                if (tables != null) {
                    final String db = database;
                    tables.forEach((tableName, stat) -> {
                        if (stat.getSelectCount() > 0) {
                            String from = tableName.getName();
                            tableNames.add(from);
                        }
                    });
                }
            }
        } catch (Exception e) {
            log.error("获取sql语句中的表名异常");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return tableNames;
    }

    /**
     * 获取sql语句中的最终查询的字段名
     *
     * @param sql
     * @param datasourceType
     * @return
     */
    public static List<String> getColumns(String sql, String datasourceType) {
        DbType jdbcType = translateDbType(datasourceType);
        if (jdbcType == null) {
            return null;
        }
        List<String> columns = new ArrayList<>();
        try {
            List<SQLStatement> statementList = SQLUtils.parseStatements(sql, jdbcType);
            for (SQLStatement statement : statementList) {
                if (!(statement instanceof SQLSelectStatement)) {
                    continue;
                }
                SQLASTVisitorAdapter visitor = new SchemaStatVisitor(jdbcType);
                if (jdbcType.equals(JdbcConstants.ORACLE)) {
                    // oracle需要使用OracleSchemaStatVisitor
                    visitor = new OracleSchemaStatVisitor();
                }
                statement.accept(visitor);
                SQLSelectStatement selectStatement = (SQLSelectStatement) statement;
                SQLSelectQueryBlock queryBlock = selectStatement.getSelect().getFirstQueryBlock();
                // 查询字段
                List<SQLSelectItem> selectList = queryBlock.getSelectList();
                if (selectList == null || selectList.isEmpty()) {
                    continue;
                }
                for (SQLSelectItem selectItem : selectList) {
                    if (StringUtils.isNotBlank(selectItem.getAlias())) {
                        columns.add(selectItem.getAlias());
                        continue;
                    }
                    String outStr = selectItem.toString().trim();
                    if (!outStr.contains(".")) {
                        columns.add(outStr);
                        continue;
                    }
                    // 去除字段前面的 表的别名 MYSQL、CLICKHOUSE使用反引号，ORACLE、POSTGRESQL使用双引号 标识特殊字段
                    String quote = null;
                    if (jdbcType.equals(JdbcConstants.MYSQL) || jdbcType.equals(JdbcConstants.CLICKHOUSE)) {
                        quote = "`";
                    } else if (jdbcType.equals(JdbcConstants.ORACLE) || jdbcType.equals(JdbcConstants.POSTGRESQL)) {
                        quote = "\"";
                    }
                    List<Integer> indexList = new ArrayList<>();
                    if (quote != null && outStr.contains(quote)) {
                        Stack<Character> stack = new Stack<>();
                        for (int i = 0; i < outStr.length(); i++) {
                            char c = outStr.charAt(i);
                            // 如果是`，则入栈
                            if (c == '`') {
                                if (!stack.isEmpty()) {
                                    stack.pop();
                                } else {
                                    stack.push(c);
                                }
                            }
                            // 如果是点，且栈空，则记录下标
                            if (c == '.' && stack.isEmpty()) {
                                indexList.add(i);
                            }
                        }
                        // 如果没有符合的点，说明没有表的别名，直接返回
                        if (indexList.isEmpty()) {
                            columns.add(outStr);
                            continue;
                        }
                    } else {
                        indexList.add(outStr.lastIndexOf("."));
                    }
                    // 最后一个点后面的字符串
                    int index = indexList.get(indexList.size() - 1);
                    columns.add(outStr.substring(index + 1));
                }
            }
        } catch (Exception e) {
            log.error("获取sql语句中的最终查询的字段名异常");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return columns;
    }


    /**
     * 将数据集插件定义的数据库类型转换为druid数据库类型
     *
     * @param datasourceType
     * @return
     */
    public static DbType translateDbType(String datasourceType) {
        DbType jdbcType;
        switch (datasourceType.toLowerCase()) {
            case DatasetConstant.DatasourceType.MYSQL:
                jdbcType = JdbcConstants.MYSQL;
                break;
            case DatasetConstant.DatasourceType.ORACLE:
                jdbcType = JdbcConstants.ORACLE;
                break;
            case DatasetConstant.DatasourceType.POSTGRESQL:
                jdbcType = JdbcConstants.POSTGRESQL;
                break;
            case DatasetConstant.DatasourceType.CLICKHOUSE:
                jdbcType = JdbcConstants.CLICKHOUSE;
                break;
            default:
                return null;
        }
        return jdbcType;
    }


    /**
     * 检查SQL语句，只允许存在select语句
     *
     * @param sql
     * @param datasourceType
     * @return
     */
    public static boolean onlySelectSql(String sql, String datasourceType) {
        DbType jdbcType;
        switch (datasourceType.toLowerCase()) {
            case DatasetConstant.DatasourceType.MYSQL:
                jdbcType = JdbcConstants.MYSQL;
                break;
            case DatasetConstant.DatasourceType.ORACLE:
                jdbcType = JdbcConstants.ORACLE;
                break;
            case DatasetConstant.DatasourceType.POSTGRESQL:
                jdbcType = JdbcConstants.POSTGRESQL;
                break;
            case DatasetConstant.DatasourceType.CLICKHOUSE:
                jdbcType = JdbcConstants.CLICKHOUSE;
                break;
            case DatasetConstant.DatasourceType.SQLSERVER:
                jdbcType = JdbcConstants.SQL_SERVER;
                break;
            default:
                // 对于其他类型的数据源，暂不做校验
                return true;
        }
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, jdbcType);
        for (SQLStatement stmt : stmts) {
            if (!(stmt instanceof SQLSelectStatement)) {
                return false;
            }
        }
        return true;
    }

}