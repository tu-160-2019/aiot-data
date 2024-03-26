// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbsynch;

import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.DatabaseAwareUtils;
import srt.cloud.framework.dbswitch.dbsynch.db2.DB2DatabaseSyncImpl;
import srt.cloud.framework.dbswitch.dbsynch.dm.DmDatabaseSyncImpl;
import srt.cloud.framework.dbswitch.dbsynch.doris.DorisDatabaseSyncImpl;
import srt.cloud.framework.dbswitch.dbsynch.hive.HiveDatabaseSyncImpl;
import srt.cloud.framework.dbswitch.dbsynch.kingbase.KingbaseDatabaseSyncImpl;
import srt.cloud.framework.dbswitch.dbsynch.mongodb.MongoDatabaseSyncImpl;
import srt.cloud.framework.dbswitch.dbsynch.mssql.SqlServerDatabaseSyncImpl;
import srt.cloud.framework.dbswitch.dbsynch.mysql.MySqlDatabaseSyncImpl;
import srt.cloud.framework.dbswitch.dbsynch.oracle.OracleDatabaseSyncImpl;
import srt.cloud.framework.dbswitch.dbsynch.oscar.OscarDatabaseSyncImpl;
import srt.cloud.framework.dbswitch.dbsynch.pgsql.GreenplumDatabaseSyncImpl;
import srt.cloud.framework.dbswitch.dbsynch.pgsql.PostgresqlDatabaseSyncImpl;
import srt.cloud.framework.dbswitch.dbsynch.sqlite.Sqlite3DatabaseSyncImpl;
import srt.cloud.framework.dbswitch.dbsynch.sybase.SybaseDatabaseSyncImpl;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 数据库同步器构造工厂类
 *
 * @author jrl
 */
public final class DatabaseSynchronizeFactory {

	private static final Map<ProductTypeEnum, Function<DataSource, IDatabaseSynchronize>> DATABASE_SYNC_MAPPER
			= new HashMap<ProductTypeEnum, Function<DataSource, IDatabaseSynchronize>>() {

		private static final long serialVersionUID = -2359773637275934408L;

		{
			put(ProductTypeEnum.MYSQL, MySqlDatabaseSyncImpl::new);
			put(ProductTypeEnum.MARIADB, MySqlDatabaseSyncImpl::new);
			put(ProductTypeEnum.ORACLE, OracleDatabaseSyncImpl::new);
			put(ProductTypeEnum.SQLSERVER, SqlServerDatabaseSyncImpl::new);
			put(ProductTypeEnum.SQLSERVER2000, SqlServerDatabaseSyncImpl::new);
			put(ProductTypeEnum.POSTGRESQL, PostgresqlDatabaseSyncImpl::new);
			put(ProductTypeEnum.GREENPLUM, GreenplumDatabaseSyncImpl::new);
			put(ProductTypeEnum.DB2, DB2DatabaseSyncImpl::new);
			put(ProductTypeEnum.DM, DmDatabaseSyncImpl::new);
			put(ProductTypeEnum.SYBASE, SybaseDatabaseSyncImpl::new);
			put(ProductTypeEnum.KINGBASE, KingbaseDatabaseSyncImpl::new);
			put(ProductTypeEnum.OSCAR, OscarDatabaseSyncImpl::new);
			put(ProductTypeEnum.GBASE8A, MySqlDatabaseSyncImpl::new);
			put(ProductTypeEnum.SQLITE3, Sqlite3DatabaseSyncImpl::new);
			put(ProductTypeEnum.DORIS, DorisDatabaseSyncImpl::new);
			put(ProductTypeEnum.MONGODB, MongoDatabaseSyncImpl::new);
			put(ProductTypeEnum.HIVE, HiveDatabaseSyncImpl::new);
		}
	};

	/**
	 * 获取指定数据源的同步器
	 *
	 * @param dataSource 数据源
	 * @return 同步器对象
	 */
	public static IDatabaseSynchronize createDatabaseWriter(DataSource dataSource) {
		ProductTypeEnum type = DatabaseAwareUtils.getDatabaseTypeByDataSource(dataSource);
		if (!DATABASE_SYNC_MAPPER.containsKey(type)) {
			throw new RuntimeException(
					String.format("[dbsynch] Unsupported database type (%s)", type));
		}

		return DATABASE_SYNC_MAPPER.get(type).apply(dataSource);
	}

	/**
	 * 获取指定数据源的同步器
	 *
	 * @param dataSource 数据源
	 * @return 同步器对象
	 */
	public static IDatabaseSynchronize createDatabaseWriter(DataSource dataSource, ProductTypeEnum productType) {
		if (!DATABASE_SYNC_MAPPER.containsKey(productType)) {
			throw new RuntimeException(
					String.format("[dbsynch] Unsupported database type (%s)", productType));
		}
		return DATABASE_SYNC_MAPPER.get(productType).apply(dataSource);
	}
}
