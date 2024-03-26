// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbsynch.kingbase;

import srt.cloud.framework.dbswitch.dbsynch.pgsql.PostgresqlDatabaseSyncImpl;

import javax.sql.DataSource;

/**
 * kingbase8数据库DML同步实现类
 *
 * @author jrl
 */
public class KingbaseDatabaseSyncImpl extends PostgresqlDatabaseSyncImpl {

  public KingbaseDatabaseSyncImpl(DataSource ds) {
    super(ds);
  }

}
