// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.sql.service;

import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;

import java.util.Map;

/**
 * SQL语言共分为四大类：数据查询语言DQL，数据操纵语言DML，数据定义语言DDL，数据控制语言DCL
 *
 * @author jrl
 *
 */
public interface ISqlConvertService {

	/**
	 * 标准DQL/DML类SQL的转换
	 *
	 * @param sql 待转换的SQL语句
	 * @return 转换为三种数据库Oracle/MySQL/PostgreSQL数据库类型后的SQL语句
	 */
	public Map<String, String> dmlSentence(String sql);

	/**
	 * 标准DQL/DML类SQL的转换
	 *
	 * @param sql 待转换的SQL语句
	 * @return 转换为指定数据库类型后的SQL语句
	 */
	public String dmlSentence(String sql, ProductTypeEnum target);

	/**
	 * 指定源数据库到目的数据库的DQL/DML类SQL的转换
	 *
	 * @param source 源数据库类型
	 * @param target 目的数据库类型
	 * @param sql    待转换的SQL语句
	 * @return 转换为目的数据库类型后的SQL语句
	 */
	public String dmlSentence(ProductTypeEnum source, ProductTypeEnum target, String sql);

	/**
	 * 标准DDL类SQL的转换
	 *
	 * @param sql 待转换的SQL语句
	 * @return 转换为三种数据库Oracle/MySQL/PostgreSQL数据库类型后的SQL语句
	 */
	public Map<String, String> ddlSentence(String sql);

	/**
	 * 标准DDL类SQL的转换
	 *
	 * @param sql 待转换的SQL语句
	 * @return 转换为指定数据库类型后的SQL语句
	 */
	public String ddlSentence(String sql, ProductTypeEnum target);

	/**
	 * 指定源数据库到目的数据库的DDL类SQL的转换
	 *
	 * @param source 源数据库类型
	 * @param target 目的数据库类型
	 * @param sql    待转换的SQL语句
	 * @return 转换为目的数据库类型后的SQL语句
	 */
	public String ddlSentence(ProductTypeEnum source, ProductTypeEnum target, String sql);

	/**
	 * 标准DCL类SQL的转换
	 *
	 * @param sql 待转换的SQL语句
	 * @return 转换为三种数据库Oracle/MySQL/PostgreSQL数据库类型后的SQL语句
	 */
	public Map<String, String> dclSentence(String sql);

	/**
	 * 指定源数据库到目的数据库的DCL类SQL的转换
	 *
	 * @param source 源数据库类型
	 * @param target 目的数据库类型
	 * @param sql    待转换的SQL语句
	 * @return 转换为目的数据库类型后的SQL语句
	 */
	public String dclSentence(ProductTypeEnum source, ProductTypeEnum target, String sql);
}
