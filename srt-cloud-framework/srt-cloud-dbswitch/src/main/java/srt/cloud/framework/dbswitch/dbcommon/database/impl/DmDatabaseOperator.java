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
 * DM数据库实现类
 *
 * @author jrl
 */
public class DmDatabaseOperator extends OracleDatabaseOperator {

  public DmDatabaseOperator(DataSource dataSource) {
    super(dataSource);
  }

}
