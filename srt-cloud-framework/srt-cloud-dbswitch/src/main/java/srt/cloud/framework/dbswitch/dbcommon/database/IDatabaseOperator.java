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

import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.dbcommon.domain.PrepareStatementResultSet;
import srt.cloud.framework.dbswitch.dbcommon.domain.StatementResultSet;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作器接口定义
 *
 * @author jrl
 */
public interface IDatabaseOperator {

	/**
	 * 获取数据源
	 *
	 * @return 数据源
	 */
	DataSource getDataSource();

	/**
	 * 获取读取(fetch)数据的批次大小
	 *
	 * @return 批次大小
	 */
	int getFetchSize();

	StatementResultSet queryTableData(String sql, List<String> fields,
									  List<String> orders);

	String getEscape();

	StatementResultSet queryTableData(String sql);

	StatementResultSet queryTableData(String sql, List<String> fields);

	/**
	 * 设置读取(fetch)数据的批次大小
	 *
	 * @param size 批次大小
	 */
	void setFetchSize(int size);

	/**
	 * 生成查询指定字段的select查询SQL语句
	 *
	 * @param schemaName 模式名称
	 * @param tableName  表名称
	 * @param fields     字段列表
	 * @return 查询指定字段的select查询SQL语句
	 */
	String getSelectTableSql(String schemaName, String tableName, List<String> fields);

	/**
	 * 获取指定schema下表的按主键有序的结果集
	 *
	 * @param schemaName 模式名称
	 * @param tableName  表名称
	 * @param fields     字段列表
	 * @param orders     排序字段列表
	 * @return 结果集包装对象
	 */
	StatementResultSet queryTableData(String schemaName, String tableName, List<String> fields,
									  List<String> orders);

	/**
	 * 获取指定schema下表的结果集
	 *
	 * @param schemaName 模式名称
	 * @param tableName  表名称
	 * @param fields     字段列表
	 * @return 结果集包装对象
	 */
	StatementResultSet queryTableData(String schemaName, String tableName, List<String> fields);

	/**
	 * 清除指定表的所有数据
	 *
	 * @param schemaName 模式名称
	 * @param tableName  表名称
	 */
	void truncateTableData(String schemaName, String tableName);

	/**
	 * 删除指定物理表
	 *
	 * @param schemaName 模式名称
	 * @param tableName  表名称
	 */
	void dropTable(String schemaName, String tableName);

	PrepareStatementResultSet queryIncreaseTableData(String sourceSchemaName, String sourceTableName, List<String> queryFieldColumn, ColumnDescription increaseColumn, String startVal, String endVal);

	PrepareStatementResultSet queryIncreaseTableData(String sql, List<String> queryFieldColumn, ColumnDescription increaseColumn, String startVal, String endVal);

	Boolean getPkExist(String targetSchemaName, String targetTableName, Map<String, Object> pkVal) throws SQLException;
}
