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
import srt.cloud.framework.dbswitch.core.util.GenerateSqlUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 支持Sybase数据库的元信息实现
 *
 * @author tang
 */
public class DatabaseSybaseImpl extends AbstractDatabase implements IDatabaseInterface {

  private static final String SHOW_CREATE_VIEW_SQL = "SELECT sc.text FROM sysobjects so, syscomments sc WHERE user_name(so.uid)=? AND so.name=? and sc.id = so.id ORDER BY sc.colid";

  private static Set<String> excludesSchemaNames;

  static {
    excludesSchemaNames = new HashSet<>();
    excludesSchemaNames.add("keycustodian_role");
    excludesSchemaNames.add("ha_role");
    excludesSchemaNames.add("replication_role");
    excludesSchemaNames.add("sa_role");
    excludesSchemaNames.add("usedb_user");
    excludesSchemaNames.add("replication_maint_role_gp");
    excludesSchemaNames.add("sybase_ts_role");
    excludesSchemaNames.add("dtm_tm_role");
    excludesSchemaNames.add("sso_role");
    excludesSchemaNames.add("navigator_role");
    excludesSchemaNames.add("sa_serverprivs_role");
    excludesSchemaNames.add("probe");
    excludesSchemaNames.add("mon_role");
    excludesSchemaNames.add("webservices_role");
    excludesSchemaNames.add("js_admin_role");
    excludesSchemaNames.add("js_user_role");
    excludesSchemaNames.add("messaging_role");
    excludesSchemaNames.add("js_client_role");
    excludesSchemaNames.add("oper_role");
    excludesSchemaNames.add("hadr_admin_role_gp");
  }

  public DatabaseSybaseImpl() {
    super(ProductTypeEnum.SYBASE.getDriveClassName());
  }

  @Override
  public ProductTypeEnum getDatabaseType() {
    return ProductTypeEnum.SYBASE;
  }

  private void setCatalogName(Connection connection){
    try {
      this.catalogName = connection.getCatalog();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<String> querySchemaList(Connection connection) {
    setCatalogName(connection);
    List<String> schemas = super.querySchemaList(connection);
    return schemas.stream().filter(s -> !excludesSchemaNames.contains(s)).collect(Collectors.toList());
  }

  @Override
  public List<TableDescription> queryTableList(Connection connection, String schemaName) {
    setCatalogName(connection);
    return super.queryTableList(connection, schemaName);
  }

  @Override
  public List<String> queryTableColumnName(Connection connection, String schemaName,
      String tableName) {
    setCatalogName(connection);
    return super.queryTableColumnName(connection, schemaName, tableName);
  }

	@Override
	protected String getDefaultValueSql(String schemaName, String tableName) {
		return null;
	}

	@Override
  public List<String> queryTablePrimaryKeys(Connection connection, String schemaName,
      String tableName) {
    setCatalogName(connection);
    return super.queryTablePrimaryKeys(connection, schemaName, tableName);
  }

  @Override
  public List<ColumnDescription> queryTableColumnMeta(Connection connection, String schemaName,
													  String tableName) {
    setCatalogName(connection);
    return super.queryTableColumnMeta(connection, schemaName, tableName);
  }

  @Override
  public String getTableDDL(Connection connection, String schemaName, String tableName) {
    List<ColumnDescription> columnDescriptions = queryTableColumnMeta(connection, schemaName, tableName);
    List<String> pks = queryTablePrimaryKeys(connection, schemaName, tableName);
    return GenerateSqlUtils.getDDLCreateTableSQL(ProductTypeEnum.SYBASE,
        columnDescriptions, pks, schemaName, tableName, false);
  }

  @Override
  public String getViewDDL(Connection connection, String schemaName, String tableName) {
    try (PreparedStatement ps = connection.prepareStatement(SHOW_CREATE_VIEW_SQL)) {
      ps.setString(1, schemaName);
      ps.setString(2, tableName);
      try (ResultSet rs = ps.executeQuery()) {
        StringBuilder sql = new StringBuilder();
        while (rs.next()) {
          sql.append(rs.getString(1));
        }
        return sql.toString();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<ColumnDescription> querySelectSqlColumnMeta(Connection connection, String sql) {
    setCatalogName(connection);
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

    switch (type) {
      case ColumnMetaData.TYPE_TIMESTAMP:
      case ColumnMetaData.TYPE_TIME:
      case ColumnMetaData.TYPE_DATE:
        retval += "DATETIME";
        if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
          retval += " NOT NULL";
        }
        break;
      case ColumnMetaData.TYPE_BOOLEAN:
        retval += "TINYINT";
        if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
          retval += " NOT NULL";
        }
        break;
      case ColumnMetaData.TYPE_NUMBER:
      case ColumnMetaData.TYPE_INTEGER:
      case ColumnMetaData.TYPE_BIGNUMBER:
        if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
          if (useAutoInc) {
            retval += "INTEGER IDENTITY NOT NULL";
          } else {
            retval += "INTEGER NOT NULL";
          }
        } else {
          if (precision != 0 || (precision == 0 && length > 9)) {
            if (precision > 0 && length > 0) {
              retval += "DECIMAL(" + length + ", " + precision + ") NULL";
            } else {
              retval += "DOUBLE PRECISION NULL";
            }
          } else {
            if (length < 3) {
              retval += "TINYINT NULL";
            } else if (length < 5) {
              retval += "SMALLINT NULL";
            } else {
              retval += "INTEGER NULL";
            }
          }
        }
        break;
      case ColumnMetaData.TYPE_STRING:
        if (length >= 2048) {
          retval += "TEXT NULL";
        } else {
          retval += "VARCHAR";
          if (length > 0) {
            retval += "(" + length + ")";
          }
          if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
            retval += " NOT NULL";
          } else {
            retval += " NULL";
          }
        }
        break;
      case ColumnMetaData.TYPE_BINARY:
        retval += "VARBINARY";
        break;
      default:
        retval += "TEXT NULL";
        break;
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
    return Collections.emptyList();
  }

	@Override
	public boolean canCreateIndex(ColumnMetaData v) {
  		//TODO
		return false;
	}

}
