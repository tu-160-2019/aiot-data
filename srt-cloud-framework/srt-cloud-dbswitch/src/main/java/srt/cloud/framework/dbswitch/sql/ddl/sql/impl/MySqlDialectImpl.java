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
import srt.cloud.framework.dbswitch.sql.ddl.type.MySqlDataTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 关于MySQL的的自增列问题：
 * <p>
 * （1）一张表中，只能有一列为自增长列。
 * <p>
 * （2）列的数据类型，必须为数值型。
 * <p>
 * （3）不能设置默认值。
 * <p>
 * （4）会自动应用not null。
 *
 * @author jrl
 */
public class MySqlDialectImpl extends AbstractDatabaseDialect {

  private static List<MySqlDataTypeEnum> integerTypes;

  static {
    integerTypes = new ArrayList<>();
    integerTypes.add(MySqlDataTypeEnum.TINYINT);
    integerTypes.add(MySqlDataTypeEnum.SMALLINT);
    integerTypes.add(MySqlDataTypeEnum.MEDIUMINT);
    integerTypes.add(MySqlDataTypeEnum.INTEGER);
    integerTypes.add(MySqlDataTypeEnum.INT);
    integerTypes.add(MySqlDataTypeEnum.BIGINT);
  }

  @Override
  public String getSchemaTableName(String schemaName, String tableName) {
    if (Objects.isNull(schemaName) || schemaName.trim().isEmpty()) {
      return String.format("`%s`", tableName);
    }
    return String.format("`%s`.`%s`", schemaName, tableName);
  }

  @Override
  public String getQuoteFieldName(String fieldName) {
    return String.format("`%s`", fieldName.trim());
  }

  @Override
  public String getPrimaryKeyAsString(List<String> pks) {
    if (!pks.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      sb.append("`");
      sb.append(StringUtils.join(pks, "` , `"));
      sb.append("`");
      return sb.toString();
    }

    return "";
  }

  @Override
  public String getFieldTypeName(ColumnDefinition column) {
    int length = column.getLengthOrPrecision();
    int scale = column.getScale();
    StringBuilder sb = new StringBuilder();
    MySqlDataTypeEnum type = null;
    try {
      type = MySqlDataTypeEnum.valueOf(column.getColumnType().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(
          String.format("Invalid MySQL data type: %s", column.getColumnType()));
    }

    if (column.isAutoIncrement()) {
      if (!MySqlDialectImpl.integerTypes.contains(type)) {
        throw new RuntimeException(
            String.format("Invalid MySQL auto increment data type: %s", column.getColumnType()));
      }
    }

    sb.append(type.name());
    switch (type) {
      case FLOAT:
      case DOUBLE:
      case DECIMAL:
        if (Objects.isNull(length) || length < 0) {
          throw new RuntimeException(
              String.format("Invalid MySQL data type length: %s(%d)", column.getColumnType(),
                  length));
        }

        if (Objects.isNull(scale) || scale < 0) {
          throw new RuntimeException(
              String.format("Invalid MySQL data type scale: %s(%d,%d)", column.getColumnType(),
                  length, scale));
        }

        sb.append(String.format("(%d,%d)", length, scale));
        break;
      case TINYINT:
      case SMALLINT:
      case MEDIUMINT:
      case INTEGER:
      case INT:
      case BIGINT:
      case CHAR:
      case VARCHAR:
        if (Objects.isNull(length) || length < 0) {
          throw new RuntimeException(
              String.format("Invalid MySQL data type length: %s(%d)", column.getColumnType(),
                  length));
        }
        sb.append(String.format(" (%d) ", length));
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
    String comment = column.getColumnComment();

    StringBuilder sb = new StringBuilder();
    sb.append(String.format("`%s` ", fieldname.trim()));
    sb.append(this.getFieldTypeName(column));

    if (column.isAutoIncrement() && column.isPrimaryKey()) {
      //在MySQL数据库里只有主键是自增的
      sb.append(" NOT NULL AUTO_INCREMENT ");
    } else {
      if (nullable) {
        sb.append(" DEFAULT NULL");
      } else if (Objects.nonNull(defaultValue) && !defaultValue.isEmpty()) {
        if ("NULL".equalsIgnoreCase(defaultValue)) {
          sb.append(" DEFAULT NULL");
        } else if (defaultValue.toUpperCase().trim().startsWith("CURRENT_TIMESTAMP")) {
          // 处理时间字段的默认当前时间问题
          sb.append(String.format(" DEFAULT %s", defaultValue));
        } else {
          sb.append(String.format(" DEFAULT '%s'", defaultValue));
        }
      } else {
        sb.append(" NOT NULL");
      }
    }

    if (Objects.nonNull(comment) && !comment.isEmpty()) {
      sb.append(String.format(" COMMENT '%s'", comment));
    }

    return sb.toString();
  }

}
