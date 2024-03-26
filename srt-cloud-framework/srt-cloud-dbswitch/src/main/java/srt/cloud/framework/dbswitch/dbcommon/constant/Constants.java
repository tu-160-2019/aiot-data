// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbcommon.constant;

/**
 * 常量值定义
 *
 * @author jrl
 */
public final class Constants {


	public static final int CLOB_LENGTH = 9999999;
	/**
	 * 默认的JDBC数据查询超时时间（单位：秒）
	 */
	public static Integer DEFAULT_QUERY_TIMEOUT_SECONDS = 1 * 60 * 60;

	/**
	 * 默认的fetch-size的值
	 */
	public static int DEFAULT_FETCH_SIZE = 1000;

	/**
	 * fetch-size的最小有效值
	 */
	public static int MINIMUM_FETCH_SIZE = 100;
}
