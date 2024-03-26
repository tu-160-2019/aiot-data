// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbwriter.oscar;


import srt.cloud.framework.dbswitch.dbwriter.AbstractDatabaseWriter;
import srt.cloud.framework.dbswitch.dbwriter.IDatabaseWriter;
import srt.cloud.framework.dbswitch.dbwriter.util.ObjectCastUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * 神通数据库写入实现类
 *
 * @author tang
 */
public class OscarWriterImpl extends AbstractDatabaseWriter implements IDatabaseWriter {

  public OscarWriterImpl(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  protected String getDatabaseProductName() {
    return "Oscar";
  }

  @Override
  public long write(List<String> fieldNames, List<Object[]> recordValues) {
    recordValues.parallelStream().forEach((Object[] row) -> {
      for (int i = 0; i < row.length; ++i) {
        try {
          row[i] = ObjectCastUtils.castByDetermine(row[i]);
        } catch (Exception e) {
          row[i] = null;
        }
      }
    });

    return super.write(fieldNames, recordValues);
  }
}
