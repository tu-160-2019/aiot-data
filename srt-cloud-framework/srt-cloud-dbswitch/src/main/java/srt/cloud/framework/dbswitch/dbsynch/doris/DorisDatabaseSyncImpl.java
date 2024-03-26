// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbsynch.doris;

import lombok.extern.slf4j.Slf4j;
import srt.cloud.framework.dbswitch.common.util.TypeConvertUtils;
import srt.cloud.framework.dbswitch.dbsynch.IDatabaseSynchronize;
import srt.cloud.framework.dbswitch.dbsynch.mysql.MySqlDatabaseSyncImpl;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * MySQL数据库DML同步实现类
 *
 * @author jrl
 */
@Slf4j
public class DorisDatabaseSyncImpl extends MySqlDatabaseSyncImpl implements
		IDatabaseSynchronize {

	public DorisDatabaseSyncImpl(DataSource ds) {
		super(ds);
	}

	@Override
	public long executeInsert(List<Object[]> records) {
		try {
			jdbcTemplate.execute("set enable_insert_strict = true");
		} catch (Exception e) {
			log.error("doris [set enable_insert_strict] error", e);
		}
		return super.executeInsert(records);
	}

	@Override
	protected void batchDelete(List<Object[]> recordValues, String batchSql, int[] argTypes) {
		udOperator(recordValues, batchSql, argTypes);
	}

	@Override
	protected void batchUpdate(List<Object[]> recordValues, String batchSql, int[] argTypes) {
		udOperator(recordValues, batchSql, argTypes);
	}

	/**
	 * doris批量操作会报错,不支持批量更新和删除
	 * @param recordValues
	 * @param batchSql
	 * @param argTypes
	 */
	private void udOperator(List<Object[]> recordValues, String batchSql, int[] argTypes) {
		try (Connection connection = dataSource.getConnection();
			 PreparedStatement ps = connection.prepareStatement(batchSql);) {
			for (Object[] recordValue : recordValues) {
				for (int j = 0; j < argTypes.length; j++) {
					ps.setObject(j + 1, recordValue[j], argTypes[j]);
				}
				ps.executeUpdate();
			}
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}
}
