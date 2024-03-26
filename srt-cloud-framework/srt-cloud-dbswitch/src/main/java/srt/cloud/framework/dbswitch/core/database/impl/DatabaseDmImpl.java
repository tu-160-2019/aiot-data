// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.core.database.impl;

import srt.cloud.framework.dbswitch.common.constant.Const;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.core.database.AbstractDatabase;
import srt.cloud.framework.dbswitch.core.database.IDatabaseInterface;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.ColumnMetaData;
import srt.cloud.framework.dbswitch.core.model.TableDescription;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 支持DM数据库的元信息实现
 *
 * @author jrl
 */
public class DatabaseDmImpl extends AbstractDatabase implements IDatabaseInterface {

	private static final String SHOW_CREATE_TABLE_SQL =
			"SELECT DBMS_METADATA.GET_DDL('TABLE','%s','%s') FROM DUAL ";
	private static final String SHOW_CREATE_VIEW_SQL =
			"SELECT DBMS_METADATA.GET_DDL('VIEW','%s','%s') FROM DUAL ";

	public DatabaseDmImpl() {
		super(ProductTypeEnum.DM.getDriveClassName());
	}

	@Override
	public ProductTypeEnum getDatabaseType() {
		return ProductTypeEnum.DM;
	}

	@Override
	public String getTableDDL(Connection connection, String schemaName, String tableName) {
		String sql = String.format(SHOW_CREATE_TABLE_SQL, tableName, schemaName);
		try (Statement st = connection.createStatement()) {
			if (st.execute(sql)) {
				try (ResultSet rs = st.getResultSet()) {
					if (rs != null && rs.next()) {
						return rs.getString(1);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return null;
	}

	@Override
	public String getViewDDL(Connection connection, String schemaName, String tableName) {
		String sql = String.format(SHOW_CREATE_VIEW_SQL, tableName, schemaName);
		try (Statement st = connection.createStatement()) {
			if (st.execute(sql)) {
				try (ResultSet rs = st.getResultSet()) {
					if (rs != null && rs.next()) {
						return rs.getString(1);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return null;
	}

	@Override
	public List<ColumnDescription> querySelectSqlColumnMeta(Connection connection, String sql) {
		String querySQL = String.format(
				"SELECT * from (%s) tmp where ROWNUM<=1 ",
				sql.replace(";", ""));
		return this.getSelectSqlColumnMeta(connection, querySQL);
	}

	@Override
	protected String getTableFieldsQuerySQL(String schemaName, String tableName) {
		return String.format("SELECT * FROM \"%s\".\"%s\"  ", schemaName, tableName);
	}

	@Override
	protected String getTestQuerySQL(String sql) {
		return String.format("explain %s", sql.replace(";", ""));
	}

	@Override
	protected String getDefaultValueSql(String schemaName, String tableName) {
		return null;
	}

	@Override
	public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc,
									 boolean addCr, boolean withRemarks) {
		String fieldname = v.getName();
		int length = v.getLength();
		int precision = v.getPrecision();

		StringBuilder retval = new StringBuilder(128);
		retval.append(" \"").append(fieldname).append("\"    ");
		boolean canHaveDefaultValue = true;
		int type = v.getType();
		switch (type) {
			case ColumnMetaData.TYPE_TIMESTAMP:
			case ColumnMetaData.TYPE_TIME:
				retval.append("TIMESTAMP");
				break;
			case ColumnMetaData.TYPE_DATE:
				retval.append("DATE");
				break;
			case ColumnMetaData.TYPE_BOOLEAN:
				retval.append("BIT");
				break;
			case ColumnMetaData.TYPE_NUMBER:
			case ColumnMetaData.TYPE_BIGNUMBER:
				if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
					retval.append("BIGINT");
				} else {
					retval.append("NUMERIC");
					if (length > 0) {
						if (length > 38) {
							length = 38;
						}

						retval.append('(').append(length);
						if (precision > 0) {
							retval.append(", ").append(precision);
						}
						retval.append(')');
					}
				}
				break;
			case ColumnMetaData.TYPE_INTEGER:
				retval.append("BIGINT");
				break;
			case ColumnMetaData.TYPE_STRING:
				if (null != pks && pks.contains(fieldname)) {
					retval.append("VARCHAR(").append(length).append(")");
				} else if (length < 2048) {
					retval.append("VARCHAR(").append(length).append(")");
				} else {
					retval.append("TEXT");
				}
				break;
			case ColumnMetaData.TYPE_BINARY:
				retval.append("BLOB");
				canHaveDefaultValue = false;
				break;
			default:
				retval.append("CLOB");
				canHaveDefaultValue = false;
				break;
		}

		if (canHaveDefaultValue && v.getDefaultValue() != null && !"null".equals(v.getDefaultValue()) && !"NULL".equals(v.getDefaultValue())) {
			if (type != ColumnMetaData.TYPE_TIMESTAMP && type != ColumnMetaData.TYPE_TIME && type != ColumnMetaData.TYPE_DATE) {
				if (v.getDefaultValue().startsWith("'")) {
					retval.append(" DEFAULT ").append(v.getDefaultValue());
				} else {
					retval.append(" DEFAULT '").append(v.getDefaultValue()).append("'");
				}
			} /*else {
				retval += " DEFAULT DEFAULT SYSDATE";
			}*/
		}

		if (!v.isNullable()) {
			retval.append(" NOT NULL");
		}

		if (addCr) {
			retval.append(Const.CR);
		}

		return retval.toString();
	}

	@Override
	public List<String> getTableColumnCommentDefinition(TableDescription td,
														List<ColumnDescription> cds) {
		List<String> results = new ArrayList<>();
		if (StringUtils.isNotBlank(td.getRemarks())) {
			results.add(String
					.format("COMMENT ON TABLE \"%s\".\"%s\" IS '%s' ",
							td.getSchemaName(), td.getTableName(),
							td.getRemarks().replace("\"", "\\\"")));
		}

		for (ColumnDescription cd : cds) {
			if (StringUtils.isNotBlank(cd.getRemarks())) {
				results.add(String
						.format("COMMENT ON COLUMN \"%s\".\"%s\".\"%s\" IS '%s' ",
								td.getSchemaName(), td.getTableName(), cd.getFieldName(),
								cd.getRemarks().replace("\"", "\\\"")));
			}
		}

		return results;
	}

	@Override
	public boolean canCreateIndex(ColumnMetaData v) {
		int length = v.getLength();
		int type = v.getType();
		boolean canCreateIndex = true;
		switch (type) {
			case ColumnMetaData.TYPE_TIMESTAMP:
			case ColumnMetaData.TYPE_TIME:
			case ColumnMetaData.TYPE_DATE:
			case ColumnMetaData.TYPE_BOOLEAN:
			case ColumnMetaData.TYPE_NUMBER:
			case ColumnMetaData.TYPE_BIGNUMBER:
			case ColumnMetaData.TYPE_INTEGER:
			case ColumnMetaData.TYPE_STRING:
				if (length >= 2048) {
					canCreateIndex = false;
				}
				break;
			case ColumnMetaData.TYPE_BINARY:
				canCreateIndex = false;
				break;
			default:
				canCreateIndex = false;
				break;
		}

		return canCreateIndex;
	}

}
