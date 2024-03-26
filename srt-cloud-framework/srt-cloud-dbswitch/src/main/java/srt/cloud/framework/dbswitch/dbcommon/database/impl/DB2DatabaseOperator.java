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

import srt.cloud.framework.dbswitch.dbcommon.database.AbstractDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.database.IDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.domain.StatementResultSet;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * DB2数据库实现类
 *
 * @author jrl
 */
public class DB2DatabaseOperator extends AbstractDatabaseOperator implements IDatabaseOperator {

	public DB2DatabaseOperator(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public StatementResultSet queryTableData(String sql, List<String> fields, List<String> orders) {
		sql = String.format("select \"%s\" from (%s) t order by \"%s\" asc ",
				StringUtils.join(fields, "\",\""), sql,
				StringUtils.join(orders, "\",\""));
		return this.selectTableData(sql, this.fetchSize);
	}

	@Override
	public StatementResultSet queryTableData(String sql, List<String> fields) {
		sql = String.format("select \"%s\" from (%s) t",
				StringUtils.join(fields, "\",\""), sql);
		return this.selectTableData(sql, this.fetchSize);
	}

	@Override
	public String getSelectTableSql(String schemaName, String tableName, List<String> fields) {
		return String.format("select \"%s\" from \"%s\".\"%s\" ",
				StringUtils.join(fields, "\",\""), schemaName, tableName);
	}

	@Override
	public StatementResultSet queryTableData(String schemaName, String tableName, List<String> fields,
											 List<String> orders) {
		String sql = String.format("select \"%s\" from \"%s\".\"%s\" order by \"%s\" asc ",
				StringUtils.join(fields, "\",\""), schemaName, tableName,
				StringUtils.join(orders, "\",\""));
		return this.selectTableData(sql, this.fetchSize);
	}

	@Override
	public StatementResultSet queryTableData(String schemaName, String tableName,
											 List<String> fields) {
		String sql = String.format("select \"%s\" from \"%s\".\"%s\" ",
				StringUtils.join(fields, "\",\""), schemaName, tableName);
		return this.selectTableData(sql, this.fetchSize);
	}

	@Override
	public void truncateTableData(String schemaName, String tableName) {
		String sql = String.format("TRUNCATE TABLE \"%s\".\"%s\" immediate ", schemaName, tableName);
		this.executeSql(sql);
	}

	@Override
	public void dropTable(String schemaName, String tableName) {
		String sql = String.format("DROP TABLE \"%s\".\"%s\" ", schemaName, tableName);
		this.executeSql(sql);
	}

}
