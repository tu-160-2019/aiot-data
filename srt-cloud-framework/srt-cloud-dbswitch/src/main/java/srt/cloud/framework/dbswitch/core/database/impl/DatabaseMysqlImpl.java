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
import srt.cloud.framework.dbswitch.common.constant.Const;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.StringUtil;
import srt.cloud.framework.dbswitch.core.database.AbstractDatabase;
import srt.cloud.framework.dbswitch.core.database.IDatabaseInterface;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.ColumnMetaData;
import srt.cloud.framework.dbswitch.core.model.TableDescription;
import srt.cloud.framework.dbswitch.core.util.JdbcUrlUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 支持MySQL数据库的元信息实现
 *
 * @author jrl
 */
@Slf4j
public class DatabaseMysqlImpl extends AbstractDatabase implements IDatabaseInterface {

	private static final String SHOW_CREATE_TABLE_SQL = "SHOW CREATE TABLE `%s`.`%s` ";
	private static final String SHOW_CREATE_VIEW_SQL = "SHOW CREATE VIEW `%s`.`%s` ";

	public DatabaseMysqlImpl() {
		super(ProductTypeEnum.MYSQL.getDriveClassName());
	}

	public DatabaseMysqlImpl(String driverClassName) {
		super(driverClassName);
	}

	@Override
	public ProductTypeEnum getDatabaseType() {
		return ProductTypeEnum.MYSQL;
	}

	@Override
	public List<String> querySchemaList(Connection connection) {
		String mysqlJdbcUrl = null;
		try {
			mysqlJdbcUrl = connection.getMetaData().getURL();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		Map<String, String> data = JdbcUrlUtils.findParamsByMySqlJdbcUrl(mysqlJdbcUrl);
		List<String> ret = new ArrayList<String>();
		ret.add(data.get("schema"));
		return ret;
	}

	@Override
	public List<TableDescription> queryTableList(Connection connection, String schemaName) {
		List<TableDescription> ret = new ArrayList<>();
		String sql = String.format("SELECT `TABLE_SCHEMA`,`TABLE_NAME`,`TABLE_TYPE`,`TABLE_COMMENT` "
				+ "FROM `information_schema`.`TABLES` where `TABLE_SCHEMA`='%s'", schemaName);
		try (PreparedStatement ps = connection.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery();) {
			while (rs.next()) {
				TableDescription td = new TableDescription();
				td.setSchemaName(rs.getString("TABLE_SCHEMA"));
				td.setTableName(rs.getString("TABLE_NAME"));
				td.setRemarks(rs.getString("TABLE_COMMENT"));
				String tableType = rs.getString("TABLE_TYPE");
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
		return "select column_name,column_default,column_comment AS column_comment from information_schema.columns where table_schema = '" + schemaName + "' and table_name = '" + tableName + "'";
	}

	@Override
	public void setColumnIndexInfo(Connection connection, String schemaName, String tableName, List<ColumnDescription> columnDescriptions) {
		// 补充一下索引信息
		try (ResultSet indexInfo = connection.getMetaData().getIndexInfo(schemaName, schemaName, tableName, false, true)) {
			setIndex(columnDescriptions, indexInfo);
		} catch (SQLException e) {
			log.error(schemaName + "." + tableName + " setColumnIndexInfo error:" + e.getMessage());
			throw new RuntimeException(schemaName + "." + tableName + " setColumnIndexInfo error!!", e);
		}
	}

	@Override
	public List<String> queryTablePrimaryKeys(Connection connection, String schemaName,
											  String tableName) {
		Set<String> ret = new HashSet<>();
		String sql = "select column_name,ordinal_position AS COLPOSITION, column_default AS DATADEFAULT, is_nullable AS NULLABLE, data_type AS DATATYPE, " +
				"character_maximum_length AS DATALENGTH, numeric_precision AS DATAPRECISION, numeric_scale AS DATASCALE, column_key , column_comment AS COLCOMMENT " +
				"from information_schema.columns where table_schema = '" + schemaName + "' and table_name = '" + tableName + "' and column_key='PRI' order by ordinal_position ";
		try (PreparedStatement ps = connection.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery();
		) {
			while (rs.next()) {
				ret.add(rs.getString("column_name"));
			}

			return new ArrayList<>(ret);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public String getTableDDL(Connection connection, String schemaName, String tableName) {
		String sql = String.format(SHOW_CREATE_TABLE_SQL, schemaName, tableName);
		List<String> result = new ArrayList<>();
		try (Statement st = connection.createStatement()) {
			if (st.execute(sql)) {
				try (ResultSet rs = st.getResultSet()) {
					if (rs != null) {
						while (rs.next()) {
							String value = rs.getString(2);
							Optional.ofNullable(value).ifPresent(result::add);
						}
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return result.stream().findAny().orElse(null);
	}

	@Override
	public String getViewDDL(Connection connection, String schemaName, String tableName) {
		String sql = String.format(SHOW_CREATE_VIEW_SQL, schemaName, tableName);
		List<String> result = new ArrayList<>();
		try (Statement st = connection.createStatement()) {
			if (st.execute(sql)) {
				try (ResultSet rs = st.getResultSet()) {
					if (rs != null) {
						while (rs.next()) {
							String value = rs.getString(2);
							Optional.ofNullable(value).ifPresent(result::add);
						}
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return result.stream().findAny().orElse(null);
	}

	@Override
	public List<ColumnDescription> querySelectSqlColumnMeta(Connection connection, String sql) {
		String querySQL = String.format(" %s LIMIT 0,1", sql.replace(";", ""));
		return this.getSelectSqlColumnMeta(connection, querySQL);
	}

	@Override
	protected String getTableFieldsQuerySQL(String schemaName, String tableName) {
		return String.format("SELECT * FROM `%s`.`%s` ", schemaName, tableName);
	}

	@Override
	protected String getTestQuerySQL(String sql) {
		return String.format("explain %s", sql.replace(";", ""));
	}

	@Override
	public String getQuotedSchemaTableCombination(String schemaName, String tableName) {
		return String.format("  `%s`.`%s` ", schemaName, tableName);
	}

	@Override
	public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc,
									 boolean addCr, boolean withRemarks) {
		String fieldname = v.getName();
		int length = v.getLength();
		int precision = v.getPrecision();
		int type = v.getType();

		String retval = " `" + fieldname + "`  ";
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
				retval += "TINYINT";
				break;
			case ColumnMetaData.TYPE_NUMBER:
			case ColumnMetaData.TYPE_INTEGER:
			case ColumnMetaData.TYPE_BIGNUMBER:
				if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
					if (useAutoInc) {
						retval += "BIGINT AUTO_INCREMENT";
					} else {
						retval += "BIGINT";
					}
				} else {
					// Integer values...
					if (precision == 0) {
						if (length > 11) {
							if (length < 19) {
								// can hold signed values between -9223372036854775808 and 9223372036854775807
								// 18 significant digits
								retval += "BIGINT";
							} else {
								retval += "DECIMAL(" + length + ")";
							}
						} else {
							retval += "INT";
						}
					} else {
						if (length > 0) {
							retval += "DECIMAL(" + length;
						} else {
							retval += "DECIMAL(" + 20;
						}
						if (precision > 0) {
							retval += ", " + precision;
						} else {
							retval += ", " + 10;
						}
						retval += ")";
						/*// Floating point values...
						if (length > 15) {

						} else {
							// A double-precision floating-point number is accurate to approximately 15
							// decimal places.
							// http://mysql.mirrors-r-us.net/doc/refman/5.1/en/numeric-type-overview.html
							retval += "DOUBLE";
						}*/
					}
				}
				break;
			case ColumnMetaData.TYPE_STRING:
				if (length > 0) {
					if (length == 1) {
						retval += "CHAR(1)";
					} else if (length < 1025) {
						retval += "VARCHAR(" + length + ")";
					} else if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
						/*
						 * MySQL5.6中varchar字段为主键时最大长度为254,例如如下的建表语句在MySQL5.7下能通过，但在MySQL5.6下无法通过：
						 *	create table `t_test`(
						 *	`key` varchar(1024) binary,
						 *	`val` varchar(1024) binary,
						 *	primary key(`key`)
						 * );
						 */
						retval += "VARCHAR(254) BINARY";
					} else if (length < 65536) {
						retval += "TEXT";
						canHaveDefaultValue = false;
					} else if (length < 16777216) {
						retval += "MEDIUMTEXT";
						canHaveDefaultValue = false;
					} else {
						retval += "LONGTEXT";
						canHaveDefaultValue = false;
					}
				} else if (length < 0) {
					retval += "TEXT";
				} else {
					retval += "TINYTEXT";
				}
				break;
			case ColumnMetaData.TYPE_BINARY:
				retval += "LONGBLOB";
				canHaveDefaultValue = false;
				break;
			default:
				retval += " LONGTEXT";
				canHaveDefaultValue = false;
				break;
		}

		if (!v.isNullable()) {
			retval += " NOT NULL";
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

		if (withRemarks && StringUtils.isNotBlank(v.getRemarks())) {
			retval += String.format(" COMMENT '%s' ", v.getRemarks().replace("'", "\\'"));
		}

		if (addCr) {
			retval += Const.CR;
		}

		return retval;
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
				if (length > 0) {
					if (length >= 1025) {
						canCreateIndex = false;
					}
				} else {
					canCreateIndex = false;
				}
				break;
			default:
				canCreateIndex = false;
				break;
		}
		return canCreateIndex;
	}

	@Override
	public String getPrimaryKeyAsString(List<String> pks) {
		if (null != pks && !pks.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("`");
			sb.append(StringUtils.join(pks, "` , `"));
			sb.append("`");
			return sb.toString();
		}

		return "";
	}

	@Override
	public List<String> getTableColumnCommentDefinition(TableDescription td,
														List<ColumnDescription> cds) {
		return Collections.emptyList();
	}

	@Override
	public String getCountMoreThanOneSql(String schemaName, String tableName, List<String> columns) {
		String columnStr = "`" + String.join("`,`", columns) + "`";
		return String.format("SELECT %s FROM `%s`.`%s` GROUP BY %s HAVING count(*)>1", columnStr, schemaName, tableName, columnStr);
	}

	@Override
	public String getCountOneSql(String schemaName, String tableName, List<String> columns) {
		String columnStr = "`" + String.join("`,`", columns) + "`";
		return String.format("SELECT %s FROM `%s`.`%s` GROUP BY %s HAVING count(*)=1", columnStr, schemaName, tableName, columnStr);
	}

	@Override
	public void setIndexSql(String schemaName, String tableName, List<String> results, Map<String, List<ColumnDescription>> columnMap) {
		for (Map.Entry<String, List<ColumnDescription>> entry : columnMap.entrySet()) {
			String indexName = entry.getKey();
			List<ColumnDescription> descriptions = entry.getValue();
			ColumnDescription columnDescription = descriptions.get(0);
			String indexSql;
			if (descriptions.size() > 1) {
				indexSql = "CREATE " + (columnDescription.isNonIndexUnique() ? "" : "UNIQUE")
						+ " INDEX `" + indexName + "` ON " + schemaName + "." + tableName
						+ " (" + descriptions.stream().map(ColumnDescription::getFieldName).collect(Collectors.joining(",")) + ") ";
			} else {
				indexSql = "CREATE " + (columnDescription.isNonIndexUnique() ? "" : "UNIQUE")
						+ " INDEX `" + indexName + "` ON " + schemaName + "." + tableName + " (" + columnDescription.getFieldName() + ") ";
			}
			results.add(indexSql);
		}
	}

}
