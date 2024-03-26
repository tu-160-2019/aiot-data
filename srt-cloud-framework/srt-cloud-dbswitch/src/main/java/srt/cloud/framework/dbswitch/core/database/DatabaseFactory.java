// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.core.database;

import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.core.database.impl.DatabaseDB2Impl;
import srt.cloud.framework.dbswitch.core.database.impl.DatabaseDmImpl;
import srt.cloud.framework.dbswitch.core.database.impl.DatabaseDorisImpl;
import srt.cloud.framework.dbswitch.core.database.impl.DatabaseGbase8aImpl;
import srt.cloud.framework.dbswitch.core.database.impl.DatabaseGreenplumImpl;
import srt.cloud.framework.dbswitch.core.database.impl.DatabaseHiveImpl;
import srt.cloud.framework.dbswitch.core.database.impl.DatabaseKingbaseImpl;
import srt.cloud.framework.dbswitch.core.database.impl.DatabaseMariaDBImpl;
import srt.cloud.framework.dbswitch.core.database.impl.DatabaseMongoDBImpl;
import srt.cloud.framework.dbswitch.core.database.impl.DatabaseMysqlImpl;
import srt.cloud.framework.dbswitch.core.database.impl.DatabaseOracleImpl;
import srt.cloud.framework.dbswitch.core.database.impl.DatabaseOscarImpl;
import srt.cloud.framework.dbswitch.core.database.impl.DatabasePostgresImpl;
import srt.cloud.framework.dbswitch.core.database.impl.DatabaseSqliteImpl;
import srt.cloud.framework.dbswitch.core.database.impl.DatabaseSqlserver2000Impl;
import srt.cloud.framework.dbswitch.core.database.impl.DatabaseSqlserverImpl;
import srt.cloud.framework.dbswitch.core.database.impl.DatabaseSybaseImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 数据库实例构建工厂类
 *
 * @author jrl
 */
public final class DatabaseFactory {

	private static final Map<ProductTypeEnum, Callable<AbstractDatabase>> DATABASE_MAPPER
			= new HashMap<ProductTypeEnum, Callable<AbstractDatabase>>() {

		private static final long serialVersionUID = 9202705534880971997L;

		{
			put(ProductTypeEnum.MYSQL, DatabaseMysqlImpl::new);
			put(ProductTypeEnum.MARIADB, DatabaseMariaDBImpl::new);
			put(ProductTypeEnum.ORACLE, DatabaseOracleImpl::new);
			put(ProductTypeEnum.SQLSERVER2000, DatabaseSqlserver2000Impl::new);
			put(ProductTypeEnum.SQLSERVER, DatabaseSqlserverImpl::new);
			put(ProductTypeEnum.POSTGRESQL, DatabasePostgresImpl::new);
			put(ProductTypeEnum.GREENPLUM, DatabaseGreenplumImpl::new);
			put(ProductTypeEnum.DB2, DatabaseDB2Impl::new);
			put(ProductTypeEnum.DM, DatabaseDmImpl::new);
			put(ProductTypeEnum.SYBASE, DatabaseSybaseImpl::new);
			put(ProductTypeEnum.KINGBASE, DatabaseKingbaseImpl::new);
			put(ProductTypeEnum.OSCAR, DatabaseOscarImpl::new);
			put(ProductTypeEnum.GBASE8A, DatabaseGbase8aImpl::new);
			put(ProductTypeEnum.HIVE, DatabaseHiveImpl::new);
			put(ProductTypeEnum.SQLITE3, DatabaseSqliteImpl::new);
			put(ProductTypeEnum.DORIS, DatabaseDorisImpl::new);
			put(ProductTypeEnum.MONGODB, DatabaseMongoDBImpl::new);
		}
	};

	public static AbstractDatabase getDatabaseInstance(ProductTypeEnum type) {
		Callable<AbstractDatabase> callable = DATABASE_MAPPER.get(type);
		if (null != callable) {
			try {
				return callable.call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		throw new UnsupportedOperationException(
				String.format("Unknown database type (%s)", type.name()));
	}

	private DatabaseFactory() {
		throw new IllegalStateException();
	}

}
