// Copyright tang.  All rights reserved.
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.core.service.impl;

import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.DatabaseAwareUtils;
import srt.cloud.framework.dbswitch.core.database.AbstractDatabase;
import srt.cloud.framework.dbswitch.core.database.DatabaseFactory;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.SchemaTableData;
import srt.cloud.framework.dbswitch.core.model.SchemaTableMeta;
import srt.cloud.framework.dbswitch.core.model.TableDescription;
import srt.cloud.framework.dbswitch.core.service.IMetaDataByDatasourceService;
import srt.cloud.framework.dbswitch.core.util.GenerateSqlUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用DataSource对象的元数据获取服务
 *
 * @author jrl
 */
public class MetaDataByDataSourceServiceImpl implements IMetaDataByDatasourceService {

	private DataSource dataSource;

	private AbstractDatabase database;

	private ProductTypeEnum type;

	public MetaDataByDataSourceServiceImpl(DataSource dataSource) {
		this(dataSource, DatabaseAwareUtils.getDatabaseTypeByDataSource(dataSource));
	}

	public MetaDataByDataSourceServiceImpl(DataSource dataSource, ProductTypeEnum type) {
		this.dataSource = dataSource;
		this.database = DatabaseFactory.getDatabaseInstance(type);
		this.type = type;
	}

	@Override
	public DataSource getDataSource() {
		return this.dataSource;
	}

	@Override
	public List<String> querySchemaList() {
		try (Connection connection = dataSource.getConnection()) {
			return database.querySchemaList(connection);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public List<TableDescription> queryTableList(String schemaName) {
		try (Connection connection = dataSource.getConnection()) {
			return database.queryTableList(connection, schemaName);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public String getTableDDL(String schemaName, String tableName) {
		try (Connection connection = dataSource.getConnection()) {
			return database.getTableDDL(connection, schemaName, tableName);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public String getTableRemark(String schemaName, String tableName) {
		try (Connection connection = dataSource.getConnection()) {
			TableDescription td = database.queryTableMeta(connection, schemaName, tableName);
			return null == td ? null : td.getRemarks();
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public String getViewDDL(String schemaName, String tableName) {
		try (Connection connection = dataSource.getConnection()) {
			return database.getViewDDL(connection, schemaName, tableName);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public List<String> queryTableColumnName(String schemaName, String tableName) {
		try (Connection connection = dataSource.getConnection()) {
			return database.queryTableColumnName(connection, schemaName, tableName);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public List<ColumnDescription> queryTableColumnMeta(String schemaName, String tableName) {
		try (Connection connection = dataSource.getConnection()) {
			List<ColumnDescription> columnDescriptions = database.queryTableColumnMeta(connection, schemaName, tableName);
			database.setColumnDefaultValue(connection, schemaName, tableName, columnDescriptions);
			database.setColumnIndexInfo(connection, schemaName, tableName, columnDescriptions);
			return columnDescriptions;
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public List<ColumnDescription> queryTableColumnMetaOnly(String schemaName, String tableName) {
		try (Connection connection = dataSource.getConnection()) {
			return database.queryTableColumnMeta(connection, schemaName, tableName);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public List<ColumnDescription> querySqlColumnMeta(String querySql) {
		try (Connection connection = dataSource.getConnection()) {
			return database.querySelectSqlColumnMeta(connection, querySql);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public List<String> queryTablePrimaryKeys(String schemaName, String tableName) {
		try (Connection connection = dataSource.getConnection()) {
			return database.queryTablePrimaryKeys(connection, schemaName, tableName);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public SchemaTableMeta queryTableMeta(String schemaName, String tableName) {
		SchemaTableMeta tableMeta = new SchemaTableMeta();

		try (Connection connection = dataSource.getConnection()) {
			TableDescription tableDesc = database.queryTableMeta(connection, schemaName, tableName);
			if (null == tableDesc) {
				throw new IllegalArgumentException("Table Or View Not Exist");
			}

			List<ColumnDescription> columns = database.queryTableColumnMeta(
					connection, schemaName, tableName);

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
	public SchemaTableData queryTableData(String schemaName, String tableName, int rowCount) {
		try (Connection connection = dataSource.getConnection()) {
			return database.queryTableData(connection, schemaName, tableName, rowCount);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public void testQuerySQL(String sql) {
		try (Connection connection = dataSource.getConnection()) {
			database.testQuerySQL(connection, sql);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public List<String> getDDLCreateTableSQL(ProductTypeEnum type,
											 List<ColumnDescription> fieldNames, List<String> primaryKeys, String schemaName,
											 String tableName, String tableRemarks, boolean autoIncr, Map<String, String> tblProperties) {
		return GenerateSqlUtils.getDDLCreateTableSQL(
				type, fieldNames, primaryKeys, schemaName, tableName, tableRemarks, autoIncr, tblProperties);
	}

	@Override
	public void addNoExistColumnsByTarget(String targetSchemaName, String targetTableName, List<ColumnDescription> targetColumnDescriptions) {
		try (Connection connection = dataSource.getConnection()) {
			database.addNoExistColumnsByTarget(connection, targetSchemaName, targetTableName, queryTableColumnMetaOnly(targetSchemaName, targetTableName).stream().map(ColumnDescription::getFieldName).collect(Collectors.toList()), targetColumnDescriptions);
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public void createIndexDefinition(List<ColumnDescription> targetColumns, List<String> targetPrimaryKeys, String targetSchemaName, String targetTableName, List<String> sqlCreateTable) {
		database.createIndexDefinition(targetColumns, targetPrimaryKeys, targetSchemaName, targetTableName, sqlCreateTable);
	}

}
