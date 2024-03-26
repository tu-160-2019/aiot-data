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

import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.core.database.AbstractDatabase;
import srt.cloud.framework.dbswitch.core.database.DatabaseFactory;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.DatabaseDescription;
import srt.cloud.framework.dbswitch.core.model.SchemaTableData;
import srt.cloud.framework.dbswitch.core.model.SchemaTableMeta;
import srt.cloud.framework.dbswitch.core.model.TableDescription;
import srt.cloud.framework.dbswitch.core.service.IMetaDataByDescriptionService;
import srt.cloud.framework.dbswitch.core.util.ConnectionUtils;
import srt.cloud.framework.dbswitch.core.util.GenerateSqlUtils;
import srt.cloud.framework.dbswitch.core.util.JdbcUrlUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * 用IP:PORT等参数配置的元数据获取服务
 *
 * @author jrl
 */
public class MetaDataByDescriptionServiceImpl implements IMetaDataByDescriptionService {

  private static int connectTimeOut = 6;
  protected AbstractDatabase database = null;
  protected DatabaseDescription databaseDesc = null;

  public MetaDataByDescriptionServiceImpl(DatabaseDescription databaseDesc) {
    this.databaseDesc = databaseDesc;
    this.database = DatabaseFactory.getDatabaseInstance(databaseDesc.getType());
  }

  @Override
  public DatabaseDescription getDatabaseConnection() {
    return this.databaseDesc;
  }

  @Override
  public List<String> querySchemaList() {
    String jdbcUrl = JdbcUrlUtils.getJdbcUrl(
        this.databaseDesc, MetaDataByDescriptionServiceImpl.connectTimeOut);
    String username = this.databaseDesc.getUsername();
    String password = this.databaseDesc.getPassword();
    try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
      return database.querySchemaList(connection);
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
  }

  @Override
  public List<TableDescription> queryTableList(String schemaName) {
    String jdbcUrl = JdbcUrlUtils.getJdbcUrl(
        this.databaseDesc, MetaDataByDescriptionServiceImpl.connectTimeOut);
    String username = this.databaseDesc.getUsername();
    String password = this.databaseDesc.getPassword();
    try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
      return database.queryTableList(connection, schemaName);
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
  }

  @Override
  public String getTableDDL(String schemaName, String tableName) {
    String jdbcUrl = JdbcUrlUtils.getJdbcUrl(
        this.databaseDesc, MetaDataByDescriptionServiceImpl.connectTimeOut);
    String username = this.databaseDesc.getUsername();
    String password = this.databaseDesc.getPassword();
    try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
      return database.getTableDDL(connection, schemaName, tableName);
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
  }

  @Override
  public String getViewDDL(String schemaName, String tableName) {
    String jdbcUrl = JdbcUrlUtils.getJdbcUrl(
        this.databaseDesc, MetaDataByDescriptionServiceImpl.connectTimeOut);
    String username = this.databaseDesc.getUsername();
    String password = this.databaseDesc.getPassword();
    try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
      return database.getViewDDL(connection, schemaName, tableName);
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
  }

  @Override
  public List<ColumnDescription> queryTableColumnMeta(String schemaName, String tableName) {
    String jdbcUrl = JdbcUrlUtils.getJdbcUrl(
        this.databaseDesc, MetaDataByDescriptionServiceImpl.connectTimeOut);
    String username = this.databaseDesc.getUsername();
    String password = this.databaseDesc.getPassword();
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
  public List<ColumnDescription> querySqlColumnMeta(String querySql) {
    String jdbcUrl = JdbcUrlUtils.getJdbcUrl(
        this.databaseDesc, MetaDataByDescriptionServiceImpl.connectTimeOut);
    String username = this.databaseDesc.getUsername();
    String password = this.databaseDesc.getPassword();
    try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
      return database.querySelectSqlColumnMeta(connection, querySql);
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
  }

  @Override
  public List<String> queryTablePrimaryKeys(String schemaName, String tableName) {
    String jdbcUrl = JdbcUrlUtils.getJdbcUrl(
        this.databaseDesc, MetaDataByDescriptionServiceImpl.connectTimeOut);
    String username = this.databaseDesc.getUsername();
    String password = this.databaseDesc.getPassword();
    try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
      return database.queryTablePrimaryKeys(connection, schemaName, tableName);
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
  }

  @Override
  public SchemaTableMeta queryTableMeta(String schemaName, String tableName) {
    SchemaTableMeta tableMeta = new SchemaTableMeta();
    String jdbcUrl = JdbcUrlUtils.getJdbcUrl(
        this.databaseDesc, MetaDataByDescriptionServiceImpl.connectTimeOut);
    String username = this.databaseDesc.getUsername();
    String password = this.databaseDesc.getPassword();
    try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
      TableDescription tableDesc = this.database.queryTableMeta(connection, schemaName, tableName);
      if (null == tableDesc) {
        throw new IllegalArgumentException("Table Or View Not Exist");
      }
      List<ColumnDescription> columns = this.queryTableColumnMeta(schemaName, tableName);

      List<String> pks;
      String createSql;
      if (tableDesc.isViewTable()) {
        pks = Collections.emptyList();
        createSql = this.database.getViewDDL(connection, schemaName, tableName);
      } else {
        pks = this.database.queryTablePrimaryKeys(connection, schemaName, tableName);
        createSql = this.database.getTableDDL(connection, schemaName, tableName);
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
    String jdbcUrl = JdbcUrlUtils.getJdbcUrl(
        this.databaseDesc, MetaDataByDescriptionServiceImpl.connectTimeOut);
    String username = this.databaseDesc.getUsername();
    String password = this.databaseDesc.getPassword();

    try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
      return this.database.queryTableData(connection, schemaName, tableName, rowCount);
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
  }

  @Override
  public void testQuerySQL(String sql) {
    String jdbcUrl = JdbcUrlUtils.getJdbcUrl(
        this.databaseDesc, MetaDataByDescriptionServiceImpl.connectTimeOut);
    String username = this.databaseDesc.getUsername();
    String password = this.databaseDesc.getPassword();
    try (Connection connection = ConnectionUtils.connect(jdbcUrl, username, password)) {
      database.testQuerySQL(connection, sql);
    } catch (SQLException se) {
      throw new RuntimeException(se);
    }
  }

  @Override
  public String getDDLCreateTableSQL(ProductTypeEnum type, List<ColumnDescription> fieldNames,
									 List<String> primaryKeys, String schemaName, String tableName, boolean autoIncr) {
    return GenerateSqlUtils.getDDLCreateTableSQL(
        type, fieldNames, primaryKeys, schemaName, tableName, autoIncr);
  }

}
