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

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.dbcommon.constant.Constants;
import srt.cloud.framework.dbswitch.dbcommon.domain.PrepareStatementResultSet;
import srt.cloud.framework.dbswitch.dbcommon.domain.StatementResultSet;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/**
 * 数据读取抽象基类
 *
 * @author jrl
 */
@Slf4j
public abstract class AbstractDatabaseOperator implements IDatabaseOperator {

	private static final ThreadLocal<SimpleDateFormat> TIMESTAMP_FORMAT = ThreadLocal.withInitial(() -> {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		return simpleDateFormat;
	});


	protected DataSource dataSource;

	protected int fetchSize;

	public AbstractDatabaseOperator(DataSource dataSource) {
		this.dataSource = Objects.requireNonNull(dataSource, "数据源非法,为null");
		this.fetchSize = Constants.DEFAULT_FETCH_SIZE;
	}

	@Override
	public DataSource getDataSource() {
		return this.dataSource;
	}

	@Override
	public int getFetchSize() {
		return this.fetchSize;
	}

	@Override
	public String getEscape() {
		return "\"";
	}


	@Override
	public PrepareStatementResultSet queryIncreaseTableData(String sourceSchemaName, String sourceTableName, List<String> queryFieldColumn, ColumnDescription increaseColumn, String startVal, String endVal) {
		return getPrepareStatementResultSet(new StringBuilder(String.format("select %s%s%s from %s%s%s.%s%s%s where 1=1", getEscape(), StringUtils.join(queryFieldColumn, "`,`"), getEscape(), getEscape(), sourceSchemaName, getEscape(), getEscape(), sourceTableName, getEscape())), increaseColumn, startVal, endVal);
	}

	@Override
	public PrepareStatementResultSet queryIncreaseTableData(String sourceSql, List<String> queryFieldColumn, ColumnDescription increaseColumn, String startVal, String endVal) {
		return getPrepareStatementResultSet(new StringBuilder(String.format("select %s%s%s from (%s) t where 1=1", getEscape(), StringUtils.join(queryFieldColumn, "`,`"), getEscape(), sourceSql)), increaseColumn, startVal, endVal);
	}

	@SneakyThrows
	private PrepareStatementResultSet getPrepareStatementResultSet(StringBuilder sql, ColumnDescription increaseColumn, String startVal, String endVal) {
		int fieldType = increaseColumn.getFieldType();
		Object start;
		Object end;
		switch (fieldType) {
			case java.sql.Types.BIGINT:
			case java.sql.Types.INTEGER:
				start = startVal != null ? Integer.parseInt(startVal) : null;
				end = endVal != null ? Integer.parseInt(endVal) : null;
				break;
			case java.sql.Types.TIMESTAMP:
			case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
			case java.sql.Types.DATE:
				start = startVal != null ? TIMESTAMP_FORMAT.get().parse(startVal) : null;
				end = endVal != null ? TIMESTAMP_FORMAT.get().parse(endVal) : null;
				break;
			default:
				start = startVal;
				end = endVal;
		}
		List<Object> params = new ArrayList<>();
		if (start != null) {
			sql.append(String.format(" and %s%s%s>=?", getEscape(), increaseColumn.getFieldName(), getEscape()));
			params.add(start);
		}
		if (end != null) {
			sql.append(String.format(" and %s%s%s<=?", getEscape(), increaseColumn.getFieldName(), getEscape()));
			params.add(end);
		}
		return this.selectTableData(sql.toString(), this.fetchSize, params);
	}

	@Override
	public Boolean getPkExist(String targetSchemaName, String targetTableName, Map<String, Object> pkVal) throws SQLException {
		if (CollectionUtils.isEmpty(pkVal)) {
			return false;
		}
		StringBuilder sql = new StringBuilder(String.format("select 1 from %s%s%s.%s%s%s where 1=1", getEscape(), targetSchemaName, getEscape(), getEscape(), targetTableName, getEscape()));
		List<Object> params = new ArrayList<>();
		for (Map.Entry<String, Object> entry : pkVal.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			sql.append(String.format(" AND %s=?", key));
			params.add(value);
		}
		try (PrepareStatementResultSet resultSet = this.selectTableData(sql.toString(), Constants.DEFAULT_FETCH_SIZE, params);) {
			return resultSet.getResultset().next();
		}
	}

	@Override
	public StatementResultSet queryTableData(String sql) {
		return this.selectTableData(sql, this.fetchSize);
	}

	@Override
	public void setFetchSize(int size) {
		/*if (size < Constants.MINIMUM_FETCH_SIZE) {
			throw new IllegalArgumentException(
					"设置的批量处理行数的大小fetchSize不得小于" + Constants.MINIMUM_FETCH_SIZE);
		}*/

		this.fetchSize = size;
	}

	/**
	 * 已经指定的查询SQL语句查询数据结果集
	 *
	 * @param sql       查询的SQL语句
	 * @param fetchSize 批处理大小
	 * @return 结果集包装对象
	 */
	protected final StatementResultSet selectTableData(String sql, int fetchSize) {
		if (log.isDebugEnabled()) {
			log.debug("Query table data sql :{}", sql);
		}

		try {
			StatementResultSet srs = new StatementResultSet();
			srs.setConnection(dataSource.getConnection());
			srs.setAutoCommit(srs.getConnection().getAutoCommit());
			srs.getConnection().setAutoCommit(false);
			srs.setStatement(srs.getConnection()
					.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY));
			srs.getStatement().setQueryTimeout(Constants.DEFAULT_QUERY_TIMEOUT_SECONDS);
			srs.getStatement().setFetchSize(fetchSize);
			srs.setResultset(srs.getStatement().executeQuery(sql));
			return srs;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	/**
	 * 已经指定的查询SQL语句查询数据结果集
	 *
	 * @param sql       查询的SQL语句
	 * @param fetchSize 批处理大小
	 * @return 结果集包装对象
	 */
	private PrepareStatementResultSet selectTableData(String sql, int fetchSize, List<Object> params) {
		if (log.isDebugEnabled()) {
			log.debug("Query table data sql :{}", sql);
		}

		try {
			PrepareStatementResultSet srs = new PrepareStatementResultSet();
			srs.setConnection(dataSource.getConnection());
			srs.setAutoCommit(srs.getConnection().getAutoCommit());
			srs.getConnection().setAutoCommit(false);
			PreparedStatement preparedStatement = srs.getConnection().prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			for (int i = 0; i < params.size(); i++) {
				preparedStatement.setObject(i + 1, params.get(i));
			}
			srs.setStatement(preparedStatement);
			srs.getStatement().setQueryTimeout(Constants.DEFAULT_QUERY_TIMEOUT_SECONDS);
			srs.getStatement().setFetchSize(fetchSize);
			srs.setResultset(srs.getStatement().executeQuery());
			return srs;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	/**
	 * 执行写SQL操作
	 *
	 * @param sql 写SQL语句
	 */
	protected final int executeSql(String sql) {
		if (log.isDebugEnabled()) {
			log.debug("Execute sql :{}", sql);
		}
		try (Connection connection = dataSource.getConnection();
			 Statement st = connection.createStatement()) {
			return st.executeUpdate(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
