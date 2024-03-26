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

/**
 * Kingbase8数据库实现类
 *
 * @author jrl
 */

import javax.sql.DataSource;

public class KingbaseDatabaseOperator extends PostgreSqlDatabaseOperator {

  public KingbaseDatabaseOperator(DataSource dataSource) {
    super(dataSource);
  }

}
