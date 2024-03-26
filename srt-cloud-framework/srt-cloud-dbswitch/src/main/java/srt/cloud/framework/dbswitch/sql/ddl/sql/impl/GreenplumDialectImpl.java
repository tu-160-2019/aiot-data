// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.sql.ddl.sql.impl;

import srt.cloud.framework.dbswitch.sql.ddl.pojo.ColumnDefinition;
import srt.cloud.framework.dbswitch.sql.ddl.type.GreenplumDataTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Greenplum方言实现类
 *
 * @author jrl
 */
public class GreenplumDialectImpl extends PostgresDialectImpl {

  protected static List<GreenplumDataTypeEnum> integerTypes;

  static {
    integerTypes = new ArrayList<>();
    integerTypes.add(GreenplumDataTypeEnum.SERIAL2);
    integerTypes.add(GreenplumDataTypeEnum.SERIAL4);
    integerTypes.add(GreenplumDataTypeEnum.SERIAL8);
    integerTypes.add(GreenplumDataTypeEnum.SMALLSERIAL);
    integerTypes.add(GreenplumDataTypeEnum.SERIAL);
    integerTypes.add(GreenplumDataTypeEnum.BIGSERIAL);
  }

  @Override
  public String getFieldTypeName(ColumnDefinition column) {
    int length = column.getLengthOrPrecision();
    int scale = column.getScale();

    StringBuilder sb = new StringBuilder();
    GreenplumDataTypeEnum type = null;
    try {
      type = GreenplumDataTypeEnum.valueOf(column.getColumnType().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(
          String.format("Invalid Greenplum data type: %s", column.getColumnType()));
    }

    if (column.isAutoIncrement()) {
      if (!GreenplumDialectImpl.integerTypes.contains(type)) {
        throw new RuntimeException(String
            .format("Invalid Greenplum auto increment data type: %s", column.getColumnType()));
      }
    }

    sb.append(type.name());
    switch (type) {
      case NUMERIC:
      case DECIMAL:
        if (Objects.isNull(length) || length < 0) {
          throw new RuntimeException(
              String.format("Invalid Greenplum data type length: %s(%d)", column.getColumnType(),
                  length));
        }

        if (Objects.isNull(scale) || scale < 0) {
          throw new RuntimeException(
              String.format("Invalid Greenplum data type scale: %s(%d,%d)", column.getColumnType(),
                  length, scale));
        }

        sb.append(String.format("(%d,%d)", length, scale));
        break;
      case CHAR:
      case VARCHAR:
        if (Objects.isNull(length) || length < 0) {
          throw new RuntimeException(
              String.format("Invalid Greenplum data type length: %s(%d)", column.getColumnType(),
                  length));
        }
        sb.append(String.format(" (%d) ", length));
        break;
      case TIMESTAMP:
        if (Objects.isNull(length) || length < 0) {
          sb.append(" (0) ");
        } else if (0 == length || 6 == length) {
          sb.append(String.format(" (%d) ", length));
        } else {
          throw new RuntimeException(
              String.format("Invalid Greenplum data type length: %s(%d)", column.getColumnType(),
                  length));
        }
        break;
      case DOUBLE:
        sb.append(" PRECISION ");
        break;
      default:
        break;
    }

    return sb.toString();
  }

}
