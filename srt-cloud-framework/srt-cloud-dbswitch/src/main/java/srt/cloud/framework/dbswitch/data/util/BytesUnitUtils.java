// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.data.util;

import java.text.DecimalFormat;

/**
 * 字节单位转换
 *
 * @author jrl
 */
public final class BytesUnitUtils {

  public static String bytesSizeToHuman(long size) {
    /** 定义GB的计算常量 */
    long GB = 1024 * 1024 * 1024;
    /** 定义MB的计算常量 */
    long MB = 1024 * 1024;
    /** 定义KB的计算常量 */
    long KB = 1024;

    /** 格式化小数 */
    DecimalFormat df = new DecimalFormat("0.00");
    String resultSize = "0.00";

    if (size / GB >= 1) {
      //如果当前Byte的值大于等于1GB
      resultSize = df.format(size / (float) GB) + "GB ";
    } else if (size / MB >= 1) {
      //如果当前Byte的值大于等于1MB
      resultSize = df.format(size / (float) MB) + "MB ";
    } else if (size / KB >= 1) {
      //如果当前Byte的值大于等于1KB
      resultSize = df.format(size / (float) KB) + "KB ";
    } else {
      resultSize = size + "B ";
    }

    return resultSize;
  }

  private BytesUnitUtils() {
  }
}
