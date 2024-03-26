// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.sql.ddl.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 表定义实体类
 *
 * @author jrl
 */
public class TableDefinition {

  private String schemaName;
  private String tableName;
  private String tableComment;
  private List<ColumnDefinition> columns = new ArrayList<>();

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = Objects.requireNonNull(schemaName);
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = Objects.requireNonNull(tableName);
  }

  public String getTableComment() {
    return tableComment;
  }

  public void setTableComment(String tableComment) {
    this.tableComment = tableComment;
  }

  public List<ColumnDefinition> getColumns() {
    return columns;
  }

  public void addColumns(ColumnDefinition column) {
    columns.add(column);
  }

  @Override
  public String toString() {
    return "TableDefinition [schemaName=" + schemaName + ", tableName=" + tableName
        + ", tableComment=" + tableComment + ", columns=" + columns + "]";
  }

}
