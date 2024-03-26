// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.sql.ddl.type;

import java.sql.Types;

/**
 * PostgreSQL的数据类型
 * <p>
 * 参考地址：https://www.yiibai.com/postgresql/postgresql-datatypes.html
 *
 * @author jrl
 */
public enum GreenplumDataTypeEnum {

  //~~~~~整型类型~~~~~~~~
  SMALLINT(0, Types.SMALLINT),
  INT2(1, Types.SMALLINT),
  INTEGER(2, Types.INTEGER),
  INT4(3, Types.INTEGER),
  BIGINT(4, Types.BIGINT),
  INT8(5, Types.BIGINT),
  DECIMAL(6, Types.DECIMAL),
  NUMERIC(7, Types.NUMERIC),
  REAL(8, Types.REAL),//equal float4
  FLOAT4(9, Types.FLOAT),
  DOUBLE(10, Types.DOUBLE),
  FLOAT8(11, Types.DOUBLE),
  SMALLSERIAL(12, Types.SMALLINT),
  SERIAL2(13, Types.SMALLINT),
  SERIAL(14, Types.INTEGER),
  SERIAL4(15, Types.INTEGER),
  BIGSERIAL(16, Types.BIGINT),
  SERIAL8(17, Types.BIGINT),

  //~~~~~日期和时间类型~~~~~~~~
  DATE(18, Types.DATE),
  TIME(19, Types.TIME),
  TIMESTAMP(20, Types.TIMESTAMP),

  //~~~~~字符串类型~~~~~~~~
  CHAR(21, Types.CHAR),
  VARCHAR(22, Types.VARCHAR),
  TEXT(23, Types.CLOB),
  BYTEA(24, Types.BLOB),

  //~~~~~~~其他类型~~~~~~~~
  BOOL(25, Types.BOOLEAN);

  private int index;
  private int jdbctype;

  GreenplumDataTypeEnum(int idx, int jdbcType) {
    this.index = idx;
    this.jdbctype = jdbcType;
  }

  public int getIndex() {
    return index;
  }

  public int getJdbcType() {
    return this.jdbctype;
  }

}
