// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.core.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConnectionUtils {

  private static final int DEFAULT_LOGIN_TIMEOUT_SECONDS = 30;

  /**
   * 建立与数据库的连接
   *
   * @param jdbcUrl  JDBC连接串
   * @param username 用户名
   * @param password 密码
   * @return java.sql.Connection
   */
  public static Connection connect(String jdbcUrl, String username, String password) {
    /*
     * 超时时间设置问题： https://blog.csdn.net/lsunwing/article/details/79461217
     * https://blog.csdn.net/weixin_34405332/article/details/91664781
     */
    try {
      Properties props = new Properties();
      props.put("user", username);
      props.put("password", password);

      /**
       * Oracle在通过jdbc连接的时候需要添加一个参数来设置是否获取注释
       */
      if (jdbcUrl.trim().startsWith("jdbc:oracle:thin:@")) {
        props.put("remarksReporting", "true");
      }

      // 设置最大时间
      DriverManager.setLoginTimeout(DEFAULT_LOGIN_TIMEOUT_SECONDS);

      return DriverManager.getConnection(jdbcUrl, props);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
