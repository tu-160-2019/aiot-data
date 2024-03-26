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
 * MySQL的数据类型
 * <p>
 * 参考地址：https://www.yiibai.com/mysql/data-types.html
 *
 * @author jrl
 */
public enum MySqlDataTypeEnum {

  //~~~~~整型类型~~~~~~~~
  TINYINT(0, Types.TINYINT),
  SMALLINT(1, Types.SMALLINT),
  MEDIUMINT(2, Types.INTEGER),
  INTEGER(3, Types.INTEGER),
  INT(4, Types.INTEGER),
  BIGINT(5, Types.BIGINT),
  FLOAT(6, Types.FLOAT),
  DOUBLE(7, Types.DOUBLE),
  DECIMAL(8, Types.DECIMAL),

  //~~~~~日期和时间类型~~~~~~~~
  DATE(9, Types.DATE),
  TIME(10, Types.TIME),
  YEAR(11, Types.DATE),
  DATETIME(12, Types.TIMESTAMP),
  TIMESTAMP(13, Types.TIMESTAMP),

  //~~~~~字符串类型~~~~~~~~
  CHAR(14, Types.CHAR),
  VARCHAR(15, Types.VARCHAR),
  TINYBLOB(16, Types.VARBINARY),
  TINYTEXT(17, Types.CLOB),
  BLOB(18, Types.VARBINARY),
  TEXT(19, Types.CLOB),
  MEDIUMBLOB(20, Types.LONGVARBINARY),
  MEDIUMTEXT(21, Types.LONGVARCHAR),
  LONGBLOB(22, Types.LONGVARBINARY),
  LONGTEXT(23, Types.LONGVARCHAR);

  private int index;
  private int jdbctype;

  MySqlDataTypeEnum(int idx, int jdbcType) {
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
