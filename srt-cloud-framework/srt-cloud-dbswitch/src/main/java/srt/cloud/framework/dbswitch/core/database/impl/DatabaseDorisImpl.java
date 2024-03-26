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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 支持MySQL数据库的元信息实现
 *
 * @author jrl
 */
@Slf4j
public class DatabaseDorisImpl extends DatabaseMysqlImpl implements IDatabaseInterface {

	private static final String SHOW_CREATE_TABLE_SQL = "SHOW CREATE TABLE `%s`.`%s` ";
	private static final String SHOW_CREATE_VIEW_SQL = "SHOW CREATE VIEW `%s`.`%s` ";

	public DatabaseDorisImpl() {
		super(ProductTypeEnum.DORIS.getDriveClassName());
	}

	public DatabaseDorisImpl(String driverClassName) {
		super(driverClassName);
	}

	@Override
	public ProductTypeEnum getDatabaseType() {
		return ProductTypeEnum.DORIS;
	}

	@Override
	public List<ColumnDescription> queryTableColumnMeta(Connection connection, String schemaName,
														String tableName) {
		String sql = this.getTableFieldsQuerySQL(schemaName, tableName);
		List<ColumnDescription> columnDescriptions = this.querySelectSqlColumnMeta(connection, sql);
		// 补充一下字段信息，获取的不准
		String extraSql = "SELECT column_name,data_type,column_size,decimal_digits,column_comment FROM information_schema.COLUMNS WHERE table_schema='" + schemaName + "' AND table_name='" + tableName + "'";
		try (PreparedStatement ps = connection.prepareStatement(extraSql);
			 ResultSet rs = ps.executeQuery();
		) {
			while (rs.next()) {
				String columnName = rs.getString("column_name");
				String dataType = rs.getString("data_type");
				String columnSize = rs.getString("column_size");
				String decimalDigits = rs.getString("decimal_digits");
				String columnComment = rs.getString("column_comment");
				if (columnName != null) {
					for (ColumnDescription cd : columnDescriptions) {
						if (columnName.equals(cd.getFieldName())) {
							cd.setFieldTypeName(dataType);
							int csize = columnSize != null ? Integer.parseInt(columnSize) : 0;
							cd.setDisplaySize(csize);
							cd.setPrecisionSize(csize);
							cd.setScaleSize(decimalDigits != null ? Integer.parseInt(decimalDigits) : 0);
							cd.setRemarks(columnComment);
							break;
						}
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(schemaName + "." + tableName + " queryTableColumnMeta error!!", e);
		}

		return columnDescriptions;
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
			case ColumnMetaData.TYPE_TIME:
				retval += "DATETIME";
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
					retval += "BIGINT";
				} else {
					// Integer values...
					if (precision == 0) {
						if (length > 11) {
							if (length < 19) {
								// can hold signed values between -9223372036854775808 and 9223372036854775807
								// 18 significant digits
								retval += "BIGINT";
							} else {
								retval += "DECIMAL(" + length + ",0)";
							}
						} else {
							retval += "INT";
						}
					} else {
						retval += "DOUBLE";
						/*retval += "DECIMAL(" + length;
						if (precision > 0) {
							retval += ", " + precision;
						}
						retval += ")";*/
						// Floating point values...
						/*if (length > 15) {
							retval += "DECIMAL(" + length;
							if (precision > 0) {
								retval += ", " + precision;
							}
							retval += ")";
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
				if (length * 3 <= 65533) {
					retval += "VARCHAR(" + length * 3 + ")";
				} else {
					retval += "STRING";
					canHaveDefaultValue = false;
				}
				break;
			default:
				retval += "STRING";
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
		return false;
	}

	@Override
	public String getPrimaryKeyAsString(List<String> pks) {
		if (null != pks && !pks.isEmpty()) {
			return "`" +
					StringUtils.join(pks, "` , `") +
					"`";
		}

		return "";
	}

	@Override
	public void setColumnDefaultValue(Connection connection, String schemaName, String tableName, List<ColumnDescription> columnDescriptions) {
		String sql = this.getDefaultValueSql(schemaName, tableName);
		try (Statement st = connection.createStatement()) {
			try (ResultSet rs = st.executeQuery(sql)) {
				while (rs.next()) {
					String columnName = rs.getString("Field");
					String columnDefault = rs.getString("Default");
					if (columnName != null) {
						for (ColumnDescription cd : columnDescriptions) {
							if (columnName.equals(cd.getFieldName())) {
								cd.setDefaultValue(columnDefault);
								break;
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			log.error(schemaName + "." + tableName + " setColumnDefaultValue error:" + e.getMessage());
			//throw new RuntimeException(e);
		}
	}

	@Override
	protected String getDefaultValueSql(String schemaName, String tableName) {
		return String.format("desc `%s`.`%s`", schemaName, tableName);
	}

	@Override
	public List<String> queryTablePrimaryKeys(Connection connection, String schemaName,
											  String tableName) {
		Set<String> ret = new HashSet<>();
		String sql = String.format("desc `%s`.`%s`", schemaName, tableName);
		try (PreparedStatement ps = connection.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery();
		) {
			//看下是否又none的字段，如果有，说明key模式为DUPLICATE KEY 可重复
			boolean NoneExtra = false;
			while (rs.next()) {
				String field = rs.getString("Field");
				String key = rs.getString("Key");
				String extra = rs.getString("Extra");
				if ("true".equalsIgnoreCase(key)) {
					ret.add(field);
				} else {
					if ("NONE".equalsIgnoreCase(extra)) {
						NoneExtra = true;
					}
				}
			}
			if (NoneExtra) {
				return new ArrayList<>();
			}
			return new ArrayList<>(ret);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setColumnIndexInfo(Connection connection, String schemaName, String tableName, List<ColumnDescription> columnDescriptions) {
	}

	@Override
	public List<String> getTableColumnCommentDefinition(TableDescription td,
														List<ColumnDescription> cds) {
		return Collections.emptyList();
	}
}
