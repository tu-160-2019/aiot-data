// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbsynch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 数据同步抽象基类
 *
 * @author jrl
 */
@Slf4j
public abstract class AbstractDatabaseSynchronize implements IDatabaseSynchronize {

	protected DataSource dataSource;
	protected JdbcTemplate jdbcTemplate;
	/*private PlatformTransactionManager transactionManager;*/
	private Map<String, Integer> columnType;
	protected List<String> fieldOrders;
	protected List<String> pksOrders;
	protected String insertStatementSql;
	protected String updateStatementSql;
	protected String deleteStatementSql;
	protected int[] insertArgsType;
	protected int[] updateArgsType;
	protected int[] deleteArgsType;

	public AbstractDatabaseSynchronize(DataSource ds) {
		this.dataSource = ds;
		this.jdbcTemplate = new JdbcTemplate(ds);
		/*this.transactionManager = new DataSourceTransactionManager(ds);*/
		this.columnType = new HashMap<>();
	}

	@Override
	public DataSource getDataSource() {
		return this.jdbcTemplate.getDataSource();
	}

	/*protected TransactionDefinition getTransactionDefinition() {
		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		return definition;
	}
*/

	/**
	 * 获取查询列元信息的SQL语句
	 *
	 * @param schemaName 模式名称
	 * @param tableName  表名称
	 * @return SQL语句
	 */
	public abstract String getColumnMetaDataSql(String schemaName, String tableName);

	/**
	 * 生成Insert操作的SQL语句
	 *
	 * @param schemaName 模式名称
	 * @param tableName  表名称
	 * @param fieldNames 字段列表
	 * @return Insert操作的SQL语句
	 */
	public abstract String getInsertPrepareStatementSql(String schemaName, String tableName,
														List<String> fieldNames);

	/**
	 * 生成Update操作的SQL语句
	 *
	 * @param schemaName 模式名称
	 * @param tableName  表名称
	 * @param fieldNames 字段列表
	 * @param pks        主键列表
	 * @return Update操作的SQL语句
	 */
	public abstract String getUpdatePrepareStatementSql(String schemaName, String tableName,
														List<String> fieldNames, List<String> pks);

	/**
	 * 生成Delete操作的SQL语句
	 *
	 * @param schemaName 模式名称
	 * @param tableName  表名称
	 * @param pks        主键列表
	 * @return Delete操作的SQL语句
	 */
	public abstract String getDeletePrepareStatementSql(String schemaName, String tableName,
														List<String> pks);

	@Override
	public void prepare(String schemaName, String tableName, List<String> fieldNames,
						List<String> pks) {
		if (fieldNames.isEmpty() || pks.isEmpty() || fieldNames.size() < pks.size()) {
			throw new IllegalArgumentException("字段列表和主键列表不能为空，或者字段总个数应不小于主键总个数");
		}

		if (!fieldNames.containsAll(pks)) {
			throw new IllegalArgumentException("字段列表必须包含主键列表");
		}

		String sql = this.getColumnMetaDataSql(schemaName, tableName);
		columnType.clear();

		this.jdbcTemplate.execute((Connection conn) -> {
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				ResultSetMetaData rsMetaData = rs.getMetaData();
				for (int i = 0, len = rsMetaData.getColumnCount(); i < len; i++) {
					columnType.put(rsMetaData.getColumnName(i + 1), rsMetaData.getColumnType(i + 1));
				}

				return true;
			} catch (Exception e) {
				throw new RuntimeException(
						String.format("获取表:%s.%s 的字段的元信息时失败. 请联系 DBA 核查该库、表信息.", schemaName, tableName), e);
			} finally {
				JdbcUtils.closeResultSet(rs);
				JdbcUtils.closeStatement(stmt);
			}
		});

		this.fieldOrders = new ArrayList<>(fieldNames);
		this.pksOrders = new ArrayList<>(pks);

		this.insertStatementSql = this.getInsertPrepareStatementSql(schemaName, tableName, fieldNames);
		this.updateStatementSql = this
				.getUpdatePrepareStatementSql(schemaName, tableName, fieldNames, pks);
		this.deleteStatementSql = this.getDeletePrepareStatementSql(schemaName, tableName, pks);

		insertArgsType = new int[fieldNames.size()];
		for (int k = 0; k < fieldNames.size(); ++k) {
			String field = fieldNames.get(k);
			insertArgsType[k] = this.columnType.get(field);
		}

		updateArgsType = new int[fieldNames.size()];
		int idx = 0;
		for (String field : fieldNames) {
			if (!pks.contains(field)) {
				updateArgsType[idx++] = this.columnType.get(field);
			}
		}
		for (String pk : pks) {
			updateArgsType[idx++] = this.columnType.get(pk);
		}

		deleteArgsType = new int[pks.size()];
		for (int j = 0; j < pks.size(); ++j) {
			String pk = pks.get(j);
			deleteArgsType[j] = this.columnType.get(pk);
		}
	}


	@Override
	public void prepareIncrease(String schemaName, String tableName, List<String> fieldNames,
								List<String> pks) {
		if (fieldNames.isEmpty() || fieldNames.size() < pks.size()) {
			throw new IllegalArgumentException("字段列表不能为空，或者字段总个数应不小于主键总个数");
		}

		if (!CollectionUtils.isEmpty(pks) && !fieldNames.containsAll(pks)) {
			throw new IllegalArgumentException("字段列表必须包含主键列表");
		}

		String sql = this.getColumnMetaDataSql(schemaName, tableName);
		columnType.clear();

		this.jdbcTemplate.execute((Connection conn) -> {
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				ResultSetMetaData rsMetaData = rs.getMetaData();
				for (int i = 0, len = rsMetaData.getColumnCount(); i < len; i++) {
					columnType.put(rsMetaData.getColumnName(i + 1), rsMetaData.getColumnType(i + 1));
				}

				return true;
			} catch (Exception e) {
				throw new RuntimeException(
						String.format("获取表:%s.%s 的字段的元信息时失败. 请联系 DBA 核查该库、表信息.", schemaName, tableName), e);
			} finally {
				JdbcUtils.closeResultSet(rs);
				JdbcUtils.closeStatement(stmt);
			}
		});

		this.fieldOrders = new ArrayList<>(fieldNames);
		this.pksOrders = new ArrayList<>(pks);

		this.insertStatementSql = this.getInsertPrepareStatementSql(schemaName, tableName, fieldNames);
		if (!CollectionUtils.isEmpty(pks)) {
			this.updateStatementSql = this.getUpdatePrepareStatementSql(schemaName, tableName, fieldNames, pks);
		}
		insertArgsType = new int[fieldNames.size()];
		for (int k = 0; k < fieldNames.size(); ++k) {
			String field = fieldNames.get(k);
			insertArgsType[k] = this.columnType.get(field);
		}

		updateArgsType = new int[fieldNames.size()];
		int idx = 0;
		for (String field : fieldNames) {
			if (!pks.contains(field)) {
				updateArgsType[idx++] = this.columnType.get(field);
			}
		}
		for (String pk : pks) {
			updateArgsType[idx++] = this.columnType.get(pk);
		}
	}

	@Override
	public long executeInsert(List<Object[]> records) {
		batchInsert(records, this.insertStatementSql, this.insertArgsType);
		return records.size();
		/*TransactionStatus status = transactionManager.getTransaction(getTransactionDefinition());
		if (log.isDebugEnabled()) {
			log.debug("Execute Insert SQL : {}", this.insertStatementSql);
		}

		try {
			int[] affects = jdbcTemplate
					.batchUpdate(this.insertStatementSql, records, this.insertArgsType);
			transactionManager.commit(status);
			return affects.length;
		} catch (Exception e) {
			transactionManager.rollback(status);
			throw e;
		}*/
	}

	@Override
	public long executeUpdate(List<Object[]> records) {
		List<Object[]> datas = new LinkedList<>();
		for (Object[] r : records) {

			Object[] nr = new Object[this.fieldOrders.size()];
			int idx = 0;

			for (int i = 0; i < this.fieldOrders.size(); ++i) {
				String field = this.fieldOrders.get(i);
				if (!this.pksOrders.contains(field)) {
					int index = this.fieldOrders.indexOf(field);
					nr[idx++] = r[index];
				}
			}

			for (String pk : this.pksOrders) {
				int index = this.fieldOrders.indexOf(pk);
				nr[idx++] = r[index];
			}

			datas.add(nr);
		}

		batchUpdate(datas, this.updateStatementSql, this.updateArgsType);
		return records.size();
		/*TransactionStatus status = transactionManager.getTransaction(getTransactionDefinition());
		if (log.isDebugEnabled()) {
			log.debug("Execute Update SQL : {}", this.updateStatementSql);
		}

		try {
			int[] affects = jdbcTemplate.batchUpdate(this.updateStatementSql, datas, this.updateArgsType);

			transactionManager.commit(status);
			return affects.length;
		} catch (Exception e) {
			transactionManager.rollback(status);
			throw e;
		}*/
	}

	@Override
	public long executeDelete(List<Object[]> records) {
		List<Object[]> datas = new LinkedList<>();
		for (Object[] r : records) {
			Object[] nr = new Object[this.pksOrders.size()];
			for (int i = 0; i < this.pksOrders.size(); ++i) {
				String pk = this.pksOrders.get(i);
				int index = this.fieldOrders.indexOf(pk);
				nr[i] = r[index];
			}

			datas.add(nr);
		}
		batchDelete(datas, this.deleteStatementSql, this.deleteArgsType);
		return records.size();

		/*TransactionStatus status = transactionManager.getTransaction(getTransactionDefinition());
		if (log.isDebugEnabled()) {
			log.debug("Execute Delete SQL : {}", this.deleteStatementSql);
		}

		try {
			int[] affects = jdbcTemplate.batchUpdate(this.deleteStatementSql, datas, this.deleteArgsType);

			transactionManager.commit(status);
			return affects.length;
		} catch (Exception e) {
			transactionManager.rollback(status);
			throw e;
		} finally {
			datas.clear();
		}*/
	}

	protected void batchOperator(List<Object[]> recordValues, String batchSql, int[] argTypes) {
		try (Connection connection = dataSource.getConnection();
			 PreparedStatement ps = connection.prepareStatement(batchSql);) {
			connection.setAutoCommit(false);
			for (Object[] recordValue : recordValues) {
				for (int j = 0; j < argTypes.length; j++) {
					ps.setObject(j + 1, recordValue[j], argTypes[j]);
				}
				ps.addBatch();
			}
			ps.executeBatch();
			ps.clearBatch();
			connection.commit();
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}

	protected void batchInsert(List<Object[]> recordValues, String batchSql, int[] argTypes) {
		batchOperator(recordValues, batchSql, argTypes);
	}

	protected void batchDelete(List<Object[]> recordValues, String batchSql, int[] argTypes) {
		batchOperator(recordValues, batchSql, argTypes);
	}

	protected void batchUpdate(List<Object[]> recordValues, String batchSql, int[] argTypes) {
		batchOperator(recordValues, batchSql, argTypes);
	}

}
