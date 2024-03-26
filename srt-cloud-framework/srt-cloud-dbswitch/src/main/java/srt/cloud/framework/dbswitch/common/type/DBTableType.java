// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.common.type;

/**
 * 数据库表类型:视图表、物理表
 *
 * @author jrl
 */
public enum DBTableType {
	/**
	 * 物理表
	 */
	TABLE(0),

	/**
	 * 视图表
	 */
	VIEW(1),

	/**
	 * 自定义sql
	 */
	SQL(2);

	private int index;

	DBTableType(int idx) {
		this.index = idx;
	}

	public int getIndex() {
		return index;
	}
}
