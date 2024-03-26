// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbcommon.database;

import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.DatabaseAwareUtils;
import srt.cloud.framework.dbswitch.dbcommon.database.impl.DB2DatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.database.impl.DmDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.database.impl.DorisDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.database.impl.GreenplumDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.database.impl.HiveDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.database.impl.KingbaseDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.database.impl.MongoDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.database.impl.MysqlDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.database.impl.OracleDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.database.impl.OscarDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.database.impl.PostgreSqlDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.database.impl.SqlServerDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.database.impl.SqliteDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.database.impl.SybaseDatabaseOperator;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 数据库操作器构造工厂类
 *
 * @author jrl
 */
public final class DatabaseOperatorFactory {

	private static final Map<ProductTypeEnum, Function<DataSource, IDatabaseOperator>> DATABASE_OPERATOR_MAPPER
			= new HashMap<ProductTypeEnum, Function<DataSource, IDatabaseOperator>>() {

		private static final long serialVersionUID = -5278835613240515265L;

		{
			put(ProductTypeEnum.MYSQL, MysqlDatabaseOperator::new);
			put(ProductTypeEnum.MARIADB, MysqlDatabaseOperator::new);
			put(ProductTypeEnum.ORACLE, OracleDatabaseOperator::new);
			put(ProductTypeEnum.SQLSERVER, SqlServerDatabaseOperator::new);
			put(ProductTypeEnum.SQLSERVER2000, SqlServerDatabaseOperator::new);
			put(ProductTypeEnum.POSTGRESQL, PostgreSqlDatabaseOperator::new);
			put(ProductTypeEnum.GREENPLUM, GreenplumDatabaseOperator::new);
			put(ProductTypeEnum.DB2, DB2DatabaseOperator::new);
			put(ProductTypeEnum.DM, DmDatabaseOperator::new);
			put(ProductTypeEnum.SYBASE, SybaseDatabaseOperator::new);
			put(ProductTypeEnum.KINGBASE, KingbaseDatabaseOperator::new);
			put(ProductTypeEnum.OSCAR, OscarDatabaseOperator::new);
			put(ProductTypeEnum.GBASE8A, MysqlDatabaseOperator::new);
			put(ProductTypeEnum.HIVE, HiveDatabaseOperator::new);
			put(ProductTypeEnum.SQLITE3, SqliteDatabaseOperator::new);
			put(ProductTypeEnum.DORIS, DorisDatabaseOperator::new);
			put(ProductTypeEnum.MONGODB, MongoDatabaseOperator::new);
		}
	};

	/**
	 * 根据数据源获取数据的读取操作器
	 *
	 * @param dataSource 数据库源
	 * @return 指定类型的数据库读取器
	 */
	public static IDatabaseOperator createDatabaseOperator(DataSource dataSource) {
		ProductTypeEnum type = DatabaseAwareUtils.getDatabaseTypeByDataSource(dataSource);
		if (!DATABASE_OPERATOR_MAPPER.containsKey(type)) {
			throw new RuntimeException(
					String.format("[dbcommon] Unsupported database type (%s)", type));
		}

		return DATABASE_OPERATOR_MAPPER.get(type).apply(dataSource);
	}

	/**
	 * 根据数据源获取数据的读取操作器
	 *
	 * @param dataSource 数据库源
	 * @return 指定类型的数据库读取器
	 */
	public static IDatabaseOperator createDatabaseOperator(DataSource dataSource, ProductTypeEnum productType) {
		if (!DATABASE_OPERATOR_MAPPER.containsKey(productType)) {
			throw new RuntimeException(
					String.format("[dbcommon] Unsupported database type (%s)", productType));
		}

		return DATABASE_OPERATOR_MAPPER.get(productType).apply(dataSource);
	}

}
