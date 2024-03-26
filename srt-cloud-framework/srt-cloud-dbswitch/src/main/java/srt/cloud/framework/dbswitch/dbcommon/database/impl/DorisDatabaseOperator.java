// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbcommon.database.impl;

import org.apache.commons.lang3.StringUtils;
import srt.cloud.framework.dbswitch.dbcommon.database.IDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.domain.StatementResultSet;

import javax.sql.DataSource;
import java.util.List;

/**
 * MySQL数据库实现类
 *
 * @author jrl
 */
public class DorisDatabaseOperator extends MysqlDatabaseOperator implements IDatabaseOperator {

	public DorisDatabaseOperator(DataSource dataSource) {
		super(dataSource);
	}

	/**
	 * doris 使用 order by 会在后面自动加 limit 65535，导致查询的数据不全
	 *
	 * @param schemaName 模式名称
	 * @param tableName  表名称
	 * @param fields     字段列表
	 * @param orders     排序字段列表
	 * @return
	 */
	@Override
	public StatementResultSet queryTableData(String schemaName, String tableName, List<String> fields,
											 List<String> orders) {
		String sql = String.format("select `%s` from `%s`.`%s` order by `%s` asc limit %s",
				StringUtils.join(fields, "`,`"),
				schemaName, tableName, StringUtils.join(orders, "`,`"), Long.MAX_VALUE);
		return this.selectTableData(sql, Integer.MIN_VALUE);
	}

	@Override
	public StatementResultSet queryTableData(String sql, List<String> fields, List<String> orders) {
		sql = String.format("select `%s` from (%s) t order by `%s` asc limit %s",
				StringUtils.join(fields, "`,`"),
				sql, StringUtils.join(orders, "`,`"), Long.MAX_VALUE);
		return this.selectTableData(sql, Integer.MIN_VALUE);
	}

}
