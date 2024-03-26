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
import srt.cloud.framework.dbswitch.core.database.constant.SQLServerConst;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.ColumnMetaData;
import srt.cloud.framework.dbswitch.core.model.TableDescription;
import srt.cloud.framework.dbswitch.core.util.DDLFormatterUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 支持SQLServer数据库的元信息实现
 *
 * @author jrl
 */
public class DatabaseSqlserverImpl extends AbstractDatabase implements IDatabaseInterface {

	private static final String SHOW_CREATE_VIEW_SQL =
			"SELECT VIEW_DEFINITION from INFORMATION_SCHEMA.VIEWS where TABLE_SCHEMA ='%s' and TABLE_NAME ='%s'";

	private static Set<String> excludesSchemaNames;

	static {
		excludesSchemaNames = new HashSet<>();
		excludesSchemaNames.add("db_denydatawriter");
		excludesSchemaNames.add("db_datawriter");
		excludesSchemaNames.add("db_accessadmin");
		excludesSchemaNames.add("db_ddladmin");
		excludesSchemaNames.add("db_securityadmin");
		excludesSchemaNames.add("db_denydatareader");
		excludesSchemaNames.add("db_backupoperator");
		excludesSchemaNames.add("db_datareader");
		excludesSchemaNames.add("db_owner");
	}

	public DatabaseSqlserverImpl() {
		super(ProductTypeEnum.SQLSERVER.getDriveClassName());
	}

	public DatabaseSqlserverImpl(String driverName) {
		super(driverName);
	}

	@Override
	public ProductTypeEnum getDatabaseType() {
		return ProductTypeEnum.SQLSERVER;
	}

	private int getDatabaseMajorVersion(Connection connection) {
		try {
			return connection.getMetaData().getDatabaseMajorVersion();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> querySchemaList(Connection connection) {
		Set<String> ret = new HashSet<>();
		try (ResultSet schemas = connection.getMetaData().getSchemas();) {
			while (schemas.next()) {
				String name = schemas.getString("TABLE_SCHEM");
				if (!excludesSchemaNames.contains(name)) {
					ret.add(name);
				}
			}
			return new ArrayList<>(ret);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<TableDescription> queryTableList(Connection connection, String schemaName) {
		int majorVersion = getDatabaseMajorVersion(connection);
		if (majorVersion <= 8) {
			return super.queryTableList(connection, schemaName);
		}

		List<TableDescription> ret = new ArrayList<>();
		String sql = String.format(
				"SELECT DISTINCT t.TABLE_SCHEMA as TABLE_SCHEMA, t.TABLE_NAME as TABLE_NAME, t.TABLE_TYPE as TABLE_TYPE, CONVERT(nvarchar(50),ISNULL(g.[value], '')) as COMMENTS \r\n"
						+ "FROM INFORMATION_SCHEMA.TABLES t LEFT JOIN sysobjects d on t.TABLE_NAME = d.name \r\n"
						+ "LEFT JOIN sys.extended_properties g on g.major_id=d.id and g.minor_id='0' where t.TABLE_SCHEMA='%s'",
				schemaName);
		try (PreparedStatement ps = connection.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery();) {
			while (rs.next()) {
				TableDescription td = new TableDescription();
				td.setSchemaName(rs.getString("TABLE_SCHEMA"));
				td.setTableName(rs.getString("TABLE_NAME"));
				td.setRemarks(rs.getString("COMMENTS"));
				String tableType = rs.getString("TABLE_TYPE").trim();
				if (tableType.equalsIgnoreCase("VIEW")) {
					td.setTableType("VIEW");
				} else {
					td.setTableType("TABLE");
				}

				ret.add(td);
			}

			return ret;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected String getDefaultValueSql(String schemaName, String tableName) {
		return "select columns.name AS column_name, columns.column_id AS COLPOSITION, columns.max_length AS DATALENGTH, columns.precision AS DATAPRECISION, columns.scale AS DATASCALE, " +
				"columns.is_nullable AS NULLABLE, types.name AS DATATYPE, CAST(ep.value  AS NVARCHAR(128)) AS column_comment, e.text AS column_default, " +
				"(select top 1 ind.is_primary_key from sys.index_columns ic left join sys.indexes ind on ic.object_id = ind.object_id and ic.index_id = ind.index_id and ind.name like 'PK_%' where ic.object_id=columns.object_id and ic.column_id=columns.column_id) AS COLKEY " +
				"from sys.columns columns LEFT JOIN sys.types types ON columns.system_type_id = types.system_type_id " +
				"LEFT JOIN syscomments e ON columns.default_object_id= e.id " +
				"LEFT JOIN sys.extended_properties ep ON ep.major_id = columns.object_id AND ep.minor_id = columns.column_id AND ep.name = 'MS_Description' " +
				"where columns.object_id = object_id('" + tableName + "') order by columns.column_id ";
	}

	@Override
	public String getTableDDL(Connection connection, String schemaName, String tableName) {
		String sql = String.format(SQLServerConst.CREATE_TABLE_SQL_TPL, schemaName, tableName);
		try (Statement st = connection.createStatement()) {
			if (st.execute(sql)) {
				try (ResultSet rs = st.getResultSet()) {
					if (rs != null && rs.next()) {
						return DDLFormatterUtils.format(rs.getString(1));
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
	public List<ColumnDescription> queryTableColumnMeta(Connection connection, String schemaName,
														String tableName) {
		int majorVersion = getDatabaseMajorVersion(connection);
		if (majorVersion <= 8) {
			return super.queryTableColumnMeta(connection, schemaName, tableName);
		}

		String sql = this.getTableFieldsQuerySQL(schemaName, tableName);
		List<ColumnDescription> ret = this.querySelectSqlColumnMeta(connection, sql);
		String querySql = String.format(
				"SELECT a.name AS COLUMN_NAME,CONVERT(nvarchar(50),ISNULL(g.[value], '')) AS REMARKS FROM sys.columns a\r\n"
						+ "LEFT JOIN sys.extended_properties g ON ( a.object_id = g.major_id AND g.minor_id = a.column_id )\r\n"
						+ "WHERE object_id = (SELECT top 1 object_id FROM sys.tables st INNER JOIN INFORMATION_SCHEMA.TABLES t on st.name=t.TABLE_NAME\r\n"
						+ "WHERE	st.name = '%s' and t.TABLE_SCHEMA='%s')",
				tableName, schemaName);
		try (PreparedStatement ps = connection.prepareStatement(querySql);
			 ResultSet rs = ps.executeQuery();) {
			while (rs.next()) {
				String columnName = rs.getString("COLUMN_NAME");
				String remarks = rs.getString("REMARKS");
				for (ColumnDescription cd : ret) {
					if (columnName.equalsIgnoreCase(cd.getFieldName())) {
						cd.setRemarks(remarks);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public List<ColumnDescription> querySelectSqlColumnMeta(Connection connection, String sql) {
		String querySQL = String.format("SELECT TOP 1 * from (%s) tmp ", sql.replace(";", ""));
		return this.getSelectSqlColumnMeta(connection, querySQL);
	}

	@Override
	protected String getTableFieldsQuerySQL(String schemaName, String tableName) {
		return String.format("select top 1 * from [%s].[%s] ", schemaName, tableName);
	}

	@Override
	protected String getTestQuerySQL(String sql) {
		return String.format("SELECT top 1 * from ( %s ) tmp", sql.replace(";", ""));
	}

	@Override
	public String getQuotedSchemaTableCombination(String schemaName, String tableName) {
		return String.format("  [%s].[%s] ", schemaName, tableName);
	}

	@Override
	public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc,
									 boolean addCr, boolean withRemarks) {
		String fieldname = v.getName();
		int length = v.getLength();
		int precision = v.getPrecision();
		int type = v.getType();

		String retval = " [" + fieldname + "]  ";
		boolean canHaveDefaultValue = true;
		switch (type) {
			case ColumnMetaData.TYPE_TIMESTAMP:
				retval += "DATETIME";
				break;
			case ColumnMetaData.TYPE_TIME:
				retval += "TIME";
				break;
			case ColumnMetaData.TYPE_DATE:
				retval += "DATE";
				break;
			case ColumnMetaData.TYPE_BOOLEAN:
				retval += "BIT";
				break;
			case ColumnMetaData.TYPE_NUMBER:
			case ColumnMetaData.TYPE_INTEGER:
			case ColumnMetaData.TYPE_BIGNUMBER:
				if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
					if (useAutoInc) {
						retval += "BIGINT IDENTITY(0,1)";
					} else {
						retval += "BIGINT";
					}
				} else {
					if (precision == 0) {
						if (length > 18) {
							retval += "DECIMAL(" + length + ",0)";
						} else {
							if (length > 9) {
								retval += "BIGINT";
							} else {
								retval += "INT";
							}
						}
					} else {
						if (precision > 0 && length > 0) {
							retval += "DECIMAL(" + length + "," + precision + ")";
						} else {
							retval += "FLOAT(53)";
						}
					}
				}
				break;
			case ColumnMetaData.TYPE_STRING:
				if (length < 8000) {
					// Maybe use some default DB String length in case length<=0
					if (length > 0) {
						// VARCHAR(n)最多能存n个字节，一个中文是两个字节。
						length = 2 * length;
						if (length > 8000) {
							length = 8000;
						}
						retval += "VARCHAR(" + length + ")";
					} else {
						retval += "VARCHAR(100)";
					}
				} else {
					retval += "TEXT"; // Up to 2bilion characters.
					canHaveDefaultValue = false;
				}
				break;
			case ColumnMetaData.TYPE_BINARY:
				retval += "VARBINARY(MAX)";
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
				retval += " DEFAULT DEFAULT (getdate())";
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
	public String getPrimaryKeyAsString(List<String> pks) {
		if (null != pks && !pks.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(StringUtils.join(pks, "] , ["));
			sb.append("]");
			return sb.toString();
		}

		return "";
	}

	@Override
	public List<String> getTableColumnCommentDefinition(TableDescription td,
														List<ColumnDescription> cds) {
		List<String> results = new ArrayList<>();
		if (StringUtils.isNotBlank(td.getRemarks())) {
			results.add(String
					.format(
							"EXEC [sys].sp_addextendedproperty 'MS_Description', N'%s', 'schema', N'%s', 'table', N'%s' ",
							td.getRemarks().replace("\"", "\\\""), td.getSchemaName(), td.getTableName()));
		}

		for (ColumnDescription cd : cds) {
			if (StringUtils.isNotBlank(cd.getRemarks())) {
				results.add(String
						.format(
								"EXEC [sys].sp_addextendedproperty 'MS_Description', N'%s', 'schema', N'%s', 'table', N'%s', 'column', N'%s' ",
								cd.getRemarks().replace("\"", "\\\""), td.getSchemaName(), td.getTableName(),
								cd.getFieldName()));
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
				if (length >= 8000) {
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

	@Override
	public String getCountMoreThanOneSql(String schemaName, String tableName, List<String> columns) {
		String columnStr = "[" + String.join("],[", columns) + "]";
		return String.format("SELECT %s FROM [%s].[%s] GROUP BY %s HAVING count(*)>1", columnStr, schemaName, tableName, columnStr);
	}

	@Override
	public String getCountOneSql(String schemaName, String tableName, List<String> columns) {
		String columnStr = "[" + String.join("],[", columns) + "]";
		return String.format("SELECT %s FROM [%s].[%s] GROUP BY %s HAVING count(*)=1", columnStr, schemaName, tableName, columnStr);
	}

}
