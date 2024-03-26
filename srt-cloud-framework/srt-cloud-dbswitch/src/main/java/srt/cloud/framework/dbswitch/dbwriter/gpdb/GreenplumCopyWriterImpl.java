// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbwriter.gpdb;

import lombok.extern.slf4j.Slf4j;
import srt.cloud.framework.dbswitch.dbwriter.AbstractDatabaseWriter;
import srt.cloud.framework.dbswitch.dbwriter.IDatabaseWriter;
import srt.cloud.framework.dbswitch.dbwriter.util.ObjectCastUtils;
import srt.cloud.framework.dbswitch.pgwriter.row.SimpleRow;
import srt.cloud.framework.dbswitch.pgwriter.row.SimpleRowWriter;
import srt.cloud.framework.dbswitch.pgwriter.util.PostgreSqlUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Greenplum数据库Copy写入实现类
 *
 * @author jrl
 */
@Slf4j
public class GreenplumCopyWriterImpl extends AbstractDatabaseWriter implements IDatabaseWriter {

  private static Set<String> unsupportedClassTypeName;

  static {
    unsupportedClassTypeName = new HashSet<>();
    unsupportedClassTypeName.add("oracle.sql.TIMESTAMPLTZ");
    unsupportedClassTypeName.add("oracle.sql.TIMESTAMPTZ");
  }

  public GreenplumCopyWriterImpl(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  protected String getDatabaseProductName() {
    return "Greenplum";
  }

  @Override
  public long write(List<String> fieldNames, List<Object[]> recordValues) {
    if (recordValues.isEmpty()) {
      return 0;
    }
    if (fieldNames.isEmpty()) {
      throw new IllegalArgumentException("第一个参数[fieldNames]为空,无效!");
    }
    if (null == this.columnType || this.columnType.isEmpty()) {
      throw new RuntimeException("请先调用prepareWrite()函数，或者出现内部代码集成调用错误！");
    }

    String[] columnNames = new String[fieldNames.size()];
    for (int i = 0; i < fieldNames.size(); ++i) {
      String s = fieldNames.get(i);
      if (!this.columnType.containsKey(s)) {
        throw new RuntimeException(
            String.format("表%s.%s 中不存在字段名为%s的字段，请检查参数传入!", schemaName, tableName, s));
      }

      columnNames[i] = s;
    }

    SimpleRowWriter.Table table = new SimpleRowWriter.Table(schemaName, tableName, columnNames);
    try (Connection connection = dataSource.getConnection();
        SimpleRowWriter pgwriter =
            new SimpleRowWriter(table, PostgreSqlUtils.getPGConnection(connection), true)) {
      pgwriter.enableNullCharacterHandler();
      for (Object[] objects : recordValues) {
        if (fieldNames.size() != objects.length) {
          throw new RuntimeException(
              String.format("传入的参数有误，字段列数%d与记录中的值个数%d不相符合", fieldNames.size(), objects.length));
        }

        pgwriter.startRow(this.getConsumer(fieldNames, objects));
      }

      return recordValues.size();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 数据类型转换参考
   * <p>
   * 1. spring-jdbc: {@code org.springframework.jdbc.core.StatementCreatorUtils}
   * <p>
   * 2. postgresql-driver: {@code org.postgresql.jdbc.PgPreparedStatement}
   */
  private Consumer<SimpleRow> getConsumer(List<String> fieldNames, Object[] objects) {
    return (row) -> {
      for (int i = 0; i < objects.length; ++i) {
        String fieldName = fieldNames.get(i);
        Object fieldValue = objects[i];
        Integer fieldType = columnType.get(fieldName);
        switch (fieldType) {
          case Types.CHAR:
          case Types.NCHAR:
          case Types.VARCHAR:
          case Types.LONGVARCHAR:
          case Types.NVARCHAR:
          case Types.LONGNVARCHAR:
            if (null == fieldValue) {
              row.setVarChar(i, null);
            } else if (unsupportedClassTypeName.contains(fieldValue.getClass().getName())) {
              row.setVarChar(i, null);
            } else {
              String val = ObjectCastUtils.castToString(fieldValue);
              if (null == val) {
                throw new RuntimeException(String.format(
                    "表[%s.%s]的字段名[%s]数据类型转换错误，应该为java.lang.String/java.sql.Clob，而实际的数据类型为%s",
                    schemaName, tableName, fieldName, fieldValue.getClass().getName()));
              }

              row.setVarChar(i, val);
            }
            break;
          case Types.CLOB:
          case Types.NCLOB:
            if (null == fieldValue) {
              row.setText(i, null);
            } else if (unsupportedClassTypeName.contains(fieldValue.getClass().getName())) {
              row.setText(i, null);
            } else {
              String val = ObjectCastUtils.castToString(fieldValue);
              if (null == val) {
                throw new RuntimeException(String.format(
                    "表名[%s.%s]的字段名[%s]数据类型转换错误，应该为java.lang.String/java.sql.Clob，而实际的数据类型为%s",
                    schemaName, tableName, fieldName, fieldValue.getClass().getName()));
              }

              row.setText(i, val);
            }
            break;
          case Types.TINYINT:
            if (null == fieldValue) {
              row.setByte(i, null);
            } else {
              Byte val = null;
              try {
                val = ObjectCastUtils.castToByte(fieldValue);
              } catch (RuntimeException e) {
                throw new RuntimeException(String.format("表名[%s.%s]的字段名[%s]数据类型转错误，%s",
                    schemaName, tableName, fieldName, e.getMessage()));
              }

              if (null == val) {
                throw new RuntimeException(String.format(
                    "表名[%s.%s]的字段名[%s]数据类型转错误，应该为java.lang.Byte，而实际的数据类型为%s", schemaName,
                    tableName, fieldName, fieldValue.getClass().getName()));
              }

              row.setByte(i, val);
            }
            break;
          case Types.SMALLINT:
            if (null == fieldValue) {
              row.setShort(i, null);
            } else {
              Short val = null;
              try {
                val = ObjectCastUtils.castToShort(fieldValue);
              } catch (RuntimeException e) {
                throw new RuntimeException(String.format("表名[%s.%s]的字段名[%s]数据类型转错误，%s",
                    schemaName, tableName, fieldName, e.getMessage()));
              }

              if (null == val) {
                throw new RuntimeException(String.format(
                    "表名[%s.%s]的字段名[%s]数据类型转换错误，应该为java.lang.Short，而实际的数据类型为%s", schemaName,
                    tableName, fieldName, fieldValue.getClass().getName()));
              }

              row.setShort(i, val);
            }
            break;
          case Types.INTEGER:
            if (null == fieldValue) {
              row.setInteger(i, null);
            } else {
              Integer val = null;
              try {
                val = ObjectCastUtils.castToInteger(fieldValue);
              } catch (RuntimeException e) {
                throw new RuntimeException(String.format("表名[%s.%s]的字段名[%s]数据类型转错误，%s",
                    schemaName, tableName, fieldName, e.getMessage()));
              }

              if (null == val) {
                throw new RuntimeException(String.format(
                    "表名[%s.%s]的字段名[%s]数据类型转换错误，应该为java.lang.Integer，而实际的数据类型为%s",
                    schemaName, tableName, fieldName, fieldValue.getClass().getName()));
              }

              row.setInteger(i, val);
            }
            break;
          case Types.BIGINT:
            if (null == fieldValue) {
              row.setLong(i, null);
            } else {
              Long val = null;
              try {
                val = ObjectCastUtils.castToLong(fieldValue);
              } catch (RuntimeException e) {
                throw new RuntimeException(String.format("表名[%s.%s]的字段名[%s]数据类型转错误，%s",
                    schemaName, tableName, fieldName, e.getMessage()));
              }

              if (null == val) {
                throw new RuntimeException(String.format(
                    "表名[%s.%s]的字段名[%s]数据类型转换错误，应该为java.lang.Long，而实际的数据类型为%s", schemaName,
                    tableName, fieldName, fieldValue.getClass().getName()));
              }

              row.setLong(i, val);
            }
            break;
          case Types.NUMERIC:
          case Types.DECIMAL:
            if (null == fieldValue) {
              row.setNumeric(i, null);
            } else {
              Number val = null;
              try {
                val = ObjectCastUtils.castToNumeric(fieldValue);
              } catch (RuntimeException e) {
                throw new RuntimeException(String.format("表名[%s.%s]的字段名[%s]数据类型转错误，%s",
                    schemaName, tableName, fieldName, e.getMessage()));
              }

              if (null == val) {
                throw new RuntimeException(String.format(
                    "表名[%s.%s]的字段名[%s]数据类型转换错误，应该为java.lang.Number，而实际的数据类型为%s", schemaName,
                    tableName, fieldName, fieldValue.getClass().getName()));
              }

              row.setNumeric(i, val);
            }
            break;
          case Types.FLOAT:
          case Types.REAL:
            if (null == fieldValue) {
              row.setFloat(i, null);
            } else {
              Float val = null;
              try {
                val = ObjectCastUtils.castToFloat(fieldValue);
              } catch (RuntimeException e) {
                throw new RuntimeException(String.format("表名[%s.%s]的字段名[%s]数据类型转错误，%s",
                    schemaName, tableName, fieldName, e.getMessage()));
              }

              if (null == val) {
                throw new RuntimeException(String.format(
                    "表名[%s.%s]的字段名[%s]数据类型转换错误，应该为java.lang.Float，而实际的数据类型为%s", schemaName,
                    tableName, fieldName, fieldValue.getClass().getName()));
              }

              row.setFloat(i, val);
            }
            break;
          case Types.DOUBLE:
            if (null == fieldValue) {
              row.setDouble(i, null);
            } else {
              Double val = null;
              try {
                val = ObjectCastUtils.castToDouble(fieldValue);
              } catch (RuntimeException e) {
                throw new RuntimeException(String.format("表名[%s.%s]的字段名[%s]数据类型转错误，%s",
                    schemaName, tableName, fieldName, e.getMessage()));
              }

              if (null == val) {
                throw new RuntimeException(String.format(
                    "表名[%s.%s]的字段名[%s]数据类型转换错误，应该为java.lang.Double，而实际的数据类型为%s", schemaName,
                    tableName, fieldName, fieldValue.getClass().getName()));
              }
              row.setDouble(i, val);
            }
            break;
          case Types.BOOLEAN:
          case Types.BIT:
            if (null == fieldValue) {
              row.setBoolean(i, null);
            } else {
              Boolean val = null;
              try {
                val = ObjectCastUtils.castToBoolean(fieldValue);
              } catch (RuntimeException e) {
                throw new RuntimeException(String.format("表名[%s.%s]的字段名[%s]数据类型转错误，%s",
                    schemaName, tableName, fieldName, e.getMessage()));
              }

              if (null == val) {
                throw new RuntimeException(String.format(
                    "表名[%s.%s]的字段名[%s]数据类型错误，应该为java.lang.Boolean，而实际的数据类型为%s", schemaName,
                    tableName, fieldName, fieldValue.getClass().getName()));
              }
              row.setBoolean(i, val);
            }
            break;
          case Types.TIME:
            if (null == fieldValue) {
              row.setTime(i, null);
            } else if (unsupportedClassTypeName.contains(fieldValue.getClass().getName())) {
              row.setTime(i, null);
            } else {
              LocalTime val = null;
              try {
                val = ObjectCastUtils.castToLocalTime(fieldValue);
              } catch (RuntimeException e) {
                throw new RuntimeException(String.format("表名[%s.%s]的字段名[%s]数据类型转错误，%s",
                    schemaName, tableName, fieldName, e.getMessage()));
              }

              if (null == val) {
                throw new RuntimeException(String.format(
                    "表名[%s.%s]的字段名[%s]数据类型转换错误，应该为java.sql.Time，而实际的数据类型为%s", schemaName,
                    tableName, fieldName, fieldValue.getClass().getName()));
              }
              row.setTime(i, val);
            }
            break;
          case Types.DATE:
            if (null == fieldValue) {
              row.setDate(i, null);
            } else if (unsupportedClassTypeName.contains(fieldValue.getClass().getName())) {
              row.setDate(i, null);
            } else {
              LocalDate val = null;
              try {
                val = ObjectCastUtils.castToLocalDate(fieldValue);
              } catch (RuntimeException e) {
                throw new RuntimeException(String.format("表名[%s.%s]的字段名[%s]数据类型转错误，%s",
                    schemaName, tableName, fieldName, e.getMessage()));
              }

              if (null == val) {
                throw new RuntimeException(String.format(
                    "表名[%s.%s]的字段名[%s]数据类型转换错误，应该为java.sql.Date，而实际的数据类型为%s", schemaName,
                    tableName, fieldName, fieldValue.getClass().getName()));
              }
              row.setDate(i, val);
            }
            break;
          case Types.TIMESTAMP:
            if (null == fieldValue) {
              row.setTimeStamp(i, null);
            } else if (unsupportedClassTypeName.contains(fieldValue.getClass().getName())) {
              row.setTimeStamp(i, null);
            } else {
              LocalDateTime val = null;
              try {
                val = ObjectCastUtils.castToLocalDateTime(fieldValue);
              } catch (RuntimeException e) {
                throw new RuntimeException(String.format("表名[%s.%s]的字段名[%s]数据类型转错误，%s",
                    schemaName, tableName, fieldName, e.getMessage()));
              }

              if (null == val) {
                throw new RuntimeException(String.format(
                    "表名[%s.%s]的字段名[%s]数据类型错误，应该为java.sql.Timestamp，而实际的数据类型为%s", schemaName,
                    tableName, fieldName, fieldValue.getClass().getName()));
              }

              row.setTimeStamp(i, val);
            }
            break;
          case Types.BINARY:
          case Types.VARBINARY:
          case Types.BLOB:
          case Types.LONGVARBINARY:
            if (null == fieldValue) {
              row.setByteArray(i, null);
            } else {
              row.setByteArray(i, ObjectCastUtils.castToByteArray(fieldValue));
            }
            break;
          case Types.NULL:
          case Types.OTHER:
            if (null == fieldValue) {
              row.setText(i, null);
            } else {
              row.setText(i, fieldValue.toString());
            }
            break;
          default:
            throw new RuntimeException(
                String.format("不支持的数据库字段类型,表名[%s.%s] 字段名[%s].", schemaName,
                    tableName, fieldName));
        }
      }
    };
  }

}
