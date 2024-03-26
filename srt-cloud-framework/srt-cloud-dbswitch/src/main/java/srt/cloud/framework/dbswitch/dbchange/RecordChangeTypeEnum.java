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

/**
 * 记录变化状态枚举类
 *
 * @author jrl
 */
public enum RecordChangeTypeEnum {
  /**
   * 未变标识
   */
  VALUE_IDENTICAL(0, "identical"),

  /**
   * 更新标识
   */
  VALUE_CHANGED(1, "update"),

  /**
   * 插入标识
   */
  VALUE_INSERT(2, "insert"),

  /**
   * 删除标识
   */
  VALUE_DELETED(3, "delete");

  /**
   * index
   */
  private Integer index;

  /**
   * 状态标记
   */
  private String status;

  RecordChangeTypeEnum(int idx, String flag) {
    this.index = idx;
    this.status = flag;
  }

  public int getIndex() {
    return index;
  }

  public String getStatus() {
    return this.status;
  }

}
