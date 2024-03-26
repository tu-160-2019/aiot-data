// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbcommon.database.impl;

import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.dbcommon.constant.Constants;
import srt.cloud.framework.dbswitch.dbcommon.database.AbstractDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.database.IDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.domain.StatementResultSet;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MySQL数据库实现类
 *
 * @author jrl
 */
public class MongoDatabaseOperator extends AbstractDatabaseOperator implements IDatabaseOperator {

	public MongoDatabaseOperator(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public int getFetchSize() {
		return 0;
	}

	@Override
	public StatementResultSet queryTableData(String sql, List<String> fields, List<String> orders) {
		return getStatementResultSet(sql);
	}

	@Override
	public StatementResultSet queryTableData(String sql, List<String> fields) {
		return getStatementResultSet(sql);
	}

	@Override
	public void setFetchSize(int size) {
	}

	@Override
	public String getSelectTableSql(String schemaName, String tableName, List<String> fields) {
		return String.format("%s.getCollection('%s').find()", schemaName, tableName);
	}

	@Override
	public StatementResultSet queryTableData(String schemaName, String tableName, List<String> fields,
											 List<String> orders) {
		String sql = String.format("%s.getCollection('%s').find({},{ %s }).sort({ %s })",
				schemaName, tableName, fields.stream().map(s -> String.format("'%s' : 1", s))
						.collect(Collectors.joining(",")), orders.stream().map(s -> String.format("'%s' : 1", s))
						.collect(Collectors.joining(",")));
		return getStatementResultSet(sql);
	}

	@Override
	public StatementResultSet queryTableData(String schemaName, String tableName, List<String> fields) {
		String sql = String.format("%s.getCollection('%s').find({},{ %s })",
				schemaName, tableName, fields.stream().map(s -> String.format("'%s' : 1", s))
						.collect(Collectors.joining(",")));
		return getStatementResultSet(sql);
	}

	@Override
	public void truncateTableData(String schemaName, String tableName) {
		cleanup(schemaName, tableName);
	}

	@Override
	public void dropTable(String schemaName, String tableName) {
		cleanup(schemaName, tableName);
	}


	private void cleanup(String schemaName, String tableName) {
		String sql = String.format("%s.getCollection('%s').drop();", schemaName, tableName);
		this.executeSql(sql);
	}

	private StatementResultSet getStatementResultSet(String sql) {
		try {
			Connection connection = this.dataSource.getConnection();
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(Constants.DEFAULT_QUERY_TIMEOUT_SECONDS);
			StatementResultSet srs = new StatementResultSet();
			srs.setConnection(connection);
			srs.setStatement(statement);
			srs.setResultset(statement.executeQuery(sql));
			return srs;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

}
