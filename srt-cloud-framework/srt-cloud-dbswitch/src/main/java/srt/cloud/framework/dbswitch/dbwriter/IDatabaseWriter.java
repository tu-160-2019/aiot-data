// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbwriter;

import javax.sql.DataSource;
import java.util.List;

/**
 * 数据库批量写入定义接口
 *
 * @author jrl
 */
public interface IDatabaseWriter {

  /**
   * 获取数据源对象
   *
   * @return DataSource数据源对象
   */
  DataSource getDataSource();

  /**
   * 批量写入预处理
   *
   * @param schemaName schema名称
   * @param tableName  table名称
   */
  void prepareWrite(String schemaName, String tableName, List<String> fieldNames);

  /**
   * 批量数据写入
   *
   * @param fieldNames   字段名称列表
   * @param recordValues 数据记录
   * @return 返回实际写入的数据记录条数
   */
  long write(List<String> fieldNames, List<Object[]> recordValues);
}
