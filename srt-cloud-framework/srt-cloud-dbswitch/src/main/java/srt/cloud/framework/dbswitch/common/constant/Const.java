// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.common.constant;

/**
 * 常量定义
 *
 * @author jrl
 */
public final class Const {

  /**
   * What's the file systems file separator on this operating system?
   */
  public static final String FILE_SEPARATOR = System.getProperty("file.separator");

  /**
   * What's the path separator on this operating system?
   */
  public static final String PATH_SEPARATOR = System.getProperty("path.separator");

  /**
   * CR: operating systems specific Carriage Return
   */
  public static final String CR = System.getProperty("line.separator");

  /**
   * DOSCR: MS-DOS specific Carriage Return
   */
  public static final String DOSCR = "\n\r";

  /**
   * An empty ("") String.
   */
  public static final String EMPTY_STRING = "";

  /**
   * The Java runtime version
   */
  public static final String JAVA_VERSION = System.getProperty("java.vm.version");

  /**
   * Create Table Statement Prefix String
   */
  public static final String CREATE_TABLE = "CREATE TABLE ";

  /**
   * Drop Table Statement Prefix String
   */
  public static final String DROP_TABLE = "DROP TABLE ";

  /**
   * Constant Keyword String
   */
  public static final String IF_NOT_EXISTS = "IF NOT EXISTS ";

  /**
   * Constant Keyword String
   */
  public static final String IF_EXISTS = "IF EXISTS ";

  /**
   * Constructor Function
   */
  private Const() {
  }

}
