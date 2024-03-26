// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Data : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.core.service.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import net.srt.flink.common.result.SqlExplainResult;
import net.srt.flink.common.utils.LogUtil;
import net.srt.flink.process.context.ProcessContextHolder;
import net.srt.flink.process.model.ProcessEntity;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.StringUtil;
import srt.cloud.framework.dbswitch.core.database.AbstractDatabase;
import srt.cloud.framework.dbswitch.core.database.DatabaseFactory;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.FlinkColumnType;
import srt.cloud.framework.dbswitch.core.model.JdbcSelectResult;
import srt.cloud.framework.dbswitch.core.model.SchemaTableData;
import srt.cloud.framework.dbswitch.core.model.SchemaTableMeta;
import srt.cloud.framework.dbswitch.core.model.TableDescription;
import srt.cloud.framework.dbswitch.core.service.IMetaDataByJdbcService;
import srt.cloud.framework.dbswitch.core.util.ConnectionUtils;
import srt.cloud.framework.dbswitch.core.util.GenerateSqlUtils;
import srt.cloud.framework.dbswitch.core.util.SqlUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 使用JDBC连接串的元数据获取服务
 *
 * @author jrl
 */
public class MetaDataByJdbcServiceImpl implements IMetaDataByJdbcService {

	protected ProductTypeEnum dbType;
	protected AbstractDatabase database;

	public MetaDataByJdbcServiceImpl(ProductTypeEnum type) {
		this.dbType = type;
		this.database = DatabaseFactory.getDatabaseInstance(type);
	}

	@Override
	public ProductTypeEnum getDatabaseType() {
		return this.dbType;
	}

	@Override
	public List<String> querySchemaList(String jdbcUrl, String username, String password) {
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			return database.querySchemaList(connection);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public List<TableDescription> queryTableList(String jdbcUrl, String username, String password,
												 String schemaName) {
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			return database.queryTableList(connection, schemaName);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public String getTableDDL(String jdbcUrl, String username, String password, String schemaName,
							  String tableName) {
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			return database.getTableDDL(connection, schemaName, tableName);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public String getViewDDL(String jdbcUrl, String username, String password, String schemaName,
							 String tableName) {
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			return database.getViewDDL(connection, schemaName, tableName);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public List<ColumnDescription> queryTableColumnMeta(String jdbcUrl, String username,
														String password, String schemaName, String tableName) {
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			List<ColumnDescription> columnDescriptions = database.queryTableColumnMeta(connection, schemaName, tableName);
			database.setColumnDefaultValue(connection, schemaName, tableName, columnDescriptions);
			database.setColumnIndexInfo(connection, schemaName, tableName, columnDescriptions);
			return columnDescriptions;
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public List<ColumnDescription> queryTableColumnMetaOnly(String jdbcUrl, String username, String password, String schemaName, String tableName) {
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			return database.queryTableColumnMeta(connection, schemaName, tableName);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public List<ColumnDescription> querySqlColumnMeta(String jdbcUrl, String username,
													  String password, String querySql) {
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			return database.querySelectSqlColumnMeta(connection, querySql);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public List<String> queryTablePrimaryKeys(String jdbcUrl, String username, String password,
											  String schemaName, String tableName) {
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			return database.queryTablePrimaryKeys(connection, schemaName, tableName);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public SchemaTableMeta queryTableMeta(String jdbcUrl, String username, String password,
										  String schemaName, String tableName) {
		SchemaTableMeta tableMeta = new SchemaTableMeta();
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			TableDescription tableDesc = database.queryTableMeta(connection, schemaName, tableName);
			if (null == tableDesc) {
				throw new IllegalArgumentException("Table Or View Not Exist");
			}

			List<ColumnDescription> columns = database
					.queryTableColumnMeta(connection, schemaName, tableName);

			List<String> pks;
			String createSql;
			if (tableDesc.isViewTable()) {
				pks = Collections.emptyList();
				createSql = database.getViewDDL(connection, schemaName, tableName);
			} else {
				pks = database.queryTablePrimaryKeys(connection, schemaName, tableName);
				createSql = database.getTableDDL(connection, schemaName, tableName);
			}

			tableMeta.setSchemaName(schemaName);
			tableMeta.setTableName(tableName);
			tableMeta.setTableType(tableDesc.getTableType());
			tableMeta.setRemarks(tableDesc.getRemarks());
			tableMeta.setColumns(columns);
			tableMeta.setPrimaryKeys(pks);
			tableMeta.setCreateSql(createSql);

			return tableMeta;
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public SchemaTableData queryTableData(String jdbcUrl, String username, String password,
										  String schemaName, String tableName, int rowCount) {
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			return database.queryTableData(connection, schemaName, tableName, rowCount);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public SchemaTableData queryTableDataBySql(String jdbcUrl, String username, String password,
											   String sql, int rowCount) {
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			return database.queryTableDataBySql(connection, sql, rowCount);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public JdbcSelectResult queryDataBySql(String jdbcUrl, String dbType, String username, String password, String sql, Integer openTrans, int rowCount) {
		ProcessEntity process = ProcessContextHolder.getProcess();
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			return database.queryDataBySql(connection, dbType, sql, openTrans, rowCount);
		} catch (Exception se) {
			process.error(LogUtil.getError(se));
			process.infoEnd();
			throw new RuntimeException(se);
		}
	}

	@Override
	public JdbcSelectResult queryDataByApiSql(String jdbcUrl, String username, String password, String sql, Integer openTrans, String sqlSeparator, Map<String, Object> sqlParam, int rowCount) {
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			return database.queryDataByApiSql(connection, sql, openTrans, sqlSeparator, sqlParam, rowCount);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public List<SqlExplainResult> explain(String sql, String dbType) {
		ProcessEntity process = ProcessContextHolder.getProcess();
		List<SqlExplainResult> sqlExplainResults = new ArrayList<>();
		String current = null;
		process.info("Start check sql...");
		try {
			List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType.toLowerCase());
			for (SQLStatement item : stmtList) {
				current = item.toString();
				String type = item.getClass().getSimpleName();
				sqlExplainResults.add(SqlExplainResult.success(type, current, null));
			}
			process.info("Sql is correct.");

		} catch (Exception e) {
			//mongodb跳过检测
			if (ProductTypeEnum.MONGODB.name().equalsIgnoreCase(dbType)) {
				sqlExplainResults.add(SqlExplainResult.success("unexpectedSqlType", sql, null));
				process.info("Sql is correct.");
			}
			//兼容doris
			else if (ProductTypeEnum.DORIS.name().equalsIgnoreCase(dbType) && (sql.contains("create") || sql.contains("CREATE"))) {
				String[] statements = SqlUtil.getStatements(sql, ";");
				for (String statement : statements) {
					sqlExplainResults.add(SqlExplainResult.success("unexpectedSqlType", statement, null));
				}
				process.info("Sql is correct.");
			} else {
				sqlExplainResults.add(SqlExplainResult.fail(current, LogUtil.getError(e)));
				process.error(LogUtil.getError(e));
			}
		}
		return sqlExplainResults;
	}

	@Override
	public void testQuerySQL(String jdbcUrl, String username, String password, String sql) {
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			database.testQuerySQL(connection, sql);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public void executeSql(String jdbcUrl, String username, String password, String sql) {
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			database.executeSql(connection, sql);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public Object queryMaxVal(String jdbcUrl, String username, String password, String schemaName, String tableName, String columnName) {
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			return database.queryMaxVal(connection, schemaName, tableName, columnName);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public Object queryMaxVal(String jdbcUrl, String username, String password, String sql, String columnName) {
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			return database.queryMaxVal(connection, sql, columnName);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public boolean tableExist(String jdbcUrl, String username, String password, String tableName) {
		try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
			database.executeSql(connection, String.format("SELECT 1 FROM %s WHERE 1=0", tableName));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String getDDLCreateTableSQL(ProductTypeEnum type, List<ColumnDescription> fieldNames,
									   List<String> primaryKeys, String schemaName, String tableName, boolean autoIncr) {
		return GenerateSqlUtils.getDDLCreateTableSQL(
				type, fieldNames, primaryKeys, schemaName, tableName, autoIncr);
	}

	@Override
	public String getFlinkTableSql(List<ColumnDescription> columns, String schemaName, String tableName, String tableRemarks, String flinkConfig) {
		StringBuilder sb = new StringBuilder("DROP TABLE IF EXISTS ");
		sb.append(tableName).append(";\n");
		sb.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (\n");
		List<String> pks = new ArrayList<>();
		for (int i = 0; i < columns.size(); i++) {
			String type = FlinkColumnType.getByJavaType(columns.get(i).getFiledTypeClassName()).getFlinkType();
			sb.append("    ");
			if (i > 0) {
				sb.append(",");
			}
			sb.append(columns.get(i).getFieldName()).append(" ").append(type);
			sb.append("\n");
			if (columns.get(i).isPk()) {
				pks.add(columns.get(i).getFieldName());
			}
		}
		StringBuilder pksb = new StringBuilder("PRIMARY KEY ( ");
		for (int i = 0; i < pks.size(); i++) {
			if (i > 0) {
				pksb.append(",");
			}
			pksb.append(pks.get(i));
		}
		pksb.append(" ) NOT ENFORCED\n");
		if (pks.size() > 0) {
			sb.append("    ,");
			sb.append(pksb);
		}
		sb.append(")");
		sb.append(" WITH (\n");
		sb.append(getFlinkTableWith(flinkConfig, schemaName, tableName));
		sb.append("\n);\n");
		return sb.toString();
	}

	@Override
	public String getSqlSelect(List<ColumnDescription> columns, String schemaName, String tableName, String tableRemarks) {
		if (ProductTypeEnum.MONGODB.equals(dbType)) {
			return String.format("db.getCollection('%s').find({},{ %s })",
					tableName, columns.stream().map(s -> String.format("'%s' : 1\n", s.getFieldName()))
							.collect(Collectors.joining(",")));
		}
		StringBuilder sb = new StringBuilder("SELECT\n");
		for (int i = 0; i < columns.size(); i++) {
			sb.append("    ");
			if (i > 0) {
				sb.append(",");
			}
			String columnComment = columns.get(i).getRemarks();
			if (StringUtil.isNotBlank(columnComment)) {
				if (columnComment.contains("\'") | columnComment.contains("\"")) {
					columnComment = columnComment.replaceAll("[\"']", "");
				}
				sb.append(columns.get(i).getFieldName()).append("  --  ").append(columnComment).append(" \n");
			} else {
				sb.append(columns.get(i).getFieldName()).append(" \n");

			}
		}
		if (StringUtil.isNotBlank(tableRemarks)) {
			sb.append(" FROM ").append(schemaName).append(".").append(tableName).append(";").append(" -- ").append(tableRemarks).append("\n");
		} else {
			sb.append(" FROM ").append(schemaName).append(".").append(tableName).append(";\n");
		}
		return sb.toString();
	}


	@Override
	public String getCountMoreThanOneSql(String schemaName, String tableName, List<String> columns) {
		return database.getCountMoreThanOneSql(schemaName, tableName, columns);
	}

	@Override
	public String getCountOneSql(String schemaName, String tableName, List<String> columns) {
		return database.getCountOneSql(schemaName, tableName, columns);
	}

	private String getFlinkTableWith(String flinkConfig, String schemaName, String tableName) {
		String tableWithSql = "";
		if (StringUtil.isNotBlank(flinkConfig)) {
			tableWithSql = SqlUtil.replaceAllParam(flinkConfig, "schemaName", schemaName);
			tableWithSql = SqlUtil.replaceAllParam(tableWithSql, "tableName", tableName);
		}
		return tableWithSql;
	}
}
