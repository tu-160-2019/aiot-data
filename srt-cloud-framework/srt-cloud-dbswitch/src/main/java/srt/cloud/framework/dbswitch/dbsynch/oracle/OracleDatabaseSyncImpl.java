// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbsynch.oracle;

import srt.cloud.framework.dbswitch.common.util.TypeConvertUtils;
import srt.cloud.framework.dbswitch.dbsynch.AbstractDatabaseSynchronize;
import srt.cloud.framework.dbswitch.dbsynch.IDatabaseSynchronize;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.SqlTypeValue;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Oracle数据库DML同步实现类
 *
 * @author jrl
 */
public class OracleDatabaseSyncImpl extends AbstractDatabaseSynchronize implements
		IDatabaseSynchronize {

  public OracleDatabaseSyncImpl(DataSource ds) {
    super(ds);
  }

  @Override
  public String getColumnMetaDataSql(String schemaName, String tableName) {
    return String.format("SELECT *  FROM \"%s\".\"%s\"  WHERE 1=2", schemaName, tableName);
  }

  @Override
  public String getInsertPrepareStatementSql(String schemaName, String tableName,
      List<String> fieldNames) {
    List<String> placeHolders = Collections.nCopies(fieldNames.size(), "?");
    return String.format("INSERT INTO \"%s\".\"%s\" ( \"%s\" ) VALUES ( %s )",
        schemaName, tableName,
        StringUtils.join(fieldNames, "\",\""),
        StringUtils.join(placeHolders, ","));
  }

  @Override
  public String getUpdatePrepareStatementSql(String schemaName, String tableName,
      List<String> fieldNames, List<String> pks) {
    List<String> uf = fieldNames.stream()
        .filter(field -> !pks.contains(field))
        .map(field -> String.format("\"%s\"=?", field))
        .collect(Collectors.toList());

    List<String> uw = pks.stream()
        .map(pk -> String.format("\"%s\"=?", pk))
        .collect(Collectors.toList());

    return String.format("UPDATE \"%s\".\"%s\" SET %s WHERE %s",
        schemaName, tableName, StringUtils.join(uf, " , "),
        StringUtils.join(uw, " AND "));
  }

  @Override
  public String getDeletePrepareStatementSql(String schemaName, String tableName,
      List<String> pks) {
    List<String> uw = pks.stream()
        .map(pk -> String.format("\"%s\"=?", pk))
        .collect(Collectors.toList());

    return String.format("DELETE FROM \"%s\".\"%s\" WHERE %s ",
        schemaName, tableName, StringUtils.join(uw, "  AND  "));
  }

  @Override
  public long executeInsert(List<Object[]> records) {
    List<InputStream> iss = new ArrayList<>();
    records.parallelStream().forEach((Object[] row) -> {
      for (int i = 0; i < row.length; ++i) {
        try {
          switch (this.insertArgsType[i]) {
            case Types.CLOB:
            case Types.NCLOB:
              row[i] = Objects.isNull(row[i])
                  ? null
                  : TypeConvertUtils.castToString(row[i]);
              break;
            case Types.BLOB:
              final byte[] bytes = Objects.isNull(row[i])
                  ? null
                  : TypeConvertUtils.castToByteArray(row[i]);
              row[i] = new SqlTypeValue() {
                @Override
                public void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType,
                    String typeName) throws SQLException {
                  if (null != bytes) {
                    InputStream is = new ByteArrayInputStream(bytes);
                    ps.setBlob(paramIndex, is);
                    iss.add(is);
                  } else {
                    ps.setNull(paramIndex, sqlType);
                  }
                }
              };
              break;
            case Types.ROWID:
            case Types.ARRAY:
            case Types.REF:
            case Types.SQLXML:
              row[i] = null;
              break;
            default:
              break;
          }
        } catch (Exception e) {
          row[i] = null;
        }
      }
    });

    try {
      return super.executeInsert(records);
    } finally {
      iss.forEach(is -> {
        try {
          is.close();
        } catch (Exception ignore) {
        }
      });
    }
  }

  @Override
  public long executeUpdate(List<Object[]> records) {
    List<InputStream> iss = new ArrayList<>();
    records.parallelStream().forEach((Object[] row) -> {
      for (int i = 0; i < row.length; ++i) {
        try {
          switch (this.updateArgsType[i]) {
            case Types.CLOB:
            case Types.NCLOB:
              row[i] = Objects.isNull(row[i])
                  ? null
                  : TypeConvertUtils.castToString(row[i]);
              break;
            case Types.BLOB:
              final byte[] bytes = Objects.isNull(row[i])
                  ? null
                  : TypeConvertUtils.castToByteArray(row[i]);
              row[i] = new SqlTypeValue() {
                @Override
                public void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType,
                    String typeName) throws SQLException {
                  if (null != bytes) {
                    InputStream is = new ByteArrayInputStream(bytes);
                    ps.setBlob(paramIndex, is);
                    iss.add(is);
                  } else {
                    ps.setNull(paramIndex, sqlType);
                  }
                }
              };
              break;
            case Types.ROWID:
            case Types.ARRAY:
            case Types.REF:
            case Types.SQLXML:
              row[i] = null;
              break;
            default:
              break;
          }
        } catch (Exception e) {
          row[i] = null;
        }
      }
    });

    try {
      return super.executeUpdate(records);
    } finally {
      iss.forEach(is -> {
        try {
          is.close();
        } catch (Exception ignore) {
        }
      });
    }
  }

  @Override
  public long executeDelete(List<Object[]> records) {
    return super.executeDelete(records);
  }
}
