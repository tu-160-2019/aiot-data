// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 字符串工具类
 *
 * @author jrl
 * @date 2021/6/8 20:55
 */
public final class DbswitchStrUtils {

  /**
   * 根据逗号切分字符串为数组
   *
   * @param str 待切分的字符串
   * @return List
   */
  public static List<String> stringToList(String str) {
    if (null != str && str.length() > 0) {
      String[] strs = str.split(",");
      if (strs.length > 0) {
        return new ArrayList<>(Arrays.asList(strs));
      }
    }

    return new ArrayList<>();
  }


  /**
   * 将二进制数据转换为16进制的可视化字符串
   *
   * @param bytes 二进制数据
   * @return 16进制的可视化字符串
   */
  public static String toHexString(byte[] bytes) {
    if (null == bytes || bytes.length <= 0) {
      return null;
    }
    final StringBuilder hexString = new StringBuilder();
    for (byte b : bytes) {
      int v = b & 0xFF;
      String s = Integer.toHexString(v);
      if (s.length() < 2) {
        hexString.append(0);
      }
      hexString.append(s);
    }
    return hexString.toString();
  }

  private DbswitchStrUtils() {
  }
}
