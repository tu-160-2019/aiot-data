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
import srt.cloud.framework.dbswitch.core.util.DDLFormatterUtils;
import srt.cloud.framework.dbswitch.core.util.PostgresUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 支持PostgreSQL数据库的元信息实现
 *
 * @author jrl
 */
public class DatabasePostgresImpl extends AbstractDatabase implements IDatabaseInterface {

	private static Set<String> systemSchemas = new HashSet<>();

	private static final String SHOW_CREATE_VIEW_SQL_1 =
			"SELECT pg_get_viewdef((select pg_class.relfilenode from pg_catalog.pg_class \n"
					+ "join pg_catalog.pg_namespace on pg_class.relnamespace = pg_namespace.oid \n"
					+ "where pg_namespace.nspname='%s' and pg_class.relname ='%s'),true) ";
	private static final String SHOW_CREATE_VIEW_SQL_2 =
			"select pg_get_viewdef('\"%s\".\"%s\"', true)";

	static {
		systemSchemas.add("pg_aoseg");
		systemSchemas.add("information_schema");
		systemSchemas.add("pg_catalog");
		systemSchemas.add("pg_bitmapindex");
	}

	public DatabasePostgresImpl() {
		super(ProductTypeEnum.POSTGRESQL.getDriveClassName());
	}

	@Override
	public ProductTypeEnum getDatabaseType() {
		return ProductTypeEnum.POSTGRESQL;
	}

	@Override
	public List<String> querySchemaList(Connection connection) {
		List<String> schemas = super.querySchemaList(connection);
		return schemas.stream()
				.filter(s -> !systemSchemas.contains(s))
				.collect(Collectors.toList());
	}

	@Override
	protected String getDefaultValueSql(String schemaName, String tableName) {
		return "select col.column_name AS column_name, col.ordinal_position AS COLPOSITION, col.column_default AS column_default, col.is_nullable AS NULLABLE, col.udt_name AS DATATYPE, " +
				"col.character_maximum_length AS DATALENGTH, col.numeric_precision AS DATAPRECISION, col.numeric_scale AS DATASCALE, des.description AS column_comment, " +
				"case when t.colname is null then 0 else 1 end as COLKEY " +
				"from information_schema.columns col left join pg_description des on col.table_name::regclass = des.objoid and col.ordinal_position = des.objsubid " +
				"left join ( " +
				"select pg_attribute.attname as colname from pg_constraint inner join pg_class on pg_constraint.conrelid = pg_class.oid " +
				"inner join pg_attribute on pg_attribute.attrelid = pg_class.oid and pg_attribute.attnum = any(pg_constraint.conkey) " +
				"where pg_class.relname = '" + tableName + "' and pg_constraint.contype = 'p' " +
				") t on t.colname = col.column_name " +
				"where col.table_schema = '" + schemaName + "' and col.table_name = '" + tableName + "' order by col.ordinal_position ";
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
						return DDLFormatterUtils.format(rs.getString(1));
					}
				}
			}
		} catch (SQLException e) {
			//throw new RuntimeException(e);
		}

		// 低版本的PostgreSQL的表的DDL获取方法
		return PostgresUtils.getTableDDL(connection, schemaName, tableName);
	}

	@Override
	public String getViewDDL(Connection connection, String schemaName, String tableName) {
		String sql = String.format(SHOW_CREATE_VIEW_SQL_1, schemaName, tableName);
		try (Statement st = connection.createStatement()) {
			try {
				if (st.execute(sql)) {
					try (ResultSet rs = st.getResultSet()) {
						if (rs != null && rs.next()) {
							return rs.getString(1);
						}
					}
				}
			} catch (SQLException se) {
				sql = String.format(SHOW_CREATE_VIEW_SQL_2, schemaName, tableName);
				if (st.execute(sql)) {
					try (ResultSet rs = st.getResultSet()) {
						if (rs != null && rs.next()) {
							return rs.getString(1);
						}
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
					if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
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
				retval += " DEFAULT CURRENT_TIMESTAMP";
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
