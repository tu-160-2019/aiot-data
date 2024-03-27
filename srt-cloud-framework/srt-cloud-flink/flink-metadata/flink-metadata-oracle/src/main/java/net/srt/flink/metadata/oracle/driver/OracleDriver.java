/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.srt.flink.metadata.oracle.driver;

import com.alibaba.druid.pool.DruidDataSource;
import net.srt.flink.common.assertion.Asserts;
import net.srt.flink.common.model.Column;
import net.srt.flink.common.model.QueryData;
import net.srt.flink.common.model.Table;
import net.srt.flink.metadata.base.convert.ITypeConvert;
import net.srt.flink.metadata.base.driver.AbstractJdbcDriver;
import net.srt.flink.metadata.base.driver.DriverConfig;
import net.srt.flink.metadata.base.query.IDBQuery;
import net.srt.flink.metadata.oracle.convert.OracleTypeConvert;
import net.srt.flink.metadata.oracle.query.OracleQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OracleDriver
 *
 * @author zrx
 * @since 2021/7/21 15:52
 **/
public class OracleDriver extends AbstractJdbcDriver {

    @Override
    public String getDriverClass() {
        return "oracle.jdbc.driver.OracleDriver";
    }

    @Override
    public IDBQuery getDBQuery() {
        return new OracleQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new OracleTypeConvert();
    }

    @Override
    public String getType() {
        return "Oracle";
    }

    @Override
    public String getName() {
        return "Oracle数据库";
    }

    /**
     * oracel sql拼接，目前还未实现limit方法
     * */
    @Override
    public StringBuilder genQueryOption(QueryData queryData) {

        String where = queryData.getOption().getWhere();
        String order = queryData.getOption().getOrder();

        StringBuilder optionBuilder = new StringBuilder()
                .append("select * from ")
                .append(queryData.getSchemaName())
                .append(".")
                .append(queryData.getTableName());

        if (where != null && !where.equals("")) {
            optionBuilder.append(" where ").append(where);
        }
        if (order != null && !order.equals("")) {
            optionBuilder.append(" order by ").append(order);
        }

        return optionBuilder;
    }

    @Override
    public String getCreateTableSql(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(table.getName() + " (");
        List<Column> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(columns.get(i).getName() + " " + getTypeConvert().convertToDB(columns.get(i)));
            if (columns.get(i).isNullable()) {
                sb.append(" NOT NULL");
            }
        }
        sb.append(");");
        sb.append("\r\n");
        List<Column> pks = columns.stream().filter(column -> column.isKeyFlag()).collect(Collectors.toList());
        if (Asserts.isNotNullCollection(pks)) {
            sb.append("ALTER TABLE " + table.getName() + " ADD CONSTRAINT " + table.getName() + "_PK PRIMARY KEY (");
            for (int i = 0; i < pks.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(pks.get(i).getName());
            }
            sb.append(");\r\n");
        }
        for (int i = 0; i < columns.size(); i++) {
            sb.append("COMMENT ON COLUMN " + table.getName() + "." + columns.get(i).getName() + " IS '" + columns.get(i).getComment() + "';");
        }
        return sb.toString();
    }

    @Override
    public Map<String, String> getFlinkColumnTypeConversion() {
        return new HashMap<>();
    }

    @Override
    protected void createDataSource(DruidDataSource ds, DriverConfig config) {
        ds.setName(config.getName().replaceAll(":", ""));
        ds.setUrl(config.getUrl());
        ds.setDriverClassName(getDriverClass());
        ds.setUsername(config.getUsername());
        ds.setPassword(config.getPassword());
        ds.setValidationQuery("select 1 from dual");
        ds.setTestWhileIdle(true);
        ds.setBreakAfterAcquireFailure(true);
        ds.setFailFast(true);
        ds.setInitialSize(1);
        ds.setMaxActive(8);
        ds.setMinIdle(5);
    }
}
