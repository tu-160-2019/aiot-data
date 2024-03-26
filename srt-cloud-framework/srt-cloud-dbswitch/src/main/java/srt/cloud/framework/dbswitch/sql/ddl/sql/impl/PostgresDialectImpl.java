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

import srt.cloud.framework.dbswitch.sql.ddl.AbstractDatabaseDialect;
import srt.cloud.framework.dbswitch.sql.ddl.pojo.ColumnDefinition;
import srt.cloud.framework.dbswitch.sql.ddl.type.PostgresDataTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * PostgreSQL方言实现类
 *
 * @author jrl
 */
public class PostgresDialectImpl extends AbstractDatabaseDialect {

  private static List<PostgresDataTypeEnum> integerTypes;

  static {
    integerTypes = new ArrayList<>();
    integerTypes.add(PostgresDataTypeEnum.SERIAL2);
    integerTypes.add(PostgresDataTypeEnum.SERIAL4);
    integerTypes.add(PostgresDataTypeEnum.SERIAL8);
    integerTypes.add(PostgresDataTypeEnum.SMALLSERIAL);
    integerTypes.add(PostgresDataTypeEnum.SERIAL);
    integerTypes.add(PostgresDataTypeEnum.BIGSERIAL);
  }

  @Override
  public String getFieldTypeName(ColumnDefinition column) {
    int length = column.getLengthOrPrecision();
    int scale = column.getScale();

    StringBuilder sb = new StringBuilder();
    PostgresDataTypeEnum type = null;
    try {
      type = PostgresDataTypeEnum.valueOf(column.getColumnType().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(
          String.format("Invalid PostgreSQL data type: %s", column.getColumnType()));
    }

    if (column.isAutoIncrement()) {
      if (!PostgresDialectImpl.integerTypes.contains(type)) {
        throw new RuntimeException(String
            .format("Invalid PostgreSQL auto increment data type: %s", column.getColumnType()));
      }
    }

    sb.append(type.name());
    switch (type) {
      case NUMERIC:
      case DECIMAL:
        if (Objects.isNull(length) || length < 0) {
          throw new RuntimeException(
              String.format("Invalid PostgreSQL data type length: %s(%d)", column.getColumnType(),
                  length));
        }

        if (Objects.isNull(scale) || scale < 0) {
          throw new RuntimeException(
              String.format("Invalid PostgreSQL data type scale: %s(%d,%d)", column.getColumnType(),
                  length, scale));
        }

        sb.append(String.format("(%d,%d)", length, scale));
        break;
      case CHAR:
      case VARCHAR:
        if (Objects.isNull(length) || length < 0) {
          throw new RuntimeException(
              String.format("Invalid PostgreSQL data type length: %s(%d)", column.getColumnType(),
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
              String.format("Invalid PostgreSQL data type length: %s(%d)", column.getColumnType(),
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

  @Override
  public String getFieldDefination(ColumnDefinition column) {
    String fieldname = column.getColumnName();
    boolean nullable = column.isNullable();
    String defaultValue = column.getDefaultValue();
    //String comment=column.getColumnComment();

    StringBuilder sb = new StringBuilder();
    sb.append(String.format("\"%s\" ", fieldname.trim()));
    sb.append(this.getFieldTypeName(column));

    if (column.isAutoIncrement()) {
      //PostgreSQL/Greenplum数据库里可以有多个自增列
      sb.append(" ");
    } else {
      if (nullable) {
        sb.append(" DEFAULT NULL");
      } else if (Objects.nonNull(defaultValue) && !defaultValue.isEmpty()) {
        if (defaultValue.equalsIgnoreCase("NULL")) {
          sb.append(" DEFAULT NULL");
        } else if ("now()".equalsIgnoreCase(defaultValue)) {
          sb.append(" DEFAULT now() ");
        } else {
          sb.append(String.format(" DEFAULT '%s'", defaultValue));
        }
      } else {
        sb.append(" NOT NULL");
      }
    }

    return sb.toString();
  }

}
