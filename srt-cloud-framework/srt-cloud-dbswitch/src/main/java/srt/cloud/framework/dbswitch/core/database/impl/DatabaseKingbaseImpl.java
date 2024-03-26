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
import srt.cloud.framework.dbswitch.core.database.constant.PostgresqlConst;
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
 * 支持Kingbase数据库的元信息实现
 *
 * @author jrl
 */
public class DatabaseKingbaseImpl extends AbstractDatabase implements IDatabaseInterface {

	private static final String SHOW_CREATE_VIEW_SQL =
			"SELECT pg_get_viewdef((select pg_class.oid from pg_catalog.pg_class \n"
					+ "join pg_catalog.pg_namespace on pg_class.relnamespace = pg_namespace.oid \n"
					+ "where pg_namespace.nspname='%s' and pg_class.relname ='%s'),true) ";

	public DatabaseKingbaseImpl() {
		super(ProductTypeEnum.KINGBASE.getDriveClassName());
	}

	@Override
	public ProductTypeEnum getDatabaseType() {
		return ProductTypeEnum.KINGBASE;
	}

	@Override
	public String getTableDDL(Connection connection, String schemaName, String tableName) {
		String sql = PostgresqlConst.CREATE_TABLE_SQL_TPL
				.replace(PostgresqlConst.TPL_KEY_SCHEMA, schemaName)
				.replace(PostgresqlConst.TPL_KEY_TABLE, tableName);
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
		String sql = String.format(SHOW_CREATE_VIEW_SQL, schemaName, tableName);
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
		String querySQL = String.format(" %s LIMIT 0 ", sql.replace(";", ""));
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
		int type = v.getType();

		String retval = " \"" + fieldname + "\"   ";
		boolean canHaveDefaultValue = true;
		switch (type) {
			case ColumnMetaData.TYPE_TIMESTAMP:
				retval += "TIMESTAMP";
				break;
			case ColumnMetaData.TYPE_TIME:
				retval += "TIME";
				break;
			case ColumnMetaData.TYPE_DATE:
				retval += "DATE";
				break;
			case ColumnMetaData.TYPE_BOOLEAN:
				retval += "BOOLEAN";
				break;
			case ColumnMetaData.TYPE_NUMBER:
			case ColumnMetaData.TYPE_INTEGER:
			case ColumnMetaData.TYPE_BIGNUMBER:
				if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
					if (useAutoInc) {
						retval += "BIGSERIAL";
					} else {
						retval += "BIGINT";
					}
				} else {
					if (length > 0) {
						if (precision > 0 || length > 18) {
							if ((length + precision) > 0 && precision > 0) {
								// Numeric(Precision, Scale): Precision = total length; Scale = decimal places
								retval += "NUMERIC(" + (length + precision) + ", " + precision + ")";
							} else {
								retval += "DOUBLE PRECISION";
							}
						} else {
							if (length > 9) {
								retval += "BIGINT";
							} else {
								if (length < 5) {
									retval += "SMALLINT";
								} else {
									retval += "INTEGER";
								}
							}
						}

					} else {
						retval += "DOUBLE PRECISION";
					}
				}
				break;
			case ColumnMetaData.TYPE_STRING:
				if (length < 1 || length >= AbstractDatabase.CLOB_LENGTH) {
					retval += "TEXT";
					canHaveDefaultValue = false;
				} else if (length <= 2000) {
					retval += "VARCHAR(" + length + ")";
				} else {
					if (null != pks && pks.contains(fieldname)) {
						retval += "VARCHAR(" + length + ")";
					} else {
						retval += "TEXT";
						canHaveDefaultValue = false;
					}
				}
				break;
			case ColumnMetaData.TYPE_BINARY:
				retval += "BYTEA";
				canHaveDefaultValue = false;
				break;
			default:
				retval += "TEXT";
				canHaveDefaultValue = false;
				break;
		}

		if (canHaveDefaultValue && v.getDefaultValue() != null && !"null".equals(v.getDefaultValue()) && !"NULL".equals(v.getDefaultValue())) {
			if (type != ColumnMetaData.TYPE_TIMESTAMP && type != ColumnMetaData.TYPE_TIME && type != ColumnMetaData.TYPE_DATE) {
				if (v.getDefaultValue().startsWith("'")) {
					retval += " DEFAULT " + v.getDefaultValue();
				} else {
					retval += " DEFAULT '" + v.getDefaultValue() + "'";
				}
			} else {
				retval += " DEFAULT DEFAULT SYSDATE";
			}
		}

		if (!v.isNullable()) {
			retval += " NOT NULL";
		}

		if (addCr) {
			retval += Const.CR;
		}

		return retval;
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
			case ColumnMetaData.TYPE_INTEGER:
			case ColumnMetaData.TYPE_BIGNUMBER:
				break;
			case ColumnMetaData.TYPE_STRING:
				if (length < 1 || length >= AbstractDatabase.CLOB_LENGTH) {
					canCreateIndex = false;
				} else if (length > 2000) {
					canCreateIndex = false;
				}
				break;
			default:
				canCreateIndex = false;
				break;
		}

		return canCreateIndex;
	}

}
