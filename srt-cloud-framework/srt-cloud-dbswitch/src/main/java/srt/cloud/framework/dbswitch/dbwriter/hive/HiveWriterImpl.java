// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbwriter.hive;

import lombok.extern.slf4j.Slf4j;
import srt.cloud.framework.dbswitch.dbwriter.IDatabaseWriter;
import srt.cloud.framework.dbswitch.dbwriter.mysql.MySqlWriterImpl;

import javax.sql.DataSource;

/**
 * DB2数据库写入实现类
 *
 * @author jrl
 */
@Slf4j
public class HiveWriterImpl extends MySqlWriterImpl implements IDatabaseWriter {

	public HiveWriterImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	protected String getDatabaseProductName() {
		return "Hive";
	}

}
