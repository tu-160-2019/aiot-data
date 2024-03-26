package srt.cloud.framework.dbswitch.common.type;// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////

import java.util.Arrays;

/**
 * 数据库产品类型的枚举定义
 *
 * @author Tang
 */
public enum ProductTypeEnum {
	/**
	 * 未知数据库类型
	 */
	UNKNOWN(0, "\"", null, null, null),

	/**
	 * MySQL数据库类型
	 */
	MYSQL(1, "`", "com.mysql.jdbc.Driver", "/* ping */ SELECT 1", "jdbc:mysql://{host}:{port}/{database}?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true"),

	/**
	 * Oracle数据库类型
	 */
	ORACLE(2, "\"", "oracle.jdbc.driver.OracleDriver", "SELECT 'Hello' from DUAL", "jdbc:oracle:thin:@{host}:{port}:{database}"),

	/**
	 * SQLServer 2000数据库类型
	 */
	SQLSERVER2000(3, "\"", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "SELECT 1 + 2as a", "jdbc:sqlserver://{host}:{port};DatabaseName={database}"),

	/**
	 * SQLServer数据库类型
	 */
	SQLSERVER(4, "\"", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "SELECT 1+2 as a", "jdbc:sqlserver://{host}:{port};DatabaseName={database}"),

	/**
	 * PostgreSQL数据库类型
	 */
	POSTGRESQL(5, "\"", "org.postgresql.Driver", "SELECT 1", "jdbc:postgresql://{host}:{port}/{database}"),

	/**
	 * Greenplum数据库类型
	 */
	GREENPLUM(6, "\"", "com.pivotal.jdbc.GreenplumDriver", "SELECT 1", "jdbc:postgresql://{host}:{port}/{database}"),

	/**
	 * MariaDB数据库类型
	 */
	MARIADB(7, "`", "org.mariadb.jdbc.Driver", "SELECT 1", "jdbc:mariadb://{host}:{port}/{database}"),

	/**
	 * DB2数据库类型
	 */
	DB2(8, "\"", "com.ibm.db2.jcc.DB2Driver", "SELECT 1 FROM SYSIBM.SYSDUMMY1", "jdbc:db2://{host}:{port}/{database}"),

	/**
	 * [国产]达梦数据库类型
	 */
	DM(9, "\"", "dm.jdbc.driver.DmDriver", "SELECT 'Hello' from DUAL", "jdbc:dm://{host}:{port}/{database}"),

	/**
	 * [国产]人大金仓数据库类型
	 */
	KINGBASE(10, "\"", "com.kingbase8.Driver", "SELECT 1", "jdbc:kingbase8://{host}:{port}/{database}"),

	/**
	 * [国产]神通数据库
	 */
	OSCAR(11, "\"", "com.oscar.Driver", "SELECT 1", "jdbc:oscar://{host}:{port}/{database}"),

	/**
	 * [国产]南大通用GBase8a数据库
	 */
	GBASE8A(12, "`", "com.gbase.jdbc.Driver", "/* ping */ SELECT 1", "jdbc:gbase://{host}:{port}/{database}"),

	/**
	 * HIVE数据库
	 */
	HIVE(13, "`", "org.apache.hive.jdbc.HiveDriver", "SELECT 1", "jdbc:hive2://{host}:{port}/{database}"),

	/**
	 * SQLite数据库
	 */
	SQLITE3(14, "\"", "org.sqlite.JDBC", "SELECT 1", "jdbc:sqlite::resource:{file}"),

	/**
	 * Sybase数据库类型
	 */
	SYBASE(15, "\"", "com.sybase.jdbc4.jdbc.SybDriver", "SELECT 1+2 as a", "jdbc:sybase:Tds:{host}:{port}/{database}"),

	/**
	 * MySQL数据库类型
	 */
	DORIS(16, "`", "com.mysql.jdbc.Driver", "/* ping */ SELECT 1", "jdbc:mysql://{host}:{port}/{database}?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&rewriteBatchedStatements=true"),

	/**
	 * MySQL数据库类型
	 */
	CLICKHOUSE(17, "`", "com.clickhouse.jdbc.ClickHouseDriver", "SELECT 1", "jdbc:clickhouse://{host}:{port}/{database}"),

	/**
	 * MySQL数据库类型
	 */
	MONGODB(18, "\"", "com.gitee.jdbc.mongodb.JdbcDriver", "use admin;", "jdbc:mongodb://{host}:{port}/{database}?authSource=admin"),
	;

	private Integer index;
	private String quote;
	private String driveClassName;
	private String testSql;
	private String url;

	public String getTestSql() {
		return testSql;
	}

	public String getUrl() {
		return url;
	}

	public String getDriveClassName() {
		return driveClassName;
	}

	ProductTypeEnum(Integer idx, String quote, String driveClassName, String testSql, String url) {
		this.index = idx;
		this.quote = quote;
		this.driveClassName = driveClassName;
		this.testSql = testSql;
		this.url = url;
	}

	public Integer getIndex() {
		return index;
	}


	public static ProductTypeEnum getByIndex(Integer index) {
		return Arrays.stream(ProductTypeEnum.values()).filter(productTypeEnum -> productTypeEnum.getIndex().equals(index)).findFirst().orElse(ProductTypeEnum.UNKNOWN);
	}


	public String quoteName(String name) {
		return String.format("%s%s%s", quote, name, quote);
	}

	public String quoteSchemaTableName(String schema, String table) {
		return String.format("%s%s%s.%s%s%s", quote, schema, quote, quote, table, quote);
	}

	/**
	 * 类似于PostgreSQL系列的数据库类型
	 *
	 * @return boolean
	 */
	public boolean isLikePostgres() {
		return this == POSTGRESQL || this == KINGBASE || this == GREENPLUM;
	}

	/**
	 * 类似于MySQL系列的数据库类型
	 *
	 * @return boolean
	 */
	public boolean isLikeMysql() {
		return this == MYSQL || this == MARIADB || this == GBASE8A;
	}

	/**
	 * 类似于Oracle系列的数据库类型
	 *
	 * @return boolean
	 */
	public boolean isLikeOracle() {
		return this == ORACLE || this == DM;
	}

	/**
	 * 类似于SQL Server系列的数据库类型
	 *
	 * @return boolean
	 */
	public boolean isLikeSqlServer() {
		return this == SQLSERVER || this == SYBASE;
	}

	/**
	 * 类似于Hive系列的数据库类型
	 *
	 * @return boolean
	 */
	public boolean isLikeHive() {
		return this == HIVE;
	}

	/**
	 * 是否为MongoDB数据库类型
	 *
	 * @return boolean
	 */
	public boolean isMongodb() {
		return this == MONGODB;
	}

	/**
	 * 是否为ClickHouse数据库类型
	 *
	 * @return boolean
	 */
	public boolean isClickHouse() {
		return this == CLICKHOUSE;
	}

	public boolean noCommentStatement() {
		return Arrays.asList(
				ProductTypeEnum.MYSQL,
				ProductTypeEnum.MARIADB,
				ProductTypeEnum.GBASE8A,
				ProductTypeEnum.HIVE,
				ProductTypeEnum.SQLITE3,
				ProductTypeEnum.SYBASE,
				ProductTypeEnum.DORIS
		).contains(this);
	}

}
