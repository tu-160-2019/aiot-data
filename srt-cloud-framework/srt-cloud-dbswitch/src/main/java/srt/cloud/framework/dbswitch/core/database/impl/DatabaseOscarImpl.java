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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 支持神通数据库的元信息实现
 *
 * @author tang
 */
public class DatabaseOscarImpl extends AbstractDatabase implements IDatabaseInterface {

  private static final String SHOW_CREATE_TABLE_SQL =
      "SELECT \"SYS_GET_TABLEDEF\" FROM \"V_SYS_TABLE\" WHERE \"SCHEMANAME\"= ? AND \"TABLENAME\"= ? ";
  private static final String SHOW_CREATE_VIEW_SQL =
      "SELECT \"DEFINITION\" FROM \"V_SYS_VIEWS\" WHERE \"SCHEMANAME\"= ? AND \"VIEWNAME\"= ?";

  public DatabaseOscarImpl() {
    super(ProductTypeEnum.OSCAR.getDriveClassName());
  }

  @Override
  public ProductTypeEnum getDatabaseType() {
    return ProductTypeEnum.OSCAR;
  }

  @Override
  public String getTableDDL(Connection connection, String schemaName, String tableName) {
    String sql = String.format(SHOW_CREATE_TABLE_SQL, tableName, schemaName);
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.setString(1, schemaName);
      ps.setString(2, tableName);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs != null && rs.next()) {
          return rs.getString(1);
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
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.setString(1, schemaName);
      ps.setString(2, tableName);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs != null && rs.next()) {
          return rs.getString(1);
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
        "SELECT * from (%s) tmp LIMIT 0 ",
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
      case ColumnMetaData.TYPE_BIGNUMBER:
      case ColumnMetaData.TYPE_INTEGER:
        retval.append("BIGINT");
        break;
      case ColumnMetaData.TYPE_NUMBER:
        if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
          retval.append("BIGINT");
        } else {
          if (length > 0) {
            if (precision > 0 || length > 18) {
              if ((length + precision) > 0 && precision > 0) {
                // Numeric(Precision, Scale): Precision = total length; Scale = decimal places
                retval.append("NUMERIC(").append(length + precision).append(", ").append(precision).append(")");
              } else {
                retval.append("DOUBLE PRECISION");
              }
            } else {
              if (length > 9) {
                retval.append("BIGINT");
              } else {
                if (length < 5) {
                  retval.append("SMALLINT");
                } else {
                  retval.append("INTEGER");
                }
              }
            }

          } else {
            retval.append("DOUBLE PRECISION");
          }
        }
        break;
      case ColumnMetaData.TYPE_STRING:
        if (2 * length >= AbstractDatabase.CLOB_LENGTH) {
          retval.append("TEXT");
        } else {
          if (length == 1) {
            retval.append("VARCHAR(2)");
          } else if (length > 0 && length < 2048) {
            retval.append("VARCHAR(").append(2 * length).append(')');
          } else {
            retval.append("TEXT");
          }
        }
        break;
      case ColumnMetaData.TYPE_BINARY:
        retval.append("BLOB");
        break;
      default:
        retval.append("TEXT");
        break;
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
  		//TODO
		return false;
	}
}
