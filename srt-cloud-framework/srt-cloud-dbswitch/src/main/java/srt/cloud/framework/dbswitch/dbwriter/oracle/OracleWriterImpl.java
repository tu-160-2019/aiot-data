// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbwriter.oracle;

import lombok.extern.slf4j.Slf4j;
import srt.cloud.framework.dbswitch.common.util.TypeConvertUtils;
import srt.cloud.framework.dbswitch.dbwriter.AbstractDatabaseWriter;
import srt.cloud.framework.dbswitch.dbwriter.IDatabaseWriter;
import org.springframework.jdbc.core.SqlTypeValue;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Oracle数据库写入实现类
 *
 * @author jrl
 */
@Slf4j
public class OracleWriterImpl extends AbstractDatabaseWriter implements IDatabaseWriter {

  public OracleWriterImpl(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  protected String getDatabaseProductName() {
    return "Oracle";
  }

  @Override
  public long write(List<String> fieldNames, List<Object[]> recordValues) {
    /**
     * 将java.sql.Array 类型转换为java.lang.String
     * <p>
     *  Oracle 没有数组类型，这里以文本类型进行存在
     * <p>
     *  Oracle的CLOB和BLOB类型写入请见：
     *  <p>
     *  oracle.jdbc.driver.OraclePreparedStatement.setObjectCritical
     */
    List<InputStream> iss = new ArrayList<>();
    recordValues.parallelStream().forEach((Object[] row) -> {
      for (int i = 0; i < row.length; ++i) {
        try {
          int dataType = this.columnType.get(fieldNames.get(i));
          switch (dataType) {
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
      return super.write(fieldNames, recordValues);
    } finally {
      iss.forEach(is -> {
        try {
          is.close();
        } catch (Exception ignore) {
        }
      });
    }
  }
}
