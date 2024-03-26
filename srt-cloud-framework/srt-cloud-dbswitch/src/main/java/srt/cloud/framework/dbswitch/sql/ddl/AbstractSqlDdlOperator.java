// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.sql.ddl;

import java.util.Objects;

/**
 * DDL操作抽象类
 *
 * @author jrl
 */
public abstract class AbstractSqlDdlOperator {

  private String name;

  public AbstractSqlDdlOperator(String name) {
    this.name = Objects.requireNonNull(name);
  }

  public String getName() {
    return this.name;
  }

  @Override
  public String toString() {
    return this.name;
  }

  public abstract String toSqlString(AbstractDatabaseDialect dialect);
}
