// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbwriter;

import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.DatabaseAwareUtils;
import srt.cloud.framework.dbswitch.dbwriter.db2.DB2WriterImpl;
import srt.cloud.framework.dbswitch.dbwriter.dm.DmWriterImpl;
import srt.cloud.framework.dbswitch.dbwriter.doris.DorisWriterImpl;
import srt.cloud.framework.dbswitch.dbwriter.gpdb.GreenplumCopyWriterImpl;
import srt.cloud.framework.dbswitch.dbwriter.gpdb.GreenplumInsertWriterImpl;
import srt.cloud.framework.dbswitch.dbwriter.hive.HiveWriterImpl;
import srt.cloud.framework.dbswitch.dbwriter.kingbase.KingbaseInsertWriterImpl;
import srt.cloud.framework.dbswitch.dbwriter.mongodb.MongoWriterImpl;
import srt.cloud.framework.dbswitch.dbwriter.mssql.SqlServerWriterImpl;
import srt.cloud.framework.dbswitch.dbwriter.mysql.MySqlWriterImpl;
import srt.cloud.framework.dbswitch.dbwriter.oracle.OracleWriterImpl;
import srt.cloud.framework.dbswitch.dbwriter.oscar.OscarWriterImpl;
import srt.cloud.framework.dbswitch.dbwriter.sqlite.Sqlite3WriterImpl;
import srt.cloud.framework.dbswitch.dbwriter.sybase.SybaseWriterImpl;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 数据库写入器构造工厂类
 *
 * @author jrl
 */
public class DatabaseWriterFactory {

	private static final Map<ProductTypeEnum, Function<DataSource, IDatabaseWriter>> DATABASE_WRITER_MAPPER
			= new HashMap<ProductTypeEnum, Function<DataSource, IDatabaseWriter>>() {

		private static final long serialVersionUID = 3365136872693503697L;

		{
			put(ProductTypeEnum.MYSQL, MySqlWriterImpl::new);
			put(ProductTypeEnum.MARIADB, MySqlWriterImpl::new);
			put(ProductTypeEnum.ORACLE, OracleWriterImpl::new);
			put(ProductTypeEnum.SQLSERVER, SqlServerWriterImpl::new);
			put(ProductTypeEnum.SQLSERVER2000, SqlServerWriterImpl::new);
			put(ProductTypeEnum.POSTGRESQL, GreenplumInsertWriterImpl::new);
			put(ProductTypeEnum.GREENPLUM, GreenplumCopyWriterImpl::new);
			put(ProductTypeEnum.DB2, DB2WriterImpl::new);
			put(ProductTypeEnum.DM, DmWriterImpl::new);
			put(ProductTypeEnum.SYBASE, SybaseWriterImpl::new);
			//对于kingbase当前只能使用insert模式
			put(ProductTypeEnum.KINGBASE, KingbaseInsertWriterImpl::new);
			put(ProductTypeEnum.OSCAR, OscarWriterImpl::new);
			put(ProductTypeEnum.GBASE8A, MySqlWriterImpl::new);
			put(ProductTypeEnum.SQLITE3, Sqlite3WriterImpl::new);
			put(ProductTypeEnum.DORIS, DorisWriterImpl::new);
			put(ProductTypeEnum.MONGODB, MongoWriterImpl::new);
			put(ProductTypeEnum.HIVE, HiveWriterImpl::new);
		}
	};

	/**
	 * 获取指定数据库类型的写入器
	 *
	 * @param dataSource 连接池数据源
	 * @return 写入器对象
	 */
	public static IDatabaseWriter createDatabaseWriter(DataSource dataSource) {
		return DatabaseWriterFactory.createDatabaseWriter(dataSource, true);
	}

	/**
	 * 获取指定数据库类型的写入器
	 *
	 * @param dataSource 连接池数据源
	 * @param insert     对于GP/GP数据库来说是否使用insert引擎写入
	 * @return 写入器对象
	 */
	public static IDatabaseWriter createDatabaseWriter(DataSource dataSource, boolean insert) {
		ProductTypeEnum type = DatabaseAwareUtils.getDatabaseTypeByDataSource(dataSource);
		if (insert) {
			if (ProductTypeEnum.POSTGRESQL.equals(type)) {
				return new GreenplumInsertWriterImpl(dataSource);
			}
		}

		if (!DATABASE_WRITER_MAPPER.containsKey(type)) {
			throw new RuntimeException(
					String.format("[dbwrite] Unsupported database type (%s)", type));
		}

		return DATABASE_WRITER_MAPPER.get(type).apply(dataSource);
	}

	/**
	 * 获取指定数据库类型的写入器
	 *
	 * @param dataSource 连接池数据源
	 * @param insert     对于GP/GP数据库来说是否使用insert引擎写入
	 * @return 写入器对象
	 */
	public static IDatabaseWriter createDatabaseWriter(DataSource dataSource, ProductTypeEnum productType, boolean insert) {
		if (insert) {
			if (ProductTypeEnum.POSTGRESQL.equals(productType)) {
				return new GreenplumInsertWriterImpl(dataSource);
			}
		}

		if (!DATABASE_WRITER_MAPPER.containsKey(productType)) {
			throw new RuntimeException(
					String.format("[dbwrite] Unsupported database type (%s)", productType));
		}

		return DATABASE_WRITER_MAPPER.get(productType).apply(dataSource);
	}

}
