// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.core.model;

import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;

/**
 * 数据库连接描述符信息定义(Database Description)
 *
 * @author jrl
 */
public class DatabaseDescription {

  protected ProductTypeEnum type;
  protected String host;
  protected int port;
  /**
   * 对于Oracle数据库的模式，可取范围为：sid,serviceName,TNSName三种
   */
  protected String mode;
  protected String dbname;
  protected String charset;
  protected String username;
  protected String password;

  public DatabaseDescription(String dbtype, String host, int port, String mode, String dbname,
      String charset, String username, String password) {
    this.type = ProductTypeEnum.valueOf(dbtype.toUpperCase());
    this.host = host;
    this.port = port;
    this.mode = mode;
    this.dbname = dbname;
    this.charset = charset;
    this.username = username;
    this.password = password;
  }

  public ProductTypeEnum getType() {
    return type;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getMode() {
    return mode;
  }

  public String getDbname() {
    return dbname;
  }

  public String getCharset() {
    return charset;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  @Override
  public String toString() {
    return "DatabaseDescription [type=" + type + ", host=" + host + ", port=" + port + ", mode="
        + mode + ", dbname=" + dbname + ", charset=" + charset + ", username=" + username
        + ", password=" + password + "]";
  }

}
