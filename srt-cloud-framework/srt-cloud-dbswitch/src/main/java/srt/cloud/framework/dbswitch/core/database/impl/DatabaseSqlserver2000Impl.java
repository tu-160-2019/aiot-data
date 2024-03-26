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

import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.core.database.IDatabaseInterface;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.TableDescription;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 支持SQLServer2000数据库的元信息实现
 *
 * @author jrl
 */
public class DatabaseSqlserver2000Impl extends DatabaseSqlserverImpl implements IDatabaseInterface {

  public DatabaseSqlserver2000Impl() {
    super(ProductTypeEnum.SQLSERVER2000.getDriveClassName());
  }

  @Override
  public ProductTypeEnum getDatabaseType() {
    return ProductTypeEnum.SQLSERVER2000;
  }

  @Override
  public List<TableDescription> queryTableList(Connection connection, String schemaName) {
    List<TableDescription> ret = new ArrayList<>();
    Set<String> uniqueSet = new HashSet<>();
    String[] types = new String[]{"TABLE", "VIEW"};
    try (ResultSet tables = connection.getMetaData()
        .getTables(this.catalogName, schemaName, "%", types);) {
      while (tables.next()) {
        String tableName = tables.getString("TABLE_NAME");
        if (uniqueSet.contains(tableName)) {
          continue;
        } else {
          uniqueSet.add(tableName);
        }

        TableDescription td = new TableDescription();
        td.setSchemaName(schemaName);
        td.setTableName(tableName);
        td.setRemarks(tables.getString("REMARKS"));
        if (tables.getString("TABLE_TYPE").equalsIgnoreCase("VIEW")) {
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
  public List<ColumnDescription> queryTableColumnMeta(Connection connection, String schemaName,
													  String tableName) {
    String sql = this.getTableFieldsQuerySQL(schemaName, tableName);
    List<ColumnDescription> ret = this.querySelectSqlColumnMeta(connection, sql);
    try (ResultSet columns = connection.getMetaData()
        .getColumns(this.catalogName, schemaName, tableName, null)) {
      while (columns.next()) {
        String columnName = columns.getString("COLUMN_NAME");
        String remarks = columns.getString("REMARKS");
        for (ColumnDescription cd : ret) {
          if (columnName.equalsIgnoreCase(cd.getFieldName())) {
            cd.setRemarks(remarks);
          }
        }
      }
      return ret;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
