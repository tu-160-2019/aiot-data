// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.common.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class HivePrepareUtils {

  private final static String HIVE_SQL_1 = "set hive.resultset.use.unique.column.names=false";
  private final static String HIVE_SQL_2 = "set hive.support.concurrency=true";
  private final static String HIVE_SQL_3 = "set hive.txn.manager = org.apache.hadoop.hive.ql.lockmgr.DbTxnManager";

  private HivePrepareUtils() {
  }

  public static void setResultSetColumnNameNotUnique(Connection connection)
      throws SQLException {
    executeWithoutResultSet(connection, HIVE_SQL_1);
  }

  public static void prepare(Connection connection, String schema, String table)
      throws SQLException {
    executeWithoutResultSet(connection, HIVE_SQL_1);
    if (isTransactionalTable(connection, schema, table)) {
      executeWithoutResultSet(connection, HIVE_SQL_2);
      executeWithoutResultSet(connection, HIVE_SQL_3);
    }
  }

  private static boolean isTransactionalTable(Connection connection, String schema, String table)
      throws SQLException {
    String fullTableName = String.format("`%s`.`%s`", schema, table);
    String sql = String.format("DESCRIBE FORMATTED %s", fullTableName);
    try (Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql)) {
      while (rs.next()) {
        String dataType = rs.getString("data_type");
        String comment = rs.getString("comment");
        if (dataType != null
            && comment != null
            && dataType.startsWith("transactional")
            && comment.startsWith("true")) {
          return true;
        }
      }
      return false;
    }
  }

  private static boolean executeWithoutResultSet(Connection connection, String sql)
      throws SQLException {
    try (Statement st = connection.createStatement()) {
      return st.execute(sql);
    }
  }
}
