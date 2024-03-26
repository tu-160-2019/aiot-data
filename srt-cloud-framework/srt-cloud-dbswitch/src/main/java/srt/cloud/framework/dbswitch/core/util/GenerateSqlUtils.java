// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.core.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.Constants;
import org.springframework.util.CollectionUtils;
import srt.cloud.framework.dbswitch.common.constant.Const;
import srt.cloud.framework.dbswitch.common.type.DBTableType;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.UuidUtils;
import srt.cloud.framework.dbswitch.core.database.AbstractDatabase;
import srt.cloud.framework.dbswitch.core.database.DatabaseFactory;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.ColumnMetaData;
import srt.cloud.framework.dbswitch.core.model.TableDescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 拼接SQL工具类
 *
 * @author jrl
 */
public final class GenerateSqlUtils {

	private static final boolean HIVE_USE_CTAS = false;

	public static String getDDLCreateTableSQL(
			ProductTypeEnum type,
			List<ColumnDescription> fieldNames,
			List<String> primaryKeys,
			String schemaName,
			String tableName,
			boolean autoIncr) {
		AbstractDatabase db = DatabaseFactory.getDatabaseInstance(type);
		return getDDLCreateTableSQL(
				db,
				fieldNames,
				primaryKeys,
				schemaName,
				tableName,
				false,
				null,
				autoIncr, Collections.emptyMap());
	}

	public static String getDDLCreateTableSQL(
			AbstractDatabase db,
			List<ColumnDescription> fieldNames,
			List<String> primaryKeys,
			String schemaName,
			String tableName,
			boolean withRemarks,
			String tableRemarks,
			boolean autoIncr, Map<String, String> tblProperties) {
		ProductTypeEnum type = db.getDatabaseType();
		StringBuilder sb = new StringBuilder();
		List<String> pks = fieldNames.stream()
				.filter((cd) -> primaryKeys.contains(cd.getFieldName()))
				.map(ColumnDescription::getFieldName)
				.collect(Collectors.toList());

		sb.append(Const.CREATE_TABLE);
		// if(ifNotExist && type!=DatabaseType.ORACLE) {
		// sb.append( Const.IF_NOT_EXISTS );
		// }
		sb.append(db.getQuotedSchemaTableCombination(schemaName, tableName));
		sb.append("(");

		for (int i = 0; i < fieldNames.size(); i++) {
			if (i > 0) {
				sb.append(", ");
			} else {
				sb.append("  ");
			}

			ColumnMetaData v = fieldNames.get(i).getMetaData();
			sb.append(db.getFieldDefinition(v, pks, autoIncr, false, withRemarks));
		}

		if (!pks.isEmpty() && !ProductTypeEnum.DORIS.equals(type) && !type.isLikeHive()) {
			String pk = db.getPrimaryKeyAsString(pks);
			sb.append(", PRIMARY KEY (").append(pk).append(")");
		}

		sb.append(")");
		if (ProductTypeEnum.MYSQL.equals(type)) {
			sb.append("ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin");
			if (withRemarks && StringUtils.isNotBlank(tableRemarks)) {
				sb.append(String.format(" COMMENT='%s' ", tableRemarks.replace("'", "\\'")));
			}
		} else if (ProductTypeEnum.DORIS.equals(type)) {
			if (!pks.isEmpty()) {
				String pk = db.getPrimaryKeyAsString(pks);
				sb.append("unique key(").append(pk).append(")").append(Const.CR);
			}
			if (withRemarks && StringUtils.isNotBlank(tableRemarks)) {
				sb.append(String.format(" COMMENT '%s' ", tableRemarks.replace("'", "\\'")));
				sb.append(Const.CR);
			}
			sb.append(String.format("DISTRIBUTED BY HASH(%s) BUCKETS 10", !pks.isEmpty() ? pks.get(0) : fieldNames.get(0).getFieldName())).append(Const.CR).append("PROPERTIES(\"replication_num\" = \"1\");");
		} else if (type.isLikeHive()) {
			if (null != tblProperties && !tblProperties.isEmpty()) {
				List<String> kvProperties = new ArrayList<>();
				tblProperties.forEach((k, v) -> kvProperties.add(String.format("\t\t'%s' = '%s'", k, v)));
				sb.append(Constants.CRLF);
				sb.append("STORED BY 'org.apache.hive.storage.jdbc.JdbcStorageHandler'");
				sb.append(Constants.CRLF);
				sb.append("TBLPROPERTIES (");
				sb.append(String.join(",\n", kvProperties));
				sb.append(")");
			} else {
				sb.append(Constants.CRLF);
				sb.append("STORED AS ORC");
			}
		} else if (type.isClickHouse()) {
			sb.append("ENGINE=MergeTree");
			if (CollectionUtils.isEmpty(pks)) {
				sb.append(Constants.CRLF);
				sb.append("ORDER BY tuple()");
			}
			if (withRemarks && StringUtils.isNotBlank(tableRemarks)) {
				//sb.append(Constants.CR);
				//sb.append(String.format("COMMENT='%s' ", tableRemarks.replace("'", "\\'")));
			}
		}

		return DDLFormatterUtils.format(sb.toString());
	}

	public static List<String> getDDLCreateTableSQL(
			ProductTypeEnum type,
			List<ColumnDescription> fieldNames,
			List<String> primaryKeys,
			String schemaName,
			String tableName,
			String tableRemarks,
			boolean autoIncr, Map<String, String> tblProperties) {
		AbstractDatabase db = DatabaseFactory.getDatabaseInstance(type);
		if (type.isLikeHive()) {
			List<String> sqlLists = new ArrayList<>();
			String tmpTableName = "tmp_" + UuidUtils.generateUuid();
			String createTableSql = getDDLCreateTableSQL(db, fieldNames, primaryKeys, schemaName,
					tmpTableName, true, tableRemarks, autoIncr, tblProperties);
			sqlLists.add(createTableSql);
			if (HIVE_USE_CTAS) {
				String createAsTableSql = String.format("CREATE TABLE `%s`.`%s` STORED AS ORC AS (SELECT * FROM `%s`.`%s`)",
						schemaName, tableName, schemaName, tmpTableName);
				sqlLists.add(createAsTableSql);
			} else {
				String createAsTableSql = getDDLCreateTableSQL(db, fieldNames, primaryKeys, schemaName,
						tableName, true, tableRemarks, autoIncr, null);
				sqlLists.add(createAsTableSql);
				String selectColumns = fieldNames.stream()
						.map(s -> String.format("`%s`", s.getFieldName()))
						.collect(Collectors.joining(","));
				String insertIntoSql = String.format("INSERT INTO `%s`.`%s` SELECT %s FROM `%s`.`%s`",
						schemaName, tableName, selectColumns, schemaName, tmpTableName);
				sqlLists.add(insertIntoSql);
			}
			String dropTmpTableSql = String.format("DROP TABLE IF EXISTS `%s`.`%s`", schemaName, tmpTableName);
			sqlLists.add(dropTmpTableSql);
			return sqlLists;
		} else if (type.noCommentStatement()) {
			String createTableSql = getDDLCreateTableSQL(db, fieldNames, primaryKeys, schemaName,
					tableName, true, tableRemarks, autoIncr, tblProperties);
			return Lists.newArrayList(createTableSql);
		} else {
			String createTableSql = getDDLCreateTableSQL(db, fieldNames, primaryKeys, schemaName,
					tableName, true, tableRemarks, autoIncr, tblProperties);
			TableDescription td = new TableDescription();
			td.setSchemaName(schemaName);
			td.setTableName(tableName);
			td.setRemarks(tableRemarks);
			td.setTableType(DBTableType.TABLE.name());
			List<String> results = db.getTableColumnCommentDefinition(td, fieldNames);
			if (CollectionUtils.isEmpty(results)) {
				results = Lists.newArrayList(createTableSql);
			} else {
				results.add(0, createTableSql);
			}
			return results;
		}
	}

}
