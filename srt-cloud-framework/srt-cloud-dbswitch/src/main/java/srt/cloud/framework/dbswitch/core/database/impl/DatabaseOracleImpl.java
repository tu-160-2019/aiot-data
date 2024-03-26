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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import srt.cloud.framework.dbswitch.common.constant.Const;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.StringUtil;
import srt.cloud.framework.dbswitch.core.database.AbstractDatabase;
import srt.cloud.framework.dbswitch.core.database.IDatabaseInterface;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.ColumnMetaData;
import srt.cloud.framework.dbswitch.core.model.TableDescription;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 支持Oracle数据库的元信息实现
 * <p>
 * 备注：
 * <p>
 * （1）Oracle12c安装教程：
 * <p>
 * 官方安装版：https://www.w3cschool.cn/oraclejc/oraclejc-vuqx2qqu.html
 * <p>
 * Docker版本：http://www.pianshen.com/article/4448142743/
 * <p>
 * https://www.cnblogs.com/Dev0ps/p/10676930.html
 * <p>
 * (2) Oracle的一个表里至多只能有一个字段为LONG类型
 *
 * @author jrl
 */
@Slf4j
public class DatabaseOracleImpl extends AbstractDatabase implements IDatabaseInterface {

	private static final String SHOW_CREATE_TABLE_SQL =
			"SELECT DBMS_METADATA.GET_DDL('TABLE','%s','%s') FROM DUAL ";
	private static final String SHOW_CREATE_VIEW_SQL =
			"SELECT DBMS_METADATA.GET_DDL('VIEW','%s','%s') FROM DUAL ";

	public DatabaseOracleImpl() {
		super(ProductTypeEnum.ORACLE.getDriveClassName());
	}

	@Override
	public ProductTypeEnum getDatabaseType() {
		return ProductTypeEnum.ORACLE;
	}

	@Override
	public List<TableDescription> queryTableList(Connection connection, String schemaName) {
		List<TableDescription> ret = new ArrayList<>();
		String sql = String.format("SELECT \"OWNER\",\"TABLE_NAME\",\"TABLE_TYPE\",\"COMMENTS\" "
				+ "FROM all_tab_comments where \"OWNER\"='%s'", schemaName);
		try (PreparedStatement ps = connection.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery();) {
			while (rs.next()) {
				TableDescription td = new TableDescription();
				td.setSchemaName(rs.getString("OWNER"));
				td.setTableName(rs.getString("TABLE_NAME"));
				td.setRemarks(rs.getString("COMMENTS"));
				String tableType = rs.getString("TABLE_TYPE").trim();
				if ("VIEW".equalsIgnoreCase(tableType)) {
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
		/*return "select columns.column_name AS \"column_name\", columns.data_type AS DATATYPE, columns.data_length AS DATALENGTH, columns.data_precision AS DATAPRECISION, " +
				"columns.data_scale AS DATASCALE, columns.nullable AS NULLABLE, columns.column_id AS COLPOSITION, columns.data_default AS \"column_default\", comments.comments AS \"column_comment\"," +
				"case when t.column_name is null then 0 else 1 end as COLKEY " +
				"from sys.user_tab_columns columns LEFT JOIN sys.user_col_comments comments ON columns.table_name = comments.table_name AND columns.column_name = comments.column_name " +
				"left join ( " +
				"select col.column_name as column_name, con.table_name as table_name from user_constraints con, user_cons_columns col " +
				"where con.constraint_name = col.constraint_name and con.constraint_type = 'P' " +
				") t on t.table_name = columns.table_name and columns.column_name = t.column_name " +
				"where columns.table_name = '" + tableName + "' order by columns.column_id ";*/
		return "SELECT\n" +
				"  COLUMNS.column_name AS column_name,\n" +
				"  COLUMNS.table_name,\n" +
				"  COLUMNS.data_type AS DATATYPE,\n" +
				"  COLUMNS.data_length AS DATALENGTH,\n" +
				"  COLUMNS.data_precision AS DATAPRECISION,\n" +
				"  COLUMNS.data_scale AS DATASCALE,\n" +
				"  COLUMNS.nullable AS NULLABLE,\n" +
				"  COLUMNS.column_id AS COLPOSITION,\n" +
				"  COLUMNS.data_default AS column_default,\n" +
				"  comments.comments AS column_comment,\n" +
				"  CASE\n" +
				"    WHEN t.column_name IS NULL THEN \n" +
				"\t\t0\n" +
				"    ELSE 1\n" +
				"  END AS COLKEY\n" +
				"FROM\n" +
				"  --sys.user_tab_columns\n" +
				"  all_TAB_COLUMNS\n" +
				"  --COLUMNS LEFT JOIN sys.user_col_comments comments ON COLUMNS.table_name = comments.table_name\n" +
				"  COLUMNS\n" +
				"LEFT JOIN all_col_comments comments ON\n" +
				"  COLUMNS.table_name = comments.table_name\n" +
				"  AND COLUMNS.column_name = comments.column_name\n" +
				"LEFT JOIN (\n" +
				"  SELECT\n" +
				"    col.column_name AS column_name,\n" +
				"    con.table_name AS table_name\n" +
				"  FROM\n" +
				"    ALL_constraints con,\n" +
				"    ALL_cons_columns col\n" +
				"  WHERE\n" +
				"    con.constraint_name = col.constraint_name\n" +
				"    AND con.constraint_type = 'P'\n" +
				") t ON\n" +
				"  t.table_name = COLUMNS.table_name\n" +
				"  AND COLUMNS.column_name = t.column_name\n" +
				"WHERE\n" +
				"  --  COLUMNS.table_name = 'REAL_PEOPLE'\n" +
				"  COLUMNS.OWNER = comments.OWNER\n" +
				"  AND COLUMNS.table_name = '" + tableName + "'\n" +
				"  AND COLUMNS.OWNER = '" + schemaName + "'\n" +
				"ORDER BY\n" +
				"COLUMNS.column_id";
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
	public List<String> queryTablePrimaryKeys(Connection connection, String schemaName,
											  String tableName) {
		// Oracle表的主键可以使用如下命令设置主键是否生效
		// 使主键失效：alter table tableName disable primary key;
		// 使主键恢复：alter table tableName enable primary key;
		Set<String> ret = new HashSet<>();
		String sql = String.format(
				"SELECT col.COLUMN_NAME FROM all_cons_columns col INNER JOIN all_constraints con \n"
						+ "ON col.constraint_name=con.constraint_name AND col.OWNER =con.OWNER  AND col.TABLE_NAME =con.TABLE_NAME \n"
						+ "WHERE con.constraint_type = 'P' and con.STATUS='ENABLED' and con.owner='%s' AND con.table_name='%s'",
				schemaName, tableName);
		try (PreparedStatement ps = connection.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery();
		) {
			while (rs.next()) {
				ret.add(rs.getString("COLUMN_NAME"));
			}

			return new ArrayList<>(ret);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ColumnDescription> querySelectSqlColumnMeta(Connection connection, String sql) {
		String querySQL = String.format("SELECT * from (%s) tmp where ROWNUM<=1 ",
				sql.replace(";", ""));
		return this.getSelectSqlColumnMeta(connection, querySQL);
	}

	@Override
	protected String getTableFieldsQuerySQL(String schemaName, String tableName) {
		return String.format("SELECT * FROM \"%s\".\"%s\" ", schemaName, tableName);
	}

	@Override
	protected String getTestQuerySQL(String sql) {
		return String.format("explain plan for %s", sql.replace(";", ""));
	}

	@Override
	public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc,
									 boolean addCr, boolean withRemarks) {
		String fieldname = v.getName();
		int length = v.getLength();
		int precision = v.getPrecision();

		StringBuilder retval = new StringBuilder(128);
		retval.append(" \"").append(fieldname).append("\"    ");

		int type = v.getType();
		boolean canHaveDefaultValue = true;
		switch (type) {
			case ColumnMetaData.TYPE_TIMESTAMP:
			case ColumnMetaData.TYPE_TIME:
				retval.append("TIMESTAMP");
				break;
			case ColumnMetaData.TYPE_DATE:
				retval.append("DATE");
				break;
			case ColumnMetaData.TYPE_BOOLEAN:
				retval.append("NUMBER(1)");
				break;
			case ColumnMetaData.TYPE_NUMBER:
			case ColumnMetaData.TYPE_BIGNUMBER:
				retval.append("NUMBER");
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
				break;
			case ColumnMetaData.TYPE_INTEGER:
				retval.append("INTEGER");
				break;
			case ColumnMetaData.TYPE_STRING:
				if (length >= AbstractDatabase.CLOB_LENGTH) {
					retval.append("CLOB");
					canHaveDefaultValue = false;
				} else {
					if (length == 1) {
						retval.append("NVARCHAR2(1)");
					} else if (length > 0 && length < 2000) {
						// VARCHAR2(size)，size最大值为4000，单位是字节；而NVARCHAR2(size)，size最大值为2000，单位是字符
						retval.append("NVARCHAR2(").append(length).append(')');
					} else {
						retval.append("CLOB");// We don't know, so we just use the maximum...
						canHaveDefaultValue = false;
					}
				}
				break;
			case ColumnMetaData.TYPE_BINARY: // the BLOB can contain binary data.
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
					retval.append(" DEFAULT").append(v.getDefaultValue());
				} else {
					retval.append(" DEFAULT").append(" '").append(v.getDefaultValue()).append("'");
				}
			} else {
				retval.append(" DEFAULT SYSDATE");
			}
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
				break;
			case ColumnMetaData.TYPE_STRING:
				if (length >= AbstractDatabase.CLOB_LENGTH) {
					canCreateIndex = false;
				} else {
					if (length >= 2000) {
						canCreateIndex = false;
					}
				}
				break;
			default:
				canCreateIndex = false;
				break;
		}

		return canCreateIndex;
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
	public void setIndexSql(String schemaName, String tableName, List<String> results, Map<String, List<ColumnDescription>> columnMap) {
		for (Map.Entry<String, List<ColumnDescription>> entry : columnMap.entrySet()) {
			String indexName = entry.getKey();
			List<ColumnDescription> descriptions = entry.getValue();
			ColumnDescription columnDescription = descriptions.get(0);
			String indexSql;
			String lastIndexName = indexName.length() > 8 ? indexName.substring(0, 8) : indexName;
			if (descriptions.size() > 1) {
				indexSql = "CREATE " + (columnDescription.isNonIndexUnique() ? "" : "UNIQUE")
						+ " INDEX \"" + schemaName + "\".\"" + lastIndexName + "_" + StringUtil.getRandom2(4) + "\" ON \"" + schemaName + "\".\"" + tableName
						+ "\" (\"" + descriptions.stream().map(ColumnDescription::getFieldName).collect(Collectors.joining("\",\"")) + "\") ";
			} else {
				indexSql = "CREATE " + (columnDescription.isNonIndexUnique() ? "" : "UNIQUE")
						+ " INDEX \"" + schemaName + "\".\"" + lastIndexName + "_" + StringUtil.getRandom2(4) + "\" ON \"" + schemaName + "\".\"" + tableName + "\" (\"" + columnDescription.getFieldName() + "\") ";
			}
			results.add(indexSql);
		}
	}

	@Override
	public void setColumnDefaultValue(Connection connection, String schemaName, String tableName, List<ColumnDescription> columnDescriptions) {

		String sql = this.getDefaultValueSql(schemaName, tableName);

		List<ColumnDescription> columns = new ArrayList<>(10);
		try (Statement st = connection.createStatement()) {
			try (ResultSet rs = st.executeQuery(sql)) {
				while (rs.next()) {
					String columnName = rs.getString("column_name");
					String columnComment = rs.getString("column_comment");
					ColumnDescription columnDescription = new ColumnDescription();
					columnDescription.setFieldName(columnName);
					columnDescription.setRemarks(columnComment);
					columns.add(columnDescription);
				}
			}
			//oracle的数据库默认值需要单独查询，否则会报错
			try (ResultSet rs = st.executeQuery(sql)) {
				int i = 0;
				while (rs.next()) {
					String columnDefault = rs.getString("column_default");
					columns.get(i).setDefaultValue(columnDefault);
					i++;
				}
			}
		} catch (SQLException e) {
			log.error(schemaName + "." + tableName + " setColumnDefaultValue error:" + e.getMessage());
			//throw new RuntimeException(e);
		}
		//整合数据
		for (ColumnDescription columnDescription : columnDescriptions) {
			for (ColumnDescription column : columns) {
				if (columnDescription.getFieldName().equals(column.getFieldName())) {
					columnDescription.setDefaultValue(column.getDefaultValue());
					columnDescription.setRemarks(column.getRemarks());
					break;
				}
			}
		}
	}
}
