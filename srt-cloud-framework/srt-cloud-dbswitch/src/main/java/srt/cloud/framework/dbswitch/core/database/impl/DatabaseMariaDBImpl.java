// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.core.database.impl;

import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;

/**
 * 支持MariaDB数据库的元信息实现
 *
 * @author jrl
 */
public class DatabaseMariaDBImpl extends DatabaseMysqlImpl {

  public DatabaseMariaDBImpl() {
    super(ProductTypeEnum.MARIADB.getDriveClassName());
  }

  @Override
  public ProductTypeEnum getDatabaseType() {
    return ProductTypeEnum.MARIADB;
  }

}
