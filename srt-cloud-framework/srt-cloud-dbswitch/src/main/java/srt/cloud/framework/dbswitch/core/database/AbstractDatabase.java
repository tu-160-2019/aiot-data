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

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.github.freakchick.orange.SqlMeta;
import lombok.extern.slf4j.Slf4j;
import net.srt.flink.common.utils.LogUtil;
import net.srt.flink.common.utils.SqlUtil;
import net.srt.flink.process.context.ProcessContextHolder;
import net.srt.flink.process.model.ProcessEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.DbswitchStrUtils;
import srt.cloud.framework.dbswitch.common.util.HivePrepareUtils;
import srt.cloud.framework.dbswitch.common.util.StringUtil;
import srt.cloud.framework.dbswitch.common.util.TypeConvertUtils;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.ColumnMetaData;
import srt.cloud.framework.dbswitch.core.model.CustomSQLStatement;
import srt.cloud.framework.dbswitch.core.model.JdbcSelectResult;
import srt.cloud.framework.dbswitch.core.model.SchemaTableData;
import srt.cloud.framework.dbswitch.core.model.TableDescription;
import srt.cloud.framework.dbswitch.core.util.SqlEngineUtil;

import java.rmi.ServerException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据库元信息抽象基类
 *
 * @author jrl
 */
@Slf4j
public abstract class AbstractDatabase implements IDatabaseInterface {

	public static final int CLOB_LENGTH = 9999999;

	protected String driverClassName;
	protected String catalogName = null;

	public AbstractDatabase(String driverClassName) {
		try {
			this.driverClassName = driverClassName;
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getDriverClassName() {
		return this.driverClassName;
	}

	@Override
	public List<String> querySchemaList(Connection connection) {
		Set<String> ret = new HashSet<>();
		try (ResultSet schemas = connection.getMetaData().getSchemas()) {
			while (schemas.next()) {
				ret.add(schemas.getString("TABLE_SCHEM"));
			}
			return new ArrayList<>(ret);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<TableDescription> queryTableList(Connection connection, String schemaName) {
		List<TableDescription> ret = new ArrayList<>();
		Set<String> uniqueSet = new HashSet<>();
		String[] types = new String[]{"TABLE", "VIEW"};
		try (ResultSet tables = connection.getMetaData()
				.getTables(this.catalogName, schemaName, "%", types)) {
			while (tables.next()) {
				String tableName = tables.getString("TABLE_NAME");
				if (uniqueSet.contains(tableName)) {
					continue;
				} else {
					uniqueSet.add(tableName);
				}

				TableDescription td = new TableDescription();
				td.setSchemaName(schemaName);
				td.setTableName(tableName);
				td.setRemarks(tables.getString("REMARKS"));
				td.setTableType(tables.getString("TABLE_TYPE").toUpperCase());
				ret.add(td);
			}
			return ret;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public TableDescription queryTableMeta(Connection connection, String schemaName,
										   String tableName) {
		return queryTableList(connection, schemaName).stream()
				.filter(one -> tableName.equals(one.getTableName()))
				.findAny().orElse(null);
	}

	@Override
	public List<String> queryTableColumnName(Connection connection, String schemaName,
											 String tableName) {
		Set<String> columns = new HashSet<>();
		try (ResultSet rs = connection.getMetaData()
				.getColumns(this.catalogName, schemaName, tableName, null)) {
			while (rs.next()) {
				columns.add(rs.getString("COLUMN_NAME"));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return new ArrayList<>(columns);
	}

	@Override
	public void setColumnDefaultValue(Connection connection, String schemaName, String tableName, List<ColumnDescription> columnDescriptions) {

		String sql = this.getDefaultValueSql(schemaName, tableName);
		if (sql == null) {
			return;
		}
		try (Statement st = connection.createStatement()) {
			try (ResultSet rs = st.executeQuery(sql)) {
				while (rs.next()) {
					String columnName = rs.getString("column_name");
					String columnDefault = rs.getString("column_default");
					String columnComment = rs.getString("column_comment");
					if (columnName != null) {
						for (ColumnDescription cd : columnDescriptions) {
							if (columnName.equals(cd.getFieldName())) {
								cd.setDefaultValue(columnDefault);
								cd.setRemarks(columnComment);
								break;
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			log.error(schemaName + "." + tableName + " setColumnDefaultValue error:" + e.getMessage());
			//throw new RuntimeException(e);
		}
	}

	protected abstract String getDefaultValueSql(String schemaName, String tableName);

	@Override
	public void setColumnIndexInfo(Connection connection, String schemaName, String tableName, List<ColumnDescription> columnDescriptions) {
		// 补充一下索引信息
		try (ResultSet indexInfo = connection.getMetaData().getIndexInfo(this.catalogName, schemaName, tableName, false, true)) {
			setIndex(columnDescriptions, indexInfo);
		} catch (SQLException e) {
			log.error(schemaName + "." + tableName + " setColumnIndexInfo error:" + e.getMessage());
			throw new RuntimeException(schemaName + "." + tableName + " setColumnIndexInfo error!!", e);
		}
	}

	/**
	 * 设置索引信息
	 *
	 * @param columnDescriptions
	 * @param indexInfo
	 * @throws SQLException
	 */
	public void setIndex(List<ColumnDescription> columnDescriptions, ResultSet indexInfo) throws SQLException {
		while (indexInfo.next()) {
			//索引值是否可以不唯一
			boolean nonUnique = indexInfo.getBoolean("NON_UNIQUE");
			//索引类别
			String indexQualifier = indexInfo.getString("INDEX_QUALIFIER");
			String indexName = indexInfo.getString("INDEX_NAME");
			/**
			 * 索引类型：
			 *  tableIndexStatistic - 此标识与表的索引描述一起返回的表统计信息
			 *  tableIndexClustered - 此为集群索引
			 *  tableIndexHashed - 此为散列索引
			 *  tableIndexOther - 此为某种其他样式的索引
			 */
			short type = indexInfo.getShort("TYPE");
			String columnName = indexInfo.getString("COLUMN_NAME");
			String ascOrDesc = indexInfo.getString("ASC_OR_DESC");
			if (columnName != null) {
				for (ColumnDescription cd : columnDescriptions) {
					if (columnName.equals(cd.getFieldName())) {
						cd.setNonIndexUnique(nonUnique);
						cd.setIndexQualifier(indexQualifier);
						cd.setIndexName(indexName);
						cd.getIndexes().add(indexName);
						cd.setIndexType(type);
						cd.setAscOrDesc(ascOrDesc);
						break;
					}
				}
			}
		}
	}

	@Override
	public List<ColumnDescription> queryTableColumnMeta(Connection connection, String schemaName,
														String tableName) {
		String sql = this.getTableFieldsQuerySQL(schemaName, tableName);
		List<ColumnDescription> ret = this.querySelectSqlColumnMeta(connection, sql);
		// 补充一下注释信息，索引信息
		try (ResultSet columns = connection.getMetaData()
				.getColumns(this.catalogName, schemaName, tableName, null)) {
			while (columns.next()) {
				String columnName = columns.getString("COLUMN_NAME");
				String remarks = columns.getString("REMARKS");
				for (ColumnDescription cd : ret) {
					if (columnName.equals(cd.getFieldName())) {
						cd.setRemarks(remarks);
						break;
					}
				}
			}
		} catch (SQLException e) {
			log.error(schemaName + "." + tableName + " queryTableColumnMeta error:" + e.getMessage());
			throw new RuntimeException(schemaName + "." + tableName + " queryTableColumnMeta error!!", e);
		}
		return ret;
	}

	@Override
	public List<ColumnDescription> queryTableColumnMetaOnly(Connection connection, String schemaName,
															String tableName) {
		String sql = this.getTableFieldsQuerySQL(schemaName, tableName);
		return this.querySelectSqlColumnMeta(connection, sql);
	}

	@Override
	public List<String> queryTablePrimaryKeys(Connection connection, String schemaName,
											  String tableName) {
		Set<String> ret = new HashSet<>();
		try (ResultSet primaryKeys = connection.getMetaData()
				.getPrimaryKeys(this.catalogName, schemaName, tableName)) {
			while (primaryKeys.next()) {
				String name = primaryKeys.getString("COLUMN_NAME");
				ret.add(name);
			}
			return new ArrayList<>(ret);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public SchemaTableData queryTableData(Connection connection, String schemaName, String tableName,
										  int rowCount) {
		String fullTableName = getQuotedSchemaTableCombination(schemaName, tableName);
		String querySQL = String.format("SELECT * FROM %s ", fullTableName);
		SchemaTableData data = new SchemaTableData();
		data.setSchemaName(schemaName);
		data.setTableName(tableName);
		data.setColumns(new ArrayList<>());
		data.setRows(new ArrayList<>());
		try (Statement st = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
			// 限制下最大数量
			st.setMaxRows(rowCount);
			if (getDatabaseType().equals(ProductTypeEnum.MYSQL) || getDatabaseType().equals(ProductTypeEnum.DORIS)) {
				st.setFetchSize(Integer.MIN_VALUE);
			} else {
				st.setFetchSize(100);
			}
			if (getDatabaseType() == ProductTypeEnum.HIVE) {
				HivePrepareUtils.prepare(connection, schemaName, tableName);
			}
			return getSchemaTableData(querySQL, rowCount, data, st);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public SchemaTableData queryTableDataBySql(Connection connection, String sql, int rowCount) {
		SchemaTableData data = new SchemaTableData();
		data.setColumns(new ArrayList<>());
		data.setRows(new ArrayList<>());
		try (Statement st = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
			// 限制下最大数量
			st.setMaxRows(rowCount);
			if (getDatabaseType().equals(ProductTypeEnum.MYSQL) || getDatabaseType().equals(ProductTypeEnum.DORIS)) {
				st.setFetchSize(Integer.MIN_VALUE);
			} else {
				st.setFetchSize(100);
			}
			return getSchemaTableData(sql, rowCount, data, st);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private SchemaTableData getSchemaTableData(String sql, int rowCount, SchemaTableData data, Statement st) throws SQLException {
		try (ResultSet rs = st.executeQuery(sql)) {
			ResultSetMetaData m = rs.getMetaData();
			int count = m.getColumnCount();
			for (int i = 1; i <= count; i++) {
				data.getColumns().add(m.getColumnLabel(i));
			}

			int counter = 0;
			while (rs.next() && counter++ < rowCount) {
				List<Object> row = new ArrayList<>(count);
				for (int i = 1; i <= count; i++) {
					Object value = rs.getObject(i);
					if (value != null && value instanceof byte[]) {
						row.add(DbswitchStrUtils.toHexString((byte[]) value));
					} else if (value != null && value instanceof java.sql.Clob) {
						row.add(TypeConvertUtils.castToString(value));
					} else if (value != null && value instanceof java.sql.Blob) {
						byte[] bytes = TypeConvertUtils.castToByteArray(value);
						row.add(DbswitchStrUtils.toHexString(bytes));
					} else {
						row.add(null == value ? null : value.toString());
					}
				}
				data.getRows().add(row);
			}

			return data;
		}
	}

	@Override
	public void testQuerySQL(Connection connection, String sql) {
		String wrapperSql = this.getTestQuerySQL(sql);
		try (Statement statement = connection.createStatement();) {
			statement.execute(wrapperSql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getQuotedSchemaTableCombination(String schemaName, String tableName) {
		return String.format(" \"%s\".\"%s\" ", schemaName, tableName);
	}

	@Override
	public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc,
									 boolean addCr, boolean withRemarks) {
		throw new RuntimeException("AbstractDatabase Unimplemented!");
	}

	@Override
	public String getPrimaryKeyAsString(List<String> pks) {
		if (!pks.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("\"");
			sb.append(StringUtils.join(pks, "\" , \""));
			sb.append("\"");
			return sb.toString();
		}

		return "";
	}

	@Override
	public List<String> getTableColumnCommentDefinition(TableDescription td,
														List<ColumnDescription> cds) {
		throw new RuntimeException("AbstractDatabase Unimplemented!");
	}

	/**************************************
	 * internal function
	 **************************************/

	protected abstract String getTableFieldsQuerySQL(String schemaName, String tableName);

	protected abstract String getTestQuerySQL(String sql);

	protected List<ColumnDescription> getSelectSqlColumnMeta(Connection connection, String querySQL) {
		List<ColumnDescription> ret = new ArrayList<>();
		try (Statement st = connection.createStatement()) {
			if (getDatabaseType() == ProductTypeEnum.HIVE) {
				HivePrepareUtils.setResultSetColumnNameNotUnique(connection);
			}

			try (ResultSet rs = st.executeQuery(querySQL)) {
				ResultSetMetaData m = rs.getMetaData();
				int columns = m.getColumnCount();
				for (int i = 1; i <= columns; i++) {
					String name = m.getColumnLabel(i);
					if (null == name) {
						name = m.getColumnName(i);
					}
					ColumnDescription cd = new ColumnDescription();
					cd.setFieldName(name);
					cd.setLabelName(name);
					cd.setFieldType(m.getColumnType(i));
					if (0 != cd.getFieldType()) {
						cd.setFieldTypeName(m.getColumnTypeName(i));
						cd.setFiledTypeClassName(m.getColumnClassName(i));
						cd.setDisplaySize(m.getColumnDisplaySize(i));
						cd.setPrecisionSize(m.getPrecision(i));
						cd.setScaleSize(m.getScale(i));
						cd.setAutoIncrement(m.isAutoIncrement(i));
						cd.setNullable(m.isNullable(i) != ResultSetMetaData.columnNoNulls);
					} else {
						// 处理视图中NULL as fieldName的情况
						cd.setFieldTypeName("CHAR");
						cd.setFiledTypeClassName(String.class.getName());
						cd.setDisplaySize(1);
						cd.setPrecisionSize(1);
						cd.setScaleSize(0);
						cd.setAutoIncrement(false);
						cd.setNullable(true);
					}

					boolean signed = false;
					try {
						signed = m.isSigned(i);
					} catch (Exception ignored) {
						// This JDBC Driver doesn't support the isSigned method
						// nothing more we can do here by catch the exception.
					}
					cd.setSigned(signed);
					cd.setDbType(getDatabaseType());

					ret.add(cd);
				}

				return ret;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取字段类型
	 *
	 * @param v
	 * @return
	 */
	@Override
	public boolean canCreateIndex(ColumnMetaData v) {
		return false;
	}

	/**
	 * 創建索引
	 *
	 * @param fieldNames
	 * @param primaryKeys
	 * @param schemaName
	 * @param tableName
	 * @param results
	 */
	public void createIndexDefinition(List<ColumnDescription> fieldNames, List<String> primaryKeys, String schemaName, String tableName, List<String> results) {
		//去除主鍵，获取需要创建索引的字段
		List<ColumnDescription> columns = fieldNames.stream()
				.filter(columnDescription -> !primaryKeys.contains(columnDescription.getFieldName()) && !CollectionUtils.isEmpty(columnDescription.getIndexes()))
				.collect(Collectors.toList());
		Map<String, List<ColumnDescription>> columnMap = new HashMap<>(6);
		for (ColumnDescription columnDescription : columns) {
			//获取字段元数据信息
			ColumnMetaData v = columnDescription.getMetaData();
			//zrx 如果不是能创建索引的类型
			if (!canCreateIndex(v)) {
				continue;
			}
			List<String> indexes = columnDescription.getIndexes();
			for (String index : indexes) {
				List<ColumnDescription> columnDescriptionList = new ArrayList<>(2);
				if (columnMap.containsKey(index)) {
					columnDescriptionList = columnMap.get(index);
					columnDescriptionList.add(columnDescription);
				} else {
					columnDescriptionList.add(columnDescription);
					columnMap.put(index, columnDescriptionList);
				}
			}
		}
		//遍历map，创建索引语句
		setIndexSql(schemaName, tableName, results, columnMap);
	}

	@Override
	public void setIndexSql(String schemaName, String tableName, List<String> results, Map<String, List<ColumnDescription>> columnMap) {
		for (Map.Entry<String, List<ColumnDescription>> entry : columnMap.entrySet()) {
			String indexName = entry.getKey();
			List<ColumnDescription> descriptions = entry.getValue();
			ColumnDescription columnDescription = descriptions.get(0);
			String indexSql;
			if (descriptions.size() > 1) {
				indexSql = "CREATE " + (columnDescription.isNonIndexUnique() ? "" : "UNIQUE")
						+ " INDEX " + indexName + " ON " + schemaName + "." + tableName
						+ " (" + descriptions.stream().map(ColumnDescription::getFieldName).collect(Collectors.joining(",")) + ") ";
			} else {
				indexSql = "CREATE " + (columnDescription.isNonIndexUnique() ? "" : "UNIQUE")
						+ " INDEX " + indexName + " ON " + schemaName + "." + tableName + " (" + columnDescription.getFieldName() + ") ";
			}
			results.add(indexSql);
		}
	}

	@Override
	public void addNoExistColumnsByTarget(Connection connection, String targetSchemaName, String targetTableName, List<String> allColumns, List<ColumnDescription> targetColumnDescriptions) {
		try (Statement statement = connection.createStatement()) {
			for (ColumnDescription targetColumn : targetColumnDescriptions) {
				if (!allColumns.contains(targetColumn.getFieldName().toLowerCase()) && !allColumns.contains(targetColumn.getFieldName().toUpperCase()) && !StringUtils.isEmpty(targetColumn.getFieldName())) {
					//添加字段
					statement.execute(getAddColumnSql(targetSchemaName, targetTableName, targetColumn));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getAddColumnSql(String targetSchemaName, String targetTableName, ColumnDescription targetColumn) {
		//如果表信息里不不含该字段，新建
		String alterSql = "ALTER TABLE " + targetSchemaName + "." + targetTableName
				+ " ADD COLUMN ";
		//获取字段元数据信息
		ColumnMetaData v = targetColumn.getMetaData();
		alterSql += getFieldDefinition(v, null, false, false, true);
		return alterSql;
	}

	public void executeSql(Connection connection, String sql) {
		try (Statement statement = connection.createStatement()) {
			statement.execute(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public JdbcSelectResult queryDataBySql(Connection connection, String dbType, String sql, Integer openTrans, int rowCount) {
		ProcessEntity process = ProcessContextHolder.getProcess();
		process.info("Start parse sql...");
		List<SQLStatement> stmtList = new ArrayList<>();
		try {
			stmtList = SQLUtils.parseStatements(sql, dbType.toLowerCase());
		} catch (Exception e) {
			//mongodb直接走sql
			if (ProductTypeEnum.MONGODB.name().equalsIgnoreCase(dbType)) {
				SQLStatement sqlStatement = new CustomSQLStatement(sql);
				stmtList.add(sqlStatement);
			}
			//兼容doris
			else if (ProductTypeEnum.DORIS.name().equalsIgnoreCase(dbType) && (sql.contains("create") || sql.contains("CREATE"))) {
				String[] statements = srt.cloud.framework.dbswitch.core.util.SqlUtil.getStatements(sql, ";");
				for (String statement : statements) {
					SQLStatement sqlStatement = new CustomSQLStatement(statement);
					stmtList.add(sqlStatement);
				}
			} else {
				JdbcSelectResult jobResult = new JdbcSelectResult();
				List<JdbcSelectResult> results = new ArrayList<>();
				jobResult.setSuccess(true);
				jobResult.setResults(results);
				JdbcSelectResult result = new JdbcSelectResult();
				result.setSuccess(false);
				result.setErrorMsg(LogUtil.getError(e));
				process.error(result.getErrorMsg());
				jobResult.setSuccess(false);
				jobResult.setErrorMsg(result.getErrorMsg());
				result.setSql(sql);
				result.setTime(0L);
				results.add(result);
				return jobResult;
			}
		}
		process.info(CharSequenceUtil.format("A total of {} statement have been Parsed.", stmtList.size()));
		process.info("Start execute sql...");
		JdbcSelectResult jobResult = new JdbcSelectResult();
		List<JdbcSelectResult> results = new ArrayList<>();
		jobResult.setSuccess(true);
		jobResult.setResults(results);

		for (SQLStatement item : stmtList) {
			process.info("Execute sql:\n" + item.toString());
			// 将查询数据存储到数据中
			List<Map<String, Object>> dataList = new ArrayList<>();
			// 存储列名的数组
			List<String> columnList = new LinkedList<>();
			// 新增、修改、删除受影响行数
			Integer updateCount = null;
			JdbcSelectResult result = new JdbcSelectResult();
			result.setSuccess(true);
			Statement stmt = null;
			ResultSet rs = null;
			long sqlStart = System.currentTimeMillis();
			try {
				if (openTrans != null) {
					if (openTrans == 1) {
						// 为了设置fetchSize，必须设置为false
						connection.setAutoCommit(false);
					} else {
						connection.setAutoCommit(true);
					}
				}
				//stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				stmt = connection.createStatement();
				// 限制下最大数量
				stmt.setMaxRows(rowCount);
				if (ProductTypeEnum.MYSQL.name().equalsIgnoreCase(dbType) || ProductTypeEnum.DORIS.name().equalsIgnoreCase(dbType)) {
					stmt.setFetchSize(Integer.MIN_VALUE);
				}
				// 是否查询操作
				boolean execute = stmt.execute(item.toString());
				if (execute) {
					result.setIfQuery(true);
					int count = 0;
					rs = stmt.getResultSet();
					// 获取结果集的元数据信息
					ResultSetMetaData rsmd = rs.getMetaData();
					// 获取列字段的个数
					int colunmCount = rsmd.getColumnCount();
					for (int i = 1; i <= colunmCount; i++) {
						// 获取所有的字段名称
						columnList.add(rsmd.getColumnLabel(i));
					}
					while (rs.next()) {
						Map<String, Object> map = new HashMap<>();
						for (int i = 1; i <= colunmCount; i++) {
							// 获取列名
							String columnName = rsmd.getColumnLabel(i);
							Object val = rs.getObject(i);
							if (val instanceof byte[]) {
								val = DbswitchStrUtils.toHexString((byte[]) val);
							} else if (val instanceof java.sql.Clob) {
								val = TypeConvertUtils.castToString(val);
							} else if (val instanceof java.sql.Blob) {
								byte[] bytes = TypeConvertUtils.castToByteArray(val);
								val = DbswitchStrUtils.toHexString(bytes);
							} else {
								val = null == val ? null : val.toString();
							}
							map.put(columnName, val);
						}
						dataList.add(map);
						count++;
						if (count >= rowCount) {
							break;
						}
					}
				} else {
					result.setIfQuery(false);
					// 执行新增、修改、删除受影响行数
					updateCount = stmt.getUpdateCount();
				}
				if (openTrans != null && openTrans == 1) {
					connection.commit();
				}
			} catch (Exception e) {
				result.setSuccess(false);
				result.setErrorMsg(LogUtil.getError(e));
				process.error(result.getErrorMsg());
				jobResult.setSuccess(false);
				jobResult.setErrorMsg(result.getErrorMsg());
				if (openTrans != null && openTrans == 1) {
					try {
						connection.rollback();
					} catch (SQLException ignored) {
					}
				}
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (stmt != null) {
						stmt.close();
					}
				} catch (SQLException ignored) {
				}
			}
			long sqlEnd = System.currentTimeMillis();
			process.info("use time:\n" + item.toString() + ":\n" + (sqlEnd - sqlStart) + "ms.");
			result.setSql(item.toString());
			result.setCount(updateCount);
			result.setColumns(columnList);
			result.setRowData(dataList);
			result.setTime(sqlEnd - sqlStart);
			results.add(result);
			//如果执行失败了，终止执行
			if (!result.getSuccess()) {
				break;
			}
		}
		return jobResult;
	}

	public JdbcSelectResult queryDataByApiSql(Connection connection, String sql, Integer openTrans, String sqlSeparator, Map<String, Object> sqlParam, int rowCount) {
		String[] statements = SqlUtil.getStatements(sql, sqlSeparator);
		JdbcSelectResult jobResult = new JdbcSelectResult();
		List<JdbcSelectResult> results = new ArrayList<>();
		jobResult.setSuccess(true);
		jobResult.setResults(results);
		sqlParam = sqlParam == null ? new HashMap<>() : sqlParam;

		for (int k = 0; k < statements.length; k++) {
			String item = statements[k];
			if (k == statements.length - 1 && StringUtil.isBlank(item)) {
				break;
			}
			// 将查询数据存储到数据中
			List<Map<String, Object>> dataList = new ArrayList<>();
			// 存储列名的数组
			List<String> columnList = new LinkedList<>();
			// 新增、修改、删除受影响行数
			Integer updateCount = null;
			JdbcSelectResult result = new JdbcSelectResult();
			result.setSuccess(true);
			Statement stmt = null;
			ResultSet rs = null;
			long sqlStart = System.currentTimeMillis();
			try {
				if (openTrans == 1) {
					// 为了设置fetchSize，必须设置为false
					connection.setAutoCommit(false);
				} else {
					connection.setAutoCommit(true);
				}
				SqlMeta sqlMeta = SqlEngineUtil.getEngine().parse(item, sqlParam);
				//部分数据库不支持使用prepareStatement，改为Statement，但sql本身中如果有问号会出问题，一般不会有这种情况，先这样
				//stmt = connection.prepareStatement(sqlMeta.getSql(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				stmt = connection.createStatement();
				item = sqlMeta.getSql();
				//参数注入
				List<Object> jdbcParamValues = sqlMeta.getJdbcParamValues();
				if (!CollectionUtils.isEmpty(jdbcParamValues)) {
					for (int i = 1; i <= jdbcParamValues.size(); i++) {
						Object param = jdbcParamValues.get(i - 1);
						//判断sql注入
						if (param != null && SqlUtil.containsSqlInjection(param)) {
							throw new ServerException("SQL 参数中含有非法字符，请检查！");
						}
						if (param == null) {
							item = item.replaceFirst("\\?", "NULL");
						} else if (param instanceof String) {
							item = item.replaceFirst("\\?", "'" + param.toString() + "'");
						} else {
							item = item.replaceFirst("\\?", String.valueOf(param));
						}
					}
				}
				// 限制下最大数量
				stmt.setMaxRows(rowCount);
				if (getDatabaseType().equals(ProductTypeEnum.MYSQL) || getDatabaseType().equals(ProductTypeEnum.DORIS)) {
					stmt.setFetchSize(Integer.MIN_VALUE);
				}
				// 是否查询操作
				boolean execute = stmt.execute(item);
				if (execute) {
					result.setIfQuery(true);
					int count = 0;
					rs = stmt.getResultSet();
					// 获取结果集的元数据信息
					ResultSetMetaData rsmd = rs.getMetaData();
					// 获取列字段的个数
					int colunmCount = rsmd.getColumnCount();
					for (int i = 1; i <= colunmCount; i++) {
						// 获取所有的字段名称
						columnList.add(rsmd.getColumnLabel(i));
					}
					while (rs.next()) {
						Map<String, Object> map = new HashMap<>();
						for (int i = 1; i <= colunmCount; i++) {
							// 获取列名
							String columnName = rsmd.getColumnLabel(i);
							Object val = rs.getObject(i);
							if (val instanceof byte[]) {
								val = DbswitchStrUtils.toHexString((byte[]) val);
							} else if (val instanceof java.sql.Clob) {
								val = TypeConvertUtils.castToString(val);
							} else if (val instanceof java.sql.Blob) {
								byte[] bytes = TypeConvertUtils.castToByteArray(val);
								val = DbswitchStrUtils.toHexString(bytes);
							} else {
								val = null == val ? null : val.toString();
							}
							map.put(columnName, val);
						}
						dataList.add(map);
						count++;
						if (count >= rowCount) {
							break;
						}
					}
				} else {
					result.setIfQuery(false);
					// 执行新增、修改、删除受影响行数
					updateCount = stmt.getUpdateCount();
				}
				if (openTrans == 1) {
					connection.commit();
				}
			} catch (Exception e) {
				result.setSuccess(false);
				result.setErrorMsg(LogUtil.getError(e));
				jobResult.setSuccess(false);
				jobResult.setErrorMsg(result.getErrorMsg());
				if (openTrans == 1) {
					try {
						connection.rollback();
					} catch (SQLException ignored) {
					}
				}
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (stmt != null) {
						stmt.close();
					}
				} catch (SQLException ignored) {
				}
			}
			long sqlEnd = System.currentTimeMillis();
			result.setSql(item);
			result.setCount(updateCount);
			result.setColumns(columnList);
			result.setRowData(dataList);
			result.setTime(sqlEnd - sqlStart);
			results.add(result);
			//如果执行失败了，终止执行
			if (!result.getSuccess()) {
				break;
			}
		}
		return jobResult;
	}

	public String getCountMoreThanOneSql(String schemaName, String tableName, List<String> columns) {
		String columnStr = "\"" + String.join("\",\"", columns) + "\"";
		return String.format("SELECT %s FROM \"%s\".\"%s\" GROUP BY %s HAVING count(*)>1", columnStr, schemaName, tableName, columnStr);
	}

	public String getCountOneSql(String schemaName, String tableName, List<String> columns) {
		String columnStr = "\"" + String.join("\",\"", columns) + "\"";
		return String.format("SELECT %s FROM \"%s\".\"%s\" GROUP BY %s HAVING count(*)=1", columnStr, schemaName, tableName, columnStr);
	}

	public Object queryMaxVal(Connection connection, String schemaName, String tableName, String columnName) {
		try (Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery(String.format("SELECT MAX(%s) FROM %s.%s", columnName, schemaName, tableName))) {
			if (resultSet.next()) {
				return resultSet.getObject(1);
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Object queryMaxVal(Connection connection, String sql, String columnName) {
		try (Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery(String.format("SELECT MAX(%s) FROM (%s) t", columnName, sql))) {
			if (resultSet.next()) {
				return resultSet.getObject(1);
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
