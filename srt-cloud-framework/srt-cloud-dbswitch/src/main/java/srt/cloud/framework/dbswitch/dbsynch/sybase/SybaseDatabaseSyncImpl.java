// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbsynch.sybase;


import srt.cloud.framework.dbswitch.dbsynch.mssql.SqlServerDatabaseSyncImpl;

import javax.sql.DataSource;

/**
 * Sybase数据库DML同步实现类
 *
 * @author tang
 */
public class SybaseDatabaseSyncImpl extends SqlServerDatabaseSyncImpl {

  public SybaseDatabaseSyncImpl(DataSource ds) {
    super(ds);
  }

}
