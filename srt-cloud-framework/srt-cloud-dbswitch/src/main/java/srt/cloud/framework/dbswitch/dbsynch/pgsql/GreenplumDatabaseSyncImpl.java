// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbsynch.pgsql;

import srt.cloud.framework.dbswitch.dbsynch.IDatabaseSynchronize;

import javax.sql.DataSource;

/**
 * Greenplum数据库DML同步实现类
 *
 * @author jrl
 */
public class GreenplumDatabaseSyncImpl extends PostgresqlDatabaseSyncImpl implements
		IDatabaseSynchronize {

  public GreenplumDatabaseSyncImpl(DataSource ds) {
    super(ds);
  }

}
