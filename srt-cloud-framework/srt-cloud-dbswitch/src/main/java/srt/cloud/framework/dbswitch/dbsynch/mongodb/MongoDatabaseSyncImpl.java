// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbsynch.mongodb;

import org.apache.commons.lang3.StringUtils;
import srt.cloud.framework.dbswitch.dbsynch.AbstractDatabaseSynchronize;
import srt.cloud.framework.dbswitch.dbsynch.IDatabaseSynchronize;
import srt.cloud.framework.dbswitch.dbsynch.pgsql.PostgresqlDatabaseSyncImpl;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * kingbase8数据库DML同步实现类
 *
 * @author jrl
 */
public class MongoDatabaseSyncImpl extends AbstractDatabaseSynchronize implements
		IDatabaseSynchronize {

	public MongoDatabaseSyncImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public String getColumnMetaDataSql(String schemaName, String tableName) {
		return String.format("SELECT *  FROM \"%s\".\"%s\"  WHERE 1=2", schemaName, tableName);
	}

	@Override
	public String getInsertPrepareStatementSql(String schemaName, String tableName,
											   List<String> fieldNames) {
		List<String> placeHolders = Collections.nCopies(fieldNames.size(), "?");
		return String.format("INSERT INTO \"%s\".\"%s\" ( \"%s\" ) VALUES ( %s )",
				schemaName, tableName,
				StringUtils.join(fieldNames, "\",\""),
				StringUtils.join(placeHolders, ","));
	}

	@Override
	public String getUpdatePrepareStatementSql(String schemaName, String tableName,
											   List<String> fieldNames, List<String> pks) {
		List<String> uf = fieldNames.stream()
				.filter(field -> !pks.contains(field))
				.map(field -> String.format("\"%s\"=?", field))
				.collect(Collectors.toList());

		List<String> uw = pks.stream()
				.map(pk -> String.format("\"%s\"=?", pk))
				.collect(Collectors.toList());

		return String.format("UPDATE \"%s\".\"%s\" SET %s WHERE %s",
				schemaName, tableName, StringUtils.join(uf, " , "),
				StringUtils.join(uw, " AND "));
	}

	@Override
	public String getDeletePrepareStatementSql(String schemaName, String tableName,
											   List<String> pks) {
		List<String> uw = pks.stream()
				.map(pk -> String.format("\"%s\"=?", pk))
				.collect(Collectors.toList());

		return String.format("DELETE FROM \"%s\".\"%s\" WHERE %s ",
				schemaName, tableName, StringUtils.join(uw, "  AND  "));
	}

	@Override
	public long executeInsert(List<Object[]> records) {
		return 0;
	}

	@Override
	public long executeUpdate(List<Object[]> records) {
		return 0;
	}

	@Override
	public long executeDelete(List<Object[]> records) {
		return 0;
	}

}
