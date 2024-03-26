// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.common.util;


import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库类型识别工具类
 *
 * @author tang
 */
public final class DatabaseAwareUtils {

	private static final Map<String, ProductTypeEnum> productNameMap;

	private static final Map<String, ProductTypeEnum> driverNameMap;

	static {
		productNameMap = new HashMap<>();
		driverNameMap = new HashMap<>();

		productNameMap.put("Greenplum", ProductTypeEnum.GREENPLUM);
		productNameMap.put("Microsoft SQL Server", ProductTypeEnum.SQLSERVER);
		productNameMap.put("DM DBMS", ProductTypeEnum.DM);
		productNameMap.put("KingbaseES", ProductTypeEnum.KINGBASE);
		productNameMap.put("Apache Hive", ProductTypeEnum.HIVE);
		productNameMap.put("MySQL", ProductTypeEnum.MYSQL);
		productNameMap.put("MariaDB", ProductTypeEnum.MARIADB);
		productNameMap.put("Oracle", ProductTypeEnum.ORACLE);
		productNameMap.put("PostgreSQL", ProductTypeEnum.POSTGRESQL);
		productNameMap.put("DB2 for Unix/Windows", ProductTypeEnum.DB2);
		productNameMap.put("Hive", ProductTypeEnum.HIVE);
		productNameMap.put("SQLite", ProductTypeEnum.SQLITE3);
		productNameMap.put("OSCAR", ProductTypeEnum.OSCAR);
		productNameMap.put("GBase", ProductTypeEnum.GBASE8A);
		productNameMap.put("Adaptive Server Enterprise", ProductTypeEnum.SYBASE);
		productNameMap.put("Doris", ProductTypeEnum.DORIS);

		driverNameMap.put("MySQL Connector Java", ProductTypeEnum.MYSQL);
		driverNameMap.put("MariaDB Connector/J", ProductTypeEnum.MARIADB);
		driverNameMap.put("Oracle JDBC driver", ProductTypeEnum.ORACLE);
		driverNameMap.put("PostgreSQL JDBC Driver", ProductTypeEnum.POSTGRESQL);
		driverNameMap.put("Kingbase8 JDBC Driver", ProductTypeEnum.KINGBASE);
		driverNameMap.put("IBM Data Server Driver for JDBC and SQLJ", ProductTypeEnum.DB2);
		driverNameMap.put("dm.jdbc.driver.DmDriver", ProductTypeEnum.DM);
		driverNameMap.put("Hive JDBC", ProductTypeEnum.HIVE);
		driverNameMap.put("SQLite JDBC", ProductTypeEnum.SQLITE3);
		driverNameMap.put("OSCAR JDBC DRIVER", ProductTypeEnum.OSCAR);
		driverNameMap.put("GBase JDBC Driver", ProductTypeEnum.GBASE8A);
		driverNameMap.put("jConnect (TM) for JDBC (TM)", ProductTypeEnum.SYBASE);
		driverNameMap.put("MySQL Connector Java Doris", ProductTypeEnum.DORIS);
	}

	/**
	 * 获取数据库的产品名
	 *
	 * @param dataSource 数据源
	 * @return 数据库产品名称字符串
	 */
	public static ProductTypeEnum getDatabaseTypeByDataSource(DataSource dataSource) {
		try (Connection connection = dataSource.getConnection()) {
			String productName = connection.getMetaData().getDatabaseProductName();
			String driverName = connection.getMetaData().getDriverName();
			if (driverNameMap.containsKey(driverName)) {
				return driverNameMap.get(driverName);
			}

			ProductTypeEnum type = productNameMap.get(productName);
			if (null == type) {
				throw new IllegalStateException("Unable to detect database type from data source instance");
			}
			return type;
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	/**
	 * 检查MySQL数据库表的存储引擎是否为Innodb
	 *
	 * @param schemaName schema名
	 * @param tableName  table名
	 * @param dataSource 数据源
	 * @return 为Innodb存储引擎时返回True, 否在为false
	 */
	public static boolean isMysqlInnodbStorageEngine(String schemaName, String tableName,
													 DataSource dataSource) {
		String sql = "SELECT count(*) as total FROM information_schema.tables "
				+ "WHERE table_schema=? AND table_name=? AND ENGINE='InnoDB'";
		try (Connection connection = dataSource.getConnection();
			 PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, schemaName);
			ps.setString(2, tableName);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}

			return false;
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	private DatabaseAwareUtils() {
	}

}
