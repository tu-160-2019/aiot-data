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

/**
 * Greenplum数据库实现类
 *
 * @author jrl
 */
public class GreenplumDatabaseOperator extends PostgreSqlDatabaseOperator {

  public GreenplumDatabaseOperator(DataSource dataSource) {
    super(dataSource);
  }
}
