// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbcommon.database.impl;

import javax.sql.DataSource;

public class OscarDatabaseOperator extends OracleDatabaseOperator {

  public OscarDatabaseOperator(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  public void dropTable(String schemaName, String tableName) {
    String sql = String.format("DROP TABLE \"%s\".\"%s\" CASCADE ", schemaName, tableName);
    this.executeSql(sql);
  }
}
