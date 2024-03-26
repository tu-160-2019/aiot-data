// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbchange;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import srt.cloud.framework.dbswitch.common.type.SourceType;
import srt.cloud.framework.dbswitch.common.util.JdbcTypesUtils;
import srt.cloud.framework.dbswitch.common.util.TypeConvertUtils;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.service.IMetaDataByDatasourceService;
import srt.cloud.framework.dbswitch.core.service.impl.MetaDataByDataSourceServiceImpl;
import srt.cloud.framework.dbswitch.data.domain.RowDataAndPk;
import srt.cloud.framework.dbswitch.dbcommon.constant.Constants;
import srt.cloud.framework.dbswitch.dbcommon.database.DatabaseOperatorFactory;
import srt.cloud.framework.dbswitch.dbcommon.database.IDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.domain.PrepareStatementResultSet;
import srt.cloud.framework.dbswitch.dbcommon.domain.StatementResultSet;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * 数据变化量计算核心类
 *
 * @author jrl
 */
@Slf4j
public final class ChangeCalculatorService implements IDatabaseChangeCaculator {

	/**
	 * SimpleDateFormat不是线程安全的,所以需要放在ThreadLocal中来保证安全性
	 */
	private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = ThreadLocal.withInitial(() -> {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		return simpleDateFormat;
	});

	private static final ThreadLocal<SimpleDateFormat> TIMESTAMP_FORMAT = ThreadLocal.withInitial(() -> {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		return simpleDateFormat;
	});

	private static final ThreadLocal<SimpleDateFormat> TIME_FORMAT = ThreadLocal.withInitial(() -> {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		return simpleDateFormat;
	});

	private static final DateTimeFormatter DATE_FORMAT_NEW = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter TIMESTAMP_FORMAT_NEW = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final DateTimeFormatter TIME_FORMAT_NEW = DateTimeFormatter.ofPattern("HH:mm:ss");
	/**
	 * 是否记录不变化的记录
	 */
	private boolean recordIdentical;

	/**
	 * 是否进行jdbc数据type检查
	 */
	private boolean checkJdbcType;

	/**
	 * 批量读取数据的行数大小
	 */
	private int queryFetchSize;

	public ChangeCalculatorService() {
		this(false, true);
	}

	public ChangeCalculatorService(boolean recordIdentical, boolean checkJdbcType) {
		this.recordIdentical = recordIdentical;
		this.checkJdbcType = checkJdbcType;
		this.queryFetchSize = Constants.DEFAULT_FETCH_SIZE;
	}

	@Override
	public boolean isRecordIdentical() {
		return this.recordIdentical;
	}

	@Override
	public void setRecordIdentical(boolean recordOrNot) {
		this.recordIdentical = recordOrNot;
	}

	@Override
	public boolean isCheckJdbcType() {
		return this.checkJdbcType;
	}

	@Override
	public void setCheckJdbcType(boolean checkOrNot) {
		this.checkJdbcType = checkOrNot;
	}

	@Override
	public int getFetchSize() {
		return this.queryFetchSize;
	}

	@Override
	public void setFetchSize(int size) {
		if (size < Constants.MINIMUM_FETCH_SIZE) {
			throw new IllegalArgumentException(
					"设置的批量处理行数的大小fetchSize不得小于" + Constants.MINIMUM_FETCH_SIZE);
		}

		this.queryFetchSize = size;
	}

	/**
	 * 变化量计算函数
	 * <p>
	 * 说明 ： old 后缀的为目标段； new 后缀的为来源段；
	 * <p>
	 * 数据由 new 向 old 方向 同步。
	 *
	 * @param task    任务描述实体对象
	 * @param handler 计算结果回调处理器
	 */
	@Override
	public void executeCalculate(@NonNull TaskParamEntity task,
								 @NonNull IDatabaseRowHandler handler) {
		if (log.isDebugEnabled()) {
			log.debug("###### Begin execute calculate table primary keys CDC data now");
		}

		Map<String, String> columnsMap = task.getColumnsMap();
		boolean useOwnFieldsColumns = !CollectionUtils.isEmpty(task.getFieldColumns());

		// 检查新旧两张表的主键字段与比较字段
		IMetaDataByDatasourceService oldMd = new MetaDataByDataSourceServiceImpl(task.getOldDataSource(), task.getOldProductType());
		IMetaDataByDatasourceService newMd = new MetaDataByDataSourceServiceImpl(task.getNewDataSource(), task.getNewProductType());
		//List<String> fieldsPrimaryKeyOld = oldMd.queryTablePrimaryKeys(task.getOldSchemaName(), task.getOldTableName());
		List<String> fieldsAllColumnOld = oldMd.queryTableColumnName(task.getOldSchemaName(), task.getOldTableName());
		List<String> fieldsPrimaryKeyNew;
		List<String> fieldsAllColumnNew;
		if (SourceType.TABLE.getCode().equals(task.getSourceType())) {
			fieldsPrimaryKeyNew = CollectionUtils.isEmpty(task.getSourcePrimaryKeys()) ? newMd.queryTablePrimaryKeys(task.getNewSchemaName(), task.getNewTableName()) : task.getSourcePrimaryKeys();
			fieldsAllColumnNew = newMd.queryTableColumnName(task.getNewSchemaName(), task.getNewTableName());
		} else {
			fieldsPrimaryKeyNew = task.getSourcePrimaryKeys();
			fieldsAllColumnNew = newMd.querySqlColumnMeta(task.getSourceSql()).stream().map(ColumnDescription::getFieldName).collect(Collectors.toList());
		}

		List<String> fieldsMappedPrimaryKeyNew = fieldsPrimaryKeyNew.stream()
				.map(s -> columnsMap.getOrDefault(s, s))
				.collect(Collectors.toList());
		List<String> fieldsMappedAllColumnNew = fieldsAllColumnNew.stream()
				.map(s -> columnsMap.getOrDefault(s, s))
				.collect(Collectors.toList());

		if (fieldsPrimaryKeyNew.isEmpty()) {
			throw new RuntimeException("计算变化量的表中存在无主键的表");
		}

		//以指定的为准，这里不再根据数据库主键判断
		/*if (!isListEqual(fieldsPrimaryKeyOld, fieldsMappedPrimaryKeyNew)) {
			throw new RuntimeException("两个表的主键映射关系不匹配");
		}*/

		if (useOwnFieldsColumns) {
			// 如果自己配置了字段列表，判断子集关系
			List<String> mappedFieldColumns = task.getFieldColumns().stream()
					.map(s -> columnsMap.getOrDefault(s, s))
					.collect(Collectors.toList());
			if (!fieldsAllColumnNew.containsAll(task.getFieldColumns())
					|| !fieldsAllColumnOld.containsAll(mappedFieldColumns)) {
				throw new RuntimeException("指定的字段列不完全在两个表中存在");
			}
			boolean same = (mappedFieldColumns.containsAll(fieldsMappedPrimaryKeyNew)
					&& task.getFieldColumns().containsAll(fieldsPrimaryKeyNew));
			if (!same) {
				throw new RuntimeException("提供的比较字段中未包含主键");
			}
			same = (fieldsAllColumnOld.containsAll(mappedFieldColumns)
					&& fieldsAllColumnNew.containsAll(task.getFieldColumns()));
			if (!same) {
				throw new RuntimeException("提供的比较字段中存在表中不存在(映射关系对不上)的字段");
			}
		} else {
			boolean same = (fieldsMappedAllColumnNew.containsAll(fieldsMappedPrimaryKeyNew)
					&& fieldsAllColumnOld.containsAll(fieldsMappedAllColumnNew));
			if (!same) {
				throw new RuntimeException("两个表的字段映射关系不匹配");
			}
		}

		// 计算除主键外的比较字段
		List<String> fieldsOfCompareValue = new ArrayList<>();
		if (useOwnFieldsColumns) {
			fieldsOfCompareValue.addAll(task.getFieldColumns());
		} else {
			fieldsOfCompareValue.addAll(fieldsAllColumnNew);
		}
		fieldsOfCompareValue.removeAll(fieldsPrimaryKeyNew);

		// 构造查询列字段
		List<String> queryFieldColumn;
		List<String> mappedQueryFieldColumn;
		if (useOwnFieldsColumns) {
			queryFieldColumn = task.getFieldColumns();
		} else {
			queryFieldColumn = fieldsAllColumnOld;
		}
		mappedQueryFieldColumn = queryFieldColumn.stream()
				.map(s -> columnsMap.getOrDefault(s, s))
				.collect(Collectors.toList());

		StatementResultSet rsold = null;
		StatementResultSet rsnew = null;

		try {
			// 提取新旧两表数据的结果集(按主键排序后的)
			IDatabaseOperator oldQuery = DatabaseOperatorFactory
					.createDatabaseOperator(task.getOldDataSource(), task.getOldProductType());
			oldQuery.setFetchSize(this.queryFetchSize);
			IDatabaseOperator newQuery = DatabaseOperatorFactory
					.createDatabaseOperator(task.getNewDataSource(), task.getNewProductType());
			newQuery.setFetchSize(this.queryFetchSize);

			if (log.isDebugEnabled()) {
				log.debug("###### Query data from two table now");
			}

			rsold = oldQuery
					.queryTableData(task.getOldSchemaName(), task.getOldTableName(),
							mappedQueryFieldColumn, fieldsMappedPrimaryKeyNew);
			if (SourceType.TABLE.getCode().equals(task.getSourceType())) {
				rsnew = newQuery
						.queryTableData(task.getNewSchemaName(), task.getNewTableName(),
								queryFieldColumn, fieldsPrimaryKeyNew);
			} else {
				//sql
				rsnew = newQuery.queryTableData(task.getSourceSql(), queryFieldColumn, fieldsPrimaryKeyNew);
			}

			ResultSetMetaData metaData = rsnew.getResultset().getMetaData();

			if (log.isDebugEnabled()) {
				log.debug("###### Check data validate now");
			}

			// 检查结果集源信息是否一直
			int oldcnt = rsold.getResultset().getMetaData().getColumnCount();
			int newcnt = rsnew.getResultset().getMetaData().getColumnCount();
			if (oldcnt != newcnt) {
				throw new RuntimeException(String.format("两个表的字段总个数不相等，即：%d!=%d", oldcnt, newcnt));
			} else {
				for (int k = 1; k < metaData.getColumnCount(); ++k) {
					String key1 = rsnew.getResultset().getMetaData().getColumnLabel(k);
					if (null == key1) {
						key1 = rsnew.getResultset().getMetaData().getColumnName(k);
					}

					String key2 = rsold.getResultset().getMetaData().getColumnLabel(k);
					if (null == key2) {
						key2 = rsold.getResultset().getMetaData().getColumnName(k);
					}

					if (checkJdbcType) {
						int type1 = rsold.getResultset().getMetaData().getColumnType(k);
						int type2 = rsnew.getResultset().getMetaData().getColumnType(k);
						if (type1 != type2) {
							throw new RuntimeException(String.format("字段 [name=%s -> %s] 的数据类型不同，因 %s!=%s !",
									key1, key2,
									JdbcTypesUtils.resolveTypeName(type1), JdbcTypesUtils.resolveTypeName(type2)));
						}
					}

				}
			}

			// 计算主键字段序列在结果集中的索引号
			int[] keyNumbers = new int[fieldsPrimaryKeyNew.size()];
			for (int i = 0; i < keyNumbers.length; ++i) {
				String fn = fieldsPrimaryKeyNew.get(i);
				keyNumbers[i] = getIndexOfField(fn, metaData);
			}

			// 计算比较(非主键)字段序列在结果集中的索引号
			int[] valNumbers = new int[fieldsOfCompareValue.size()];
			for (int i = 0; i < valNumbers.length; ++i) {
				String fn = fieldsOfCompareValue.get(i);
				valNumbers[i] = getIndexOfField(fn, metaData);
			}

			// 初始化计算结果数据字段列信息
			List<String> targetColumns = new ArrayList<>();
			for (int k = 1; k <= metaData.getColumnCount(); ++k) {
				String key = metaData.getColumnLabel(k);
				if (null == key) {
					key = metaData.getColumnName(k);
				}
				targetColumns.add(columnsMap.getOrDefault(key, key));
			}

			if (log.isDebugEnabled()) {
				log.debug("###### Enter CDC calculate now");
			}

			// 进入核心比较计算算法区域
			RecordChangeTypeEnum flagField = null;
			Object[] outputRow;
			Object[] one = getRowData(rsold.getResultset());
			Object[] two = getRowData(rsnew.getResultset());
			while (true) {
				if (one == null && two == null) {
					break;
				} else if (one == null && two != null) {
					flagField = RecordChangeTypeEnum.VALUE_INSERT;
					outputRow = two;
					two = getRowData(rsnew.getResultset());
				} else if (one != null && two == null) {
					flagField = RecordChangeTypeEnum.VALUE_DELETED;
					outputRow = one;
					one = getRowData(rsold.getResultset());
				} else {
					int compare = this.compare(one, two, keyNumbers, metaData);
					if (0 == compare) {
						int compareValues = this.compare(one, two, valNumbers, metaData);
						if (compareValues == 0) {
							flagField = RecordChangeTypeEnum.VALUE_IDENTICAL;
							outputRow = one;
						} else {
							flagField = RecordChangeTypeEnum.VALUE_CHANGED;
							outputRow = two;
						}

						one = getRowData(rsold.getResultset());
						two = getRowData(rsnew.getResultset());
					} else {
						if (compare < 0) {
							flagField = RecordChangeTypeEnum.VALUE_DELETED;
							outputRow = one;
							one = getRowData(rsold.getResultset());
						} else {
							flagField = RecordChangeTypeEnum.VALUE_INSERT;
							outputRow = two;
							two = getRowData(rsnew.getResultset());
						}
					}
				}

				if (!this.recordIdentical && RecordChangeTypeEnum.VALUE_IDENTICAL == flagField) {
					continue;
				}

				// 这里对计算的单条记录结果进行处理
				handler.handle(Collections.unmodifiableList(targetColumns), outputRow, flagField);
			}

			if (log.isDebugEnabled()) {
				log.debug("###### Calculate CDC Over now");
			}

			// 结束返回前的回调
			handler.destroy(Collections.unmodifiableList(targetColumns));

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (null != rsold) {
				rsold.close();
			}
			if (null != rsnew) {
				rsnew.close();
			}
		}

	}

	@Override
	public void executeCalculate(TaskIncreaseParamEntity task, IDatabaseRowHandler handler) {
		if (log.isDebugEnabled()) {
			log.debug("###### Begin execute calculate table increase column CDC data now");
		}

		Map<String, String> columnsMap = task.getColumnsMap();

		// 检查新旧两张表的主键字段与比较字段
		IMetaDataByDatasourceService sourceMd = new MetaDataByDataSourceServiceImpl(task.getSourceDataSource(), task.getSourceProductType());
		IMetaDataByDatasourceService targetMd = new MetaDataByDataSourceServiceImpl(task.getTargetDataSource(), task.getTargetProductType());
		List<String> sourcePrimaryKeys = task.getSourcePrimaryKeys();
		List<String> fieldsAllColumnTarget = targetMd.queryTableColumnName(task.getTargetSchemaName(), task.getTargetTableName());
		List<String> fieldsPrimaryKeySource;
		List<String> fieldsAllColumnSource;
		if (SourceType.TABLE.getCode().equals(task.getSourceType())) {
			fieldsPrimaryKeySource = CollectionUtils.isEmpty(sourcePrimaryKeys) ? sourceMd.queryTablePrimaryKeys(task.getSourceSchemaName(), task.getSourceTableName()) : sourcePrimaryKeys;
			fieldsAllColumnSource = sourceMd.queryTableColumnName(task.getSourceSchemaName(), task.getSourceTableName());
		} else {
			fieldsPrimaryKeySource = task.getSourcePrimaryKeys();
			fieldsAllColumnSource = sourceMd.querySqlColumnMeta(task.getSourceSql()).stream().map(ColumnDescription::getFieldName).collect(Collectors.toList());
		}

		List<String> fieldsPrimaryKeyTarget = fieldsPrimaryKeySource.stream()
				.map(s -> columnsMap.getOrDefault(s, s))
				.collect(Collectors.toList());

		List<String> mappedFieldColumns = task.getFieldColumns().stream()
				.map(s -> columnsMap.getOrDefault(s, s))
				.collect(Collectors.toList());
		boolean same = (fieldsAllColumnTarget.containsAll(mappedFieldColumns)
				&& fieldsAllColumnSource.containsAll(task.getFieldColumns()));
		if (!same) {
			throw new RuntimeException("提供的字段中存在表中不存在(映射关系对不上)的字段");
		}
		// 构造查询列字段
		List<String> queryFieldColumn = task.getFieldColumns();

		PrepareStatementResultSet rsSource = null;

		try {
			IDatabaseOperator targetQuery = DatabaseOperatorFactory
					.createDatabaseOperator(task.getTargetDataSource(), task.getTargetProductType());
			IDatabaseOperator sourceQuery = DatabaseOperatorFactory
					.createDatabaseOperator(task.getSourceDataSource(), task.getSourceProductType());
			sourceQuery.setFetchSize(this.queryFetchSize);

			if (log.isDebugEnabled()) {
				log.debug("###### Query data from two table now");
			}

			if (SourceType.TABLE.getCode().equals(task.getSourceType())) {
				rsSource = sourceQuery
						.queryIncreaseTableData(task.getSourceSchemaName(), task.getSourceTableName(),
								queryFieldColumn, task.getIncreaseColumn(), task.getStartVal(), task.getEndVal());
			} else {
				//sql
				rsSource = sourceQuery.queryIncreaseTableData(task.getSourceSql(), queryFieldColumn, task.getIncreaseColumn(), task.getStartVal(), task.getEndVal());
			}
			//获取增量数据
			while (true) {
				RowDataAndPk rowDataAnPk = getRowDataAnPk(rsSource.getResultset(), fieldsPrimaryKeyTarget);
				if (rowDataAnPk == null) {
					break;
				}
				//判断目的端是否有这个主键，没有新增，有则更新
				if (targetQuery.getPkExist(task.getTargetSchemaName(), task.getTargetTableName(), rowDataAnPk.getPkVal())) {
					handler.handle(Collections.unmodifiableList(mappedFieldColumns), rowDataAnPk.getRow(), RecordChangeTypeEnum.VALUE_CHANGED);
				} else {
					handler.handle(Collections.unmodifiableList(mappedFieldColumns), rowDataAnPk.getRow(), RecordChangeTypeEnum.VALUE_INSERT);
				}
			}

			if (log.isDebugEnabled()) {
				log.debug("###### Calculate CDC increase column Over now");
			}
			// 结束返回前的回调
			handler.destroy(Collections.unmodifiableList(mappedFieldColumns));

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (null != rsSource) {
				rsSource.close();
			}
		}

	}


	private boolean isListEqual(List<String> left, List<String> right) {
		return left.containsAll(right) && right.containsAll(left);
	}

	/**
	 * 获取字段的索引号
	 *
	 * @param key      字段名
	 * @param metaData 结果集的元信息
	 * @return 字段的索引号
	 * @throws SQLException
	 */
	private int getIndexOfField(String key, ResultSetMetaData metaData) throws SQLException {
		for (int k = 1; k <= metaData.getColumnCount(); ++k) {
			String fieldName = metaData.getColumnLabel(k);
			if (null == fieldName) {
				fieldName = metaData.getColumnName(k);
			}

			if (fieldName.equals(key)) {
				return k - 1;
			}
		}

		return -1;
	}

	/**
	 * 记录比较
	 *
	 * @param obj1     记录1
	 * @param obj2     记录2
	 * @param fieldnrs 待比较的字段索引号
	 * @param metaData 记录集的元信息
	 * @return 比较的结果：0，-1，1
	 * @throws SQLException
	 */
	private int compare(Object[] obj1, Object[] obj2, int[] fieldnrs, ResultSetMetaData metaData)
			throws SQLException {
		if (obj1.length != obj2.length) {
			throw new RuntimeException("Invalid compare object list !");
		}

		for (int fieldnr : fieldnrs) {
			int jdbcType = metaData.getColumnType(fieldnr + 1);
			Object o1 = obj1[fieldnr];
			Object o2 = obj2[fieldnr];

			int cmp = typeCompare(jdbcType, o1, o2);
			if (cmp != 0) {
				return cmp;
			}
		}

		return 0;
	}

	/**
	 * 字段值对象比较，将对象转换为字节数组来比较实现
	 *
	 * @param type 字段的JDBC数据类型
	 * @param o1   对象1
	 * @param o2   对象2
	 * @return 0为相等，-1为小于，1为大于
	 */
	private int typeCompare(int type, Object o1, Object o2) {
		boolean n1 = (o1 == null);
		boolean n2 = (o2 == null);
		if (n1 && !n2) {
			return -1;
		}
		if (!n1 && n2) {
			return 1;
		}
		if (n1 && n2) {
			return 0;
		}

		/**
		 * <p>
		 * 这里要比较的两个对象o1与o2可能类型不同，但值相同，例如： Integer o1=12，Long o2=12;
		 * </p>
		 * <p>
		 * 但是这种不属于同一类的比较情况不应出现: String o1="12", Integer o2=12;
		 * </p>
		 */
		if (JdbcTypesUtils.isString(type)) {
			String s1 = TypeConvertUtils.castToString(o1);
			String s2 = TypeConvertUtils.castToString(o2);
			return s1.compareTo(s2);
		} else if (JdbcTypesUtils.isNumeric(type) && o1 instanceof Number
				&& o2 instanceof Number) {
			Number s1 = (Number) o1;
			Number s2 = (Number) o2;
			return Double.compare(s1.doubleValue(), s2.doubleValue());
		} else if ((JdbcTypesUtils.isInteger(type) || JdbcTypesUtils.isBoolean(type)) &&
				(o1 instanceof Number && o2 instanceof Number ||
						o1 instanceof Number && o2 instanceof Boolean ||
						o1 instanceof Boolean && o2 instanceof Number ||
						o1 instanceof Boolean && o2 instanceof Boolean)) {
			Number s1 = o1 instanceof Boolean ? (Boolean) o1 ? 1 : 0 : (Number) o1;
			Number s2 = o2 instanceof Boolean ? (Boolean) o2 ? 1 : 0 : (Number) o2;
			return Long.compare(s1.longValue(), s2.longValue());
		} else if (JdbcTypesUtils.isDateTime(type)) {
			if (o1 instanceof java.sql.Time && o2 instanceof java.sql.Time) {
				java.sql.Time t1 = (java.sql.Time) o1;
				java.sql.Time t2 = (java.sql.Time) o2;
				return t1.compareTo(t2);
			} else if (o1 instanceof java.sql.Timestamp && o2 instanceof java.sql.Timestamp) {
				java.sql.Timestamp t1 = (java.sql.Timestamp) o1;
				java.sql.Timestamp t2 = (java.sql.Timestamp) o2;
				return t1.compareTo(t2);
			} else if (o1 instanceof java.sql.Date && o2 instanceof java.sql.Date) {
				java.sql.Date t1 = (java.sql.Date) o1;
				java.sql.Date t2 = (java.sql.Date) o2;
				return t1.compareTo(t2);
			} else if (o1 instanceof LocalDateTime && o2 instanceof LocalDateTime) {
				LocalDateTime t1 = (LocalDateTime) o1;
				LocalDateTime t2 = (LocalDateTime) o2;
				return t1.compareTo(t2);
			} else {
				String s1 = getDateStr(o1);
				String s2 = getDateStr(o2);
				return s1.compareTo(s2);
			}
		} else {
			try {
				return compareTo(
						TypeConvertUtils.castToByteArray(o1),
						TypeConvertUtils.castToByteArray(o2)
				);
			} catch (Exception e) {
				log.warn("CDC compare field value failed, return 0 instead,{}", e.getMessage());
				return 0;
			}
		}
	}

	private String getDateStr(Object o) {
		String s;
		if (o instanceof java.sql.Time) {
			s = TIME_FORMAT.get().format(o);
		} else if (o instanceof java.sql.Timestamp) {
			s = TIMESTAMP_FORMAT.get().format(o);
		} else if (o instanceof java.sql.Date) {
			s = TIMESTAMP_FORMAT.get().format(o);
		} else if (o instanceof LocalDateTime) {
			LocalDateTime t = (LocalDateTime) o;
			s = TIMESTAMP_FORMAT_NEW.format(t);
		} else if (o instanceof LocalDate) {
			LocalDate t = (LocalDate) o;
			s = TIMESTAMP_FORMAT_NEW.format(t);
		} else {
			s = String.valueOf(o);
		}
		return s;
	}

	/**
	 * 字节数组的比较
	 *
	 * @param s1 字节数组1
	 * @param s2 字节数组2
	 * @return 0为相等，-1为小于，1为大于
	 */
	public int compareTo(byte[] s1, byte[] s2) {
		int len1 = s1.length;
		int len2 = s2.length;
		int lim = Math.min(len1, len2);
		byte[] v1 = s1;
		byte[] v2 = s2;

		int k = 0;
		while (k < lim) {
			byte c1 = v1[k];
			byte c2 = v2[k];
			if (c1 != c2) {
				return c1 - c2;
			}
			k++;
		}
		return len1 - len2;
	}

	/**
	 * 从结果集中取出一条记录
	 *
	 * @param rs 记录集
	 * @return 一条记录，到记录结尾时返回null
	 * @throws SQLException
	 */
	private Object[] getRowData(ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		Object[] rowData = null;

		if (rs.next()) {
			rowData = new Object[metaData.getColumnCount()];
			for (int j = 1; j <= metaData.getColumnCount(); ++j) {
				rowData[j - 1] = rs.getObject(j);
			}
		}

		return rowData;
	}

	private RowDataAndPk getRowDataAnPk(ResultSet rs, List<String> fieldsPrimaryKeyTarget) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		RowDataAndPk rowDataAndPk = null;
		Object[] rowData;
		Map<String, Object> pkVal;

		if (rs.next()) {
			rowDataAndPk = new RowDataAndPk();
			pkVal = new HashMap<>();
			rowData = new Object[metaData.getColumnCount()];
			rowDataAndPk.setRow(rowData);
			rowDataAndPk.setPkVal(pkVal);
			for (int j = 1; j <= metaData.getColumnCount(); ++j) {
				String columnLabel = metaData.getColumnLabel(j);
				Object object = rs.getObject(j);
				if (fieldsPrimaryKeyTarget.contains(columnLabel)) {
					pkVal.put(columnLabel, object);
				}
				rowData[j - 1] = object;
			}
		}
		return rowDataAndPk;
	}

}
