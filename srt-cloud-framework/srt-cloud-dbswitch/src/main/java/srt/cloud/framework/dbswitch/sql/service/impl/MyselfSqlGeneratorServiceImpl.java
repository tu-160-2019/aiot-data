// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.sql.service.impl;

import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.sql.ddl.AbstractDatabaseDialect;
import srt.cloud.framework.dbswitch.sql.ddl.AbstractSqlDdlOperator;
import srt.cloud.framework.dbswitch.sql.ddl.pojo.TableDefinition;
import srt.cloud.framework.dbswitch.sql.ddl.sql.DdlSqlAlterTable;
import srt.cloud.framework.dbswitch.sql.ddl.sql.DdlSqlCreateTable;
import srt.cloud.framework.dbswitch.sql.ddl.sql.DdlSqlDropTable;
import srt.cloud.framework.dbswitch.sql.ddl.sql.DdlSqlTruncateTable;
import srt.cloud.framework.dbswitch.sql.ddl.sql.impl.GreenplumDialectImpl;
import srt.cloud.framework.dbswitch.sql.ddl.sql.impl.MySqlDialectImpl;
import srt.cloud.framework.dbswitch.sql.ddl.sql.impl.OracleDialectImpl;
import srt.cloud.framework.dbswitch.sql.ddl.sql.impl.PostgresDialectImpl;
import srt.cloud.framework.dbswitch.sql.service.ISqlGeneratorService;

import java.util.HashMap;
import java.util.Map;

/**
 * 拼接生成SQL实现类
 *
 * @author jrl
 *
 */
public class MyselfSqlGeneratorServiceImpl implements ISqlGeneratorService {

	private static final Map<ProductTypeEnum, String> DATABASE_MAPPER = new HashMap<ProductTypeEnum, String>();

	static {
		DATABASE_MAPPER.put(ProductTypeEnum.MYSQL, MySqlDialectImpl.class.getName());
		DATABASE_MAPPER.put(ProductTypeEnum.ORACLE, OracleDialectImpl.class.getName());
		DATABASE_MAPPER.put(ProductTypeEnum.POSTGRESQL, PostgresDialectImpl.class.getName());
		DATABASE_MAPPER.put(ProductTypeEnum.GREENPLUM, GreenplumDialectImpl.class.getName());
	}

	public static AbstractDatabaseDialect getDatabaseInstance(ProductTypeEnum type) {
		if (DATABASE_MAPPER.containsKey(type)) {
			String className = DATABASE_MAPPER.get(type);
			try {
				return (AbstractDatabaseDialect) Class.forName(className).newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		throw new RuntimeException(String.format("Unkown database type (%s)", type.name()));
	}

	@Override
	public String createTable(String dbType, TableDefinition t) {
		ProductTypeEnum type = ProductTypeEnum.valueOf(dbType.toUpperCase());
		AbstractDatabaseDialect dialect = getDatabaseInstance(type);
		AbstractSqlDdlOperator operator = new DdlSqlCreateTable(t);
		return operator.toSqlString(dialect);
	}

	@Override
	public String alterTable(String dbType, String handle, TableDefinition t){
		ProductTypeEnum type = ProductTypeEnum.valueOf(dbType.toUpperCase());
		AbstractDatabaseDialect dialect = getDatabaseInstance(type);
		AbstractSqlDdlOperator operator = new DdlSqlAlterTable(t,handle);
		return operator.toSqlString(dialect);
	}

	@Override
	public String dropTable(String dbType, TableDefinition t) {
		ProductTypeEnum type = ProductTypeEnum.valueOf(dbType.toUpperCase());
		AbstractDatabaseDialect dialect = getDatabaseInstance(type);
		AbstractSqlDdlOperator operator = new DdlSqlDropTable(t);
		return operator.toSqlString(dialect);
	}

	@Override
	public String truncateTable(String dbType, TableDefinition t) {
		ProductTypeEnum type = ProductTypeEnum.valueOf(dbType.toUpperCase());
		AbstractDatabaseDialect dialect = getDatabaseInstance(type);
		AbstractSqlDdlOperator operator = new DdlSqlTruncateTable(t);
		return operator.toSqlString(dialect);
	}

}
