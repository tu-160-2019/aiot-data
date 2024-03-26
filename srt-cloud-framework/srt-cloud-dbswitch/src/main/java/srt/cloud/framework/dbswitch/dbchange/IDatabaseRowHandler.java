// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbchange;

import java.util.List;

/**
 * 计算结果行记录处理器
 *
 * @author jrl
 */
public interface IDatabaseRowHandler {

  /**
   * 行数据处理
   *
   * @param fields 字段名称列表，该列表只读
   * @param record 一条数据记实录
   * @param flag   数据变化状态
   */
  void handle(List<String> fields, Object[] record, RecordChangeTypeEnum flag);

  /**
   * 计算结束通知
   *
   * @param fields 字段名称列表，该列表只读
   */
  void destroy(List<String> fields);
}
