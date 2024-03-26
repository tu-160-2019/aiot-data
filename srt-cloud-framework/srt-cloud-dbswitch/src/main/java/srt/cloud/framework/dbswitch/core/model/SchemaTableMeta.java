// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.core.model;

import java.util.List;

public class SchemaTableMeta extends TableDescription {

  private List<String> primaryKeys;

  private String createSql;

  private List<ColumnDescription> columns;

  public List<String> getPrimaryKeys() {
    return primaryKeys;
  }

  public void setPrimaryKeys(List<String> primaryKeys) {
    this.primaryKeys = primaryKeys;
  }

  public String getCreateSql() {
    return createSql;
  }

  public void setCreateSql(String createSql) {
    this.createSql = createSql;
  }

  public List<ColumnDescription> getColumns() {
    return columns;
  }

  public void setColumns(List<ColumnDescription> columns) {
    this.columns = columns;
  }
}
