// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.data.handler;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.sizeof.SizeOf;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import srt.cloud.framework.dbswitch.common.constant.ChangeDataSyncType;
import srt.cloud.framework.dbswitch.common.constant.MapperType;
import srt.cloud.framework.dbswitch.common.entity.MapperConfig;
import srt.cloud.framework.dbswitch.common.entity.PatternMapper;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.type.SourceType;
import srt.cloud.framework.dbswitch.common.util.DatabaseAwareUtils;
import srt.cloud.framework.dbswitch.common.util.ExamineUtils;
import srt.cloud.framework.dbswitch.common.util.PatterNameUtils;
import srt.cloud.framework.dbswitch.common.util.StringUtil;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.TableDescription;
import srt.cloud.framework.dbswitch.core.service.IMetaDataByDatasourceService;
import srt.cloud.framework.dbswitch.core.service.impl.MetaDataByDataSourceServiceImpl;
import srt.cloud.framework.dbswitch.data.config.DbswichProperties;
import srt.cloud.framework.dbswitch.data.domain.DbSwitchTableResult;
import srt.cloud.framework.dbswitch.data.entity.SourceDataSourceProperties;
import srt.cloud.framework.dbswitch.data.entity.TargetDataSourceProperties;
import srt.cloud.framework.dbswitch.data.util.BytesUnitUtils;
import srt.cloud.framework.dbswitch.dbchange.ChangeCalculatorService;
import srt.cloud.framework.dbswitch.dbchange.IDatabaseChangeCaculator;
import srt.cloud.framework.dbswitch.dbchange.IDatabaseRowHandler;
import srt.cloud.framework.dbswitch.dbchange.RecordChangeTypeEnum;
import srt.cloud.framework.dbswitch.dbchange.TaskIncreaseParamEntity;
import srt.cloud.framework.dbswitch.dbchange.TaskParamEntity;
import srt.cloud.framework.dbswitch.dbcommon.database.DatabaseOperatorFactory;
import srt.cloud.framework.dbswitch.dbcommon.database.IDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.domain.StatementResultSet;
import srt.cloud.framework.dbswitch.dbsynch.DatabaseSynchronizeFactory;
import srt.cloud.framework.dbswitch.dbsynch.IDatabaseSynchronize;
import srt.cloud.framework.dbswitch.dbwriter.DatabaseWriterFactory;
import srt.cloud.framework.dbswitch.dbwriter.IDatabaseWriter;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 在一个线程内的单表迁移处理逻辑
 *
 * @author jrl
 */
@Slf4j
public class MigrationHandler implements Supplier<DbSwitchTableResult> {

	private final long MAX_CACHE_BYTES_SIZE = 512 * 1024 * 1024;

	private int fetchSize = 100;
	private final DbswichProperties properties;
	private final SourceDataSourceProperties sourceProperties;
	private final TargetDataSourceProperties targetProperties;

	private volatile boolean interrupted = false;

	// 来源端
	private final HikariDataSource sourceDataSource;
	private ProductTypeEnum sourceProductType;
	private String sourceSchemaName;
	private String sourceTableName;
	private String sourceTableRemarks;
	private List<ColumnDescription> sourceColumnDescriptions;
	private List<String> sourcePrimaryKeys;
	private String increaseColumnName;
	private ColumnDescription increaseColumn;
	private List<PatternMapper> regexTableMapper;
	private List<PatternMapper> regexColumnMapper;

	private IMetaDataByDatasourceService sourceMetaDataService;

	// 目的端
	private final HikariDataSource targetDataSource;
	private ProductTypeEnum targetProductType;
	private String targetSchemaName;
	private String targetTableName;
	private List<ColumnDescription> targetColumnDescriptions;
	private List<String> targetPrimaryKeys;

	// 日志输出字符串使用
	private String tableNameMapString;

	public static MigrationHandler createInstance(TableDescription td,
												  DbswichProperties properties,
												  Integer sourcePropertiesIndex,
												  HikariDataSource sds,
												  HikariDataSource tds) {
		return new MigrationHandler(td, properties, sourcePropertiesIndex, sds, tds);
	}

	private MigrationHandler(TableDescription td,
							 DbswichProperties properties,
							 Integer sourcePropertiesIndex,
							 HikariDataSource sds,
							 HikariDataSource tds) {
		this.properties = properties;
		this.sourceProperties = properties.getSource().get(sourcePropertiesIndex);
		this.targetProperties = properties.getTarget();
		this.sourceSchemaName = td.getSchemaName();
		this.sourceTableName = td.getTableName();
		this.sourceDataSource = sds;
		this.targetDataSource = tds;

		if (MapperType.ALL.name().equals(sourceProperties.getMapperType())) {
			this.regexTableMapper = sourceProperties.getRegexTableMapper();
			this.regexColumnMapper = sourceProperties.getRegexColumnMapper();
			this.sourcePrimaryKeys = sourceProperties.getSourcePrimaryKeys();
			this.increaseColumnName = sourceProperties.getIncreaseColumnName();
		} else {
			Map<String, MapperConfig> configMap = sourceProperties.getConfigMap();
			MapperConfig mapperConfig = configMap.getOrDefault(sourceTableName, new MapperConfig());
			this.regexTableMapper = mapperConfig.getRegexTableMapper();
			this.regexColumnMapper = mapperConfig.getRegexColumnMapper();
			this.sourcePrimaryKeys = mapperConfig.getSourcePrimaryKeys();
			this.increaseColumnName = mapperConfig.getIncreaseColumnName();
		}

		if (sourceProperties.getFetchSize() >= fetchSize) {
			fetchSize = sourceProperties.getFetchSize();
		}

		// 获取映射转换后新的表名
		this.targetSchemaName = properties.getTarget().getTargetSchema();
		if (SourceType.SQL.getCode().equals(sourceProperties.getSourceType())) {
			this.targetTableName = targetProperties.getTargetTable();
		} else {
			this.targetTableName = PatterNameUtils.getFinalName(sourceTableName, regexTableMapper);
		}
		if (StringUtils.isEmpty(this.targetTableName)) {
			throw new RuntimeException("表名的映射规则配置有误，不能将[" + this.sourceTableName + "]映射为空");
		}
		//添加表名前缀
		if (SourceType.TABLE.getCode().equals(sourceProperties.getSourceType()) && StringUtil.isNotBlank(properties.getTarget().getTablePrefix()) && !this.targetTableName.startsWith(properties.getTarget().getTablePrefix())) {
			this.targetTableName = properties.getTarget().getTablePrefix() + this.targetTableName;
		}
		if (properties.getTarget().getLowercase()) {
			this.targetTableName = this.targetTableName.toLowerCase();
		}
		if (properties.getTarget().getUppercase()) {
			this.targetTableName = this.targetTableName.toUpperCase();
		}

		this.tableNameMapString = String.format("%s.%s --> %s.%s",
				sourceSchemaName, SourceType.TABLE.getCode().equals(sourceProperties.getSourceType()) ? sourceTableName : sourceProperties.getSourceSql(),
				targetSchemaName, targetTableName);
	}

	public void interrupt() {
		this.interrupted = true;
	}

	@Override
	public DbSwitchTableResult get() {

		log.info("Begin Migrate table for {}", tableNameMapString);

		/*this.sourceProductType = DatabaseAwareUtils.getDatabaseTypeByDataSource(sourceDataSource);
		this.targetProductType = DatabaseAwareUtils.getDatabaseTypeByDataSource(targetDataSource);*/
		this.sourceProductType = sourceProperties.getSourceProductType();
		this.targetProductType = targetProperties.getTargetProductType();
		this.sourceMetaDataService = new MetaDataByDataSourceServiceImpl(sourceDataSource,
				sourceProductType);

		// 读取源表的表及字段元数据
		if (SourceType.TABLE.getCode().equals(sourceProperties.getSourceType())) {
			this.sourceTableRemarks = sourceMetaDataService
					.getTableRemark(sourceSchemaName, sourceTableName);
			this.sourceColumnDescriptions = sourceMetaDataService
					.queryTableColumnMeta(sourceSchemaName, sourceTableName);
			this.sourcePrimaryKeys = CollectionUtils.isEmpty(sourcePrimaryKeys) ? sourceMetaDataService
					.queryTablePrimaryKeys(sourceSchemaName, sourceTableName) : sourcePrimaryKeys;
		} else if (SourceType.SQL.getCode().equals(sourceProperties.getSourceType())) {
			this.sourceColumnDescriptions = sourceMetaDataService.querySqlColumnMeta(sourceProperties.getSourceSql());
		}
		//增量字段
		this.increaseColumn = this.sourceColumnDescriptions.stream().filter(item->item.getFieldName().equals(this.increaseColumnName)).findFirst().orElse(null);

		// 根据表的列名映射转换准备目标端表的字段信息
		this.targetColumnDescriptions = sourceColumnDescriptions.stream()
				.map(column -> {
					String newName = PatterNameUtils.getFinalName(
							column.getFieldName(),
							regexColumnMapper);
					ColumnDescription description = column.copy();
					description.setFieldName(properties.getTarget().getLowercase() && newName != null ? newName.toLowerCase() : properties.getTarget().getUppercase() && newName != null ? newName.toUpperCase() : newName);
					description.setLabelName(properties.getTarget().getLowercase() && newName != null ? newName.toLowerCase() : properties.getTarget().getUppercase() && newName != null ? newName.toUpperCase() : newName);
					return description;
				}).collect(Collectors.toList());
		this.targetPrimaryKeys = sourcePrimaryKeys.stream()
				.map(name -> {
							String finalName = PatterNameUtils.getFinalName(name, regexColumnMapper);
							if (properties.getTarget().getLowercase() && finalName != null) {
								finalName = finalName.toLowerCase();
							}
							if (properties.getTarget().getUppercase() && finalName != null) {
								finalName = finalName.toUpperCase();
							}
							return finalName;
						}
				).collect(Collectors.toList());

		//构建表的同步结果
		DbSwitchTableResult dbSwitchTableResult = new DbSwitchTableResult();
		dbSwitchTableResult.setSourceSchemaName(sourceSchemaName);
		dbSwitchTableResult.setSourceTableName(SourceType.TABLE.getCode().equals(sourceProperties.getSourceType()) ? sourceTableName : sourceProperties.getSourceSql());
		dbSwitchTableResult.setTargetSchemaName(targetSchemaName);
		dbSwitchTableResult.setTargetTableName(targetTableName);
		dbSwitchTableResult.setTableRemarks(sourceTableRemarks);
		dbSwitchTableResult.setSyncTime(new Date());

		// 打印表名与字段名的映射关系
		List<String> columnMapperPairs = new ArrayList<>();
		Map<String, String> mapChecker = new HashMap<>();
		for (int i = 0; i < sourceColumnDescriptions.size(); ++i) {
			String sourceColumnName = sourceColumnDescriptions.get(i).getFieldName();
			String targetColumnName = targetColumnDescriptions.get(i).getFieldName();
			if (StringUtils.hasLength(targetColumnName)) {
				columnMapperPairs.add(String.format("%s --> %s", sourceColumnName, targetColumnName));
				mapChecker.put(sourceColumnName, targetColumnName);
			} else {
				columnMapperPairs.add(String.format(
						"%s --> %s",
						sourceColumnName,
						String.format("<!Field(%s) is Deleted>", (i + 1))
				));
			}
		}
		log.info("Mapping relation : \ntable mapper :\n\t{}  \ncolumn mapper :\n\t{} ",
				tableNameMapString, String.join("\n\t", columnMapperPairs));
		Set<String> valueSet = new HashSet<>(mapChecker.values());
		if (valueSet.size() <= 0) {
			throw new RuntimeException("字段映射配置有误，禁止通过映射将表所有的字段都删除!");
		}
		if (!valueSet.containsAll(this.targetPrimaryKeys)) {
			throw new RuntimeException("字段映射配置有误，禁止通过映射将表的主键字段删除!");
		}
		if (mapChecker.keySet().size() != valueSet.size()) {
			throw new RuntimeException("字段映射配置有误，禁止将多个字段映射到一个同名字段!");
		}

		if (interrupted) {
			throw new RuntimeException("task is interrupted");
		}

		IDatabaseWriter writer = DatabaseWriterFactory.createDatabaseWriter(
				targetDataSource, targetProductType, properties.getTarget().getWriterEngineInsert());

		//如果是mongodb作为源端，设置为删除表，不然同步的时候大概率会报类型转换的错误
		if (ProductTypeEnum.MONGODB.equals(sourceProductType)) {
			properties.getTarget().setTargetDrop(true);
		}
		if (ProductTypeEnum.MONGODB.equals(targetProductType)) {
			//mogodb无需建表
			try {
				DatabaseOperatorFactory.createDatabaseOperator(targetDataSource, targetProductType)
						.dropTable(targetSchemaName, targetTableName);
				log.info("Target Table {}.{} is exits, drop it now !", targetSchemaName, targetTableName);
			} catch (Exception e) {
				log.info("Target Table {}.{} is not exits, create it!", targetSchemaName, targetTableName);
			}
			return doFullCoverSynchronize(writer, dbSwitchTableResult);
		} else if (ProductTypeEnum.HIVE.equals(targetProductType)) {
			try {
				DatabaseOperatorFactory.createDatabaseOperator(targetDataSource, targetProductType)
						.dropTable(targetSchemaName, targetTableName);
				log.info("Target Table {}.{} is exits, drop it now !", targetSchemaName, targetTableName);
			} catch (Exception e) {
				log.info("Target Table {}.{} is not exits, create it!", targetSchemaName, targetTableName);
			}
			// 生成建表语句并创建
			List<String> sqlCreateTable = sourceMetaDataService.getDDLCreateTableSQL(
					targetProductType,
					targetColumnDescriptions.stream()
							.filter(column -> StringUtils.hasLength(column.getFieldName()))
							.collect(Collectors.toList()),
					targetPrimaryKeys,
					targetSchemaName,
					targetTableName,
					sourceTableRemarks,
					properties.getTarget().getCreateTableAutoIncrement(),
					getTblProperties()
			);
			JdbcTemplate targetJdbcTemplate = new JdbcTemplate(targetDataSource);
			for (String sql : sqlCreateTable) {
				log.info("Execute SQL: \n{}", sql);
				targetJdbcTemplate.execute(sql);
			}
			return dbSwitchTableResult;
		}

		//zrx 如果不同步已存在的
		if (!properties.getTarget().getSyncExist()) {
			IMetaDataByDatasourceService metaDataByDatasourceServicee = new MetaDataByDataSourceServiceImpl(targetDataSource, targetProductType);
			List<TableDescription> tableDescriptions = metaDataByDatasourceServicee.queryTableList(targetSchemaName);
			if (tableDescriptions.stream().anyMatch(tableDescription -> tableDescription.getTableName().equals(targetTableName))) {
				log.info("syncExist is false,table {}.{} has existed,do not sync,just return!", targetSchemaName, targetTableName);
				dbSwitchTableResult.setSuccessMsg("由于设置了不同步已存在的表，所以未同步表和数据");
				return dbSwitchTableResult;
			}
		}

		if (properties.getTarget().getTargetDrop()) {
      /*
        如果配置了dbswitch.target.datasource-target-drop=true时，
        <p>
        先执行drop table语句，然后执行create table语句
       */

			try {
				DatabaseOperatorFactory.createDatabaseOperator(targetDataSource, targetProductType)
						.dropTable(targetSchemaName, targetTableName);
				log.info("Target Table {}.{} is exits, drop it now !", targetSchemaName, targetTableName);
			} catch (Exception e) {
				log.info("Target Table {}.{} is not exits, create it!", targetSchemaName, targetTableName);
			}

			IMetaDataByDatasourceService targetDatasourceservice =
					new MetaDataByDataSourceServiceImpl(targetDataSource, targetProductType);
			// 生成建表语句并创建
			List<ColumnDescription> targetColumns = targetColumnDescriptions.stream()
					.filter(column -> StringUtils.hasLength(column.getFieldName()))
					.collect(Collectors.toList());
			List<String> sqlCreateTable = sourceMetaDataService.getDDLCreateTableSQL(
					targetProductType,
					targetColumns,
					targetPrimaryKeys,
					targetSchemaName,
					targetTableName,
					sourceTableRemarks,
					properties.getTarget().getCreateTableAutoIncrement(),
					getTblProperties()
			);
			//zrx 索引创建语句
			if (properties.getTarget().getIndexCreate()) {
				targetDatasourceservice.createIndexDefinition(targetColumns, targetPrimaryKeys, targetSchemaName, targetTableName, sqlCreateTable);
			}
			JdbcTemplate targetJdbcTemplate = new JdbcTemplate(targetDataSource);
			for (String sql : sqlCreateTable) {
				targetJdbcTemplate.execute(sql);
				log.info("Execute SQL: \n{}", sql);
			}

			// 如果只想创建表，这里直接返回
			if (null != properties.getTarget().getOnlyCreate()
					&& properties.getTarget().getOnlyCreate()) {
				dbSwitchTableResult.setSuccessMsg("由于设置了只创建表，所以未同步数据，已建表");
				return dbSwitchTableResult;
			}

			if (interrupted) {
				throw new RuntimeException("task is interrupted");
			}

			return doFullCoverSynchronize(writer, dbSwitchTableResult);
		} else {
			// 对于只想创建表的情况，不提供后续的变化量数据同步功能
			if (null != properties.getTarget().getOnlyCreate()
					&& properties.getTarget().getOnlyCreate()) {
				dbSwitchTableResult.setSuccessMsg("由于设置了只创建表，所以未同步数据，已建表");
				return dbSwitchTableResult;
			}

			if (interrupted) {
				throw new RuntimeException("task is interrupted");
			}

			IMetaDataByDatasourceService metaDataByDatasourceService =
					new MetaDataByDataSourceServiceImpl(targetDataSource, targetProductType);
			List<String> targetTableNames = metaDataByDatasourceService
					.queryTableList(targetSchemaName)
					.stream().map(TableDescription::getTableName)
					.collect(Collectors.toList());

			if (targetTableNames.stream().noneMatch(name -> name.equalsIgnoreCase(targetTableName))) {
				// 当目标端不存在该表时，则生成建表语句并创建
				List<ColumnDescription> targetColumns = targetColumnDescriptions.stream()
						.filter(column -> StringUtils.hasLength(column.getFieldName()))
						.collect(Collectors.toList());
				List<String> sqlCreateTable = sourceMetaDataService.getDDLCreateTableSQL(
						targetProductType,
						targetColumns,
						targetPrimaryKeys,
						targetSchemaName,
						targetTableName,
						sourceTableRemarks,
						properties.getTarget().getCreateTableAutoIncrement(),
						getTblProperties()
				);

				//zrx 索引创建语句
				if (properties.getTarget().getIndexCreate()) {
					metaDataByDatasourceService.createIndexDefinition(targetColumns, targetPrimaryKeys, targetSchemaName, targetTableName, sqlCreateTable);
				}

				JdbcTemplate targetJdbcTemplate = new JdbcTemplate(targetDataSource);
				for (String sql : sqlCreateTable) {
					targetJdbcTemplate.execute(sql);
					log.info("Execute SQL: \n{}", sql);
				}

				if (interrupted) {
					throw new RuntimeException("task is interrupted");
				}

				return doFullCoverSynchronize(writer, dbSwitchTableResult);
			}

			// 判断是否具备变化量同步的条件：（1）两端表结构一致，且都有一样的主键字段；(2)MySQL使用Innodb引擎；
			if (properties.getTarget().getChangeDataSync()) {
				// 根据主键情况判断同步的方式：增量同步或覆盖同步
				List<String> dbTargetPks = metaDataByDatasourceService.queryTablePrimaryKeys(
						targetSchemaName, targetTableName);

				//添加目标表中不存在的字段
				metaDataByDatasourceService.addNoExistColumnsByTarget(targetSchemaName, targetTableName, targetColumnDescriptions);

				//如果是增量字段同步
				if (ChangeDataSyncType.INCREASE_COLUMN.name().equals(properties.getTarget().getChangeDataSyncType())) {
					return doIncreaseColumnSynchronize(writer, dbSwitchTableResult);
				}

				if (!targetPrimaryKeys.isEmpty() && !dbTargetPks.isEmpty()
						&& targetPrimaryKeys.containsAll(dbTargetPks)
						&& dbTargetPks.containsAll(targetPrimaryKeys)) {
					if (targetProductType == ProductTypeEnum.MYSQL
							&& !DatabaseAwareUtils.isMysqlInnodbStorageEngine(
							targetSchemaName, targetTableName, targetDataSource)) {
						return doFullCoverSynchronize(writer, dbSwitchTableResult);
					} else {
						return doIncreaseSynchronize(writer, dbSwitchTableResult);
					}
				} else {
					return doFullCoverSynchronize(writer, dbSwitchTableResult);
				}
			} else {
				return doFullCoverSynchronize(writer, dbSwitchTableResult);
			}
		}
	}

	/**
	 * 执行覆盖同步
	 *
	 * @param writer 目的端的写入器
	 */
	private DbSwitchTableResult doFullCoverSynchronize(IDatabaseWriter writer, DbSwitchTableResult dbSwitchTableResult) {

		AtomicLong syncBytes = dbSwitchTableResult.getSyncBytes();
		AtomicLong syncCount = dbSwitchTableResult.getSyncCount();

		final int BATCH_SIZE = fetchSize;

		List<String> sourceFields = new ArrayList<>();
		List<String> targetFields = new ArrayList<>();
		for (int i = 0; i < targetColumnDescriptions.size(); ++i) {
			ColumnDescription scd = sourceColumnDescriptions.get(i);
			ColumnDescription tcd = targetColumnDescriptions.get(i);
			if (!StringUtils.isEmpty(tcd.getFieldName())) {
				sourceFields.add(scd.getFieldName());
				targetFields.add(tcd.getFieldName());
			}
		}
		// 准备目的端的数据写入操作
		writer.prepareWrite(targetSchemaName, targetTableName, targetFields);

		// 清空目的端表的数据
		IDatabaseOperator targetOperator = DatabaseOperatorFactory
				.createDatabaseOperator(writer.getDataSource(), targetProductType);
		targetOperator.truncateTableData(targetSchemaName, targetTableName);

		// 查询源端数据并写入目的端
		IDatabaseOperator sourceOperator = DatabaseOperatorFactory
				.createDatabaseOperator(sourceDataSource, sourceProductType);
		sourceOperator.setFetchSize(BATCH_SIZE);

		try (StatementResultSet srs = SourceType.TABLE.getCode().equals(sourceProperties.getSourceType()) ?
				sourceOperator.queryTableData(sourceSchemaName, sourceTableName, sourceFields) :
				sourceOperator.queryTableData(sourceProperties.getSourceSql(), sourceFields);
			 ResultSet rs = srs.getResultset()) {
			List<Object[]> cache = new LinkedList<>();
			long cacheBytes = 0;
			/*long totalCount = 0;
			long totalBytes = 0;*/
			while (rs.next()) {
				Object[] record = new Object[sourceFields.size()];
				for (int i = 1; i <= sourceFields.size(); ++i) {
					try {
						record[i - 1] = rs.getObject(i);
					} catch (Exception e) {
						log.warn("!!! Read data from table [ {} ] use function ResultSet.getObject() error",
								tableNameMapString, e);
						record[i - 1] = null;
					}
				}

				cache.add(record);
				long bytes = SizeOf.newInstance().sizeOf(record);
				cacheBytes += bytes;
				syncCount.getAndAdd(1);

				if (cache.size() >= BATCH_SIZE || cacheBytes >= MAX_CACHE_BYTES_SIZE) {
					long ret = writer.write(targetFields, cache);
					log.info("[FullCoverSync] handle table [{}] data count: {}, the batch bytes size: {}",
							tableNameMapString, ret, BytesUnitUtils.bytesSizeToHuman(cacheBytes));
					cache.clear();
					/*totalBytes += cacheBytes;*/
					syncBytes.getAndAdd(cacheBytes);
					cacheBytes = 0;
				}
			}

			if (cache.size() > 0) {
				long ret = writer.write(targetFields, cache);
				log.info("[FullCoverSync] handle table [{}] data count: {}, last batch bytes size: {}",
						tableNameMapString, ret, BytesUnitUtils.bytesSizeToHuman(cacheBytes));
				cache.clear();
				/*totalBytes += cacheBytes;*/
				syncBytes.getAndAdd(cacheBytes);
			}

			/*log.info("[FullCoverSync] handle table [{}] total data count:{}, total bytes={}",
					tableNameMapString, totalCount, BytesUnitUtils.bytesSizeToHuman(totalBytes));*/
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//返回表执行结果
		return dbSwitchTableResult;
	}

	/**
	 * 变化量同步
	 *
	 * @param writer 目的端的写入器
	 */
	private DbSwitchTableResult doIncreaseSynchronize(IDatabaseWriter writer, DbSwitchTableResult dbSwitchTableResult) {
		final int BATCH_SIZE = fetchSize;

		AtomicLong syncCount = dbSwitchTableResult.getSyncCount();
		AtomicLong syncBytes = dbSwitchTableResult.getSyncBytes();

		List<String> sourceFields = new ArrayList<>();
		List<String> targetFields = new ArrayList<>();
		Map<String, String> columnNameMaps = new HashMap<>();
		for (int i = 0; i < targetColumnDescriptions.size(); ++i) {
			ColumnDescription scd = sourceColumnDescriptions.get(i);
			ColumnDescription tcd = targetColumnDescriptions.get(i);
			if (!StringUtils.isEmpty(tcd.getFieldName())) {
				sourceFields.add(scd.getFieldName());
				targetFields.add(tcd.getFieldName());
				columnNameMaps.put(scd.getFieldName(), tcd.getFieldName());
			}
		}

		TaskParamEntity.TaskParamEntityBuilder taskBuilder = TaskParamEntity.builder();
		//target相当于老数据，source相当于要同步的新数据
		taskBuilder.oldProductType(targetProductType);
		taskBuilder.oldDataSource(writer.getDataSource());
		taskBuilder.oldSchemaName(targetSchemaName);
		taskBuilder.oldTableName(targetTableName);
		taskBuilder.newProductType(sourceProductType);
		taskBuilder.newDataSource(sourceDataSource);
		taskBuilder.newSchemaName(sourceSchemaName);
		taskBuilder.newTableName(sourceTableName);
		taskBuilder.sourceType(sourceProperties.getSourceType());
		taskBuilder.sourceSql(sourceProperties.getSourceSql());
		taskBuilder.sourcePrimaryKeys(sourcePrimaryKeys);
		taskBuilder.fieldColumns(sourceFields);
		taskBuilder.columnsMap(columnNameMaps);

		TaskParamEntity param = taskBuilder.build();

		IDatabaseSynchronize synchronizer = DatabaseSynchronizeFactory
				.createDatabaseWriter(writer.getDataSource(), targetProductType);
		synchronizer.prepare(targetSchemaName, targetTableName, targetFields, targetPrimaryKeys);

		IDatabaseChangeCaculator calculator = new ChangeCalculatorService();
		calculator.setFetchSize(fetchSize);
		calculator.setRecordIdentical(false);
		calculator.setCheckJdbcType(false);

		// 执行实际的变化同步过程
		calculator.executeCalculate(param, new IDatabaseRowHandler() {

			private long countInsert = 0;
			private long countUpdate = 0;
			private long countDelete = 0;
			private long countTotal = 0;
			private long cacheBytes = 0;
			private final List<Object[]> cacheInsert = new LinkedList<>();
			private final List<Object[]> cacheUpdate = new LinkedList<>();
			private final List<Object[]> cacheDelete = new LinkedList<>();

			@Override
			public void handle(List<String> fields, Object[] record, RecordChangeTypeEnum flag) {
				if (flag == RecordChangeTypeEnum.VALUE_INSERT) {
					cacheInsert.add(record);
					countInsert++;
				} else if (flag == RecordChangeTypeEnum.VALUE_CHANGED) {
					cacheUpdate.add(record);
					countUpdate++;
				} else {
					cacheDelete.add(record);
					countDelete++;
				}

				long bytes = SizeOf.newInstance().sizeOf(record);
				cacheBytes += bytes;
				syncBytes.getAndAdd(bytes);
				countTotal++;
				syncCount.getAndAdd(1);
				checkFull(fields);
			}

			/**
			 * 检测缓存是否已满，如果已满执行同步操作
			 *
			 * @param fields 同步的字段列表
			 */
			private void checkFull(List<String> fields) {
				if (cacheInsert.size() >= BATCH_SIZE || cacheUpdate.size() >= BATCH_SIZE
						|| cacheDelete.size() >= BATCH_SIZE || cacheBytes >= MAX_CACHE_BYTES_SIZE) {
					if (cacheDelete.size() > 0) {
						doDelete(fields);
					}

					if (cacheInsert.size() > 0) {
						doInsert(fields);
					}

					if (cacheUpdate.size() > 0) {
						doUpdate(fields);
					}

					log.info("[IncreaseSync] Handle table [{}] data one batch size: {}",
							tableNameMapString, BytesUnitUtils.bytesSizeToHuman(cacheBytes));
					cacheBytes = 0;
				}
			}

			@Override
			public void destroy(List<String> fields) {
				if (cacheDelete.size() > 0) {
					doDelete(fields);
				}

				if (cacheInsert.size() > 0) {
					doInsert(fields);
				}

				if (cacheUpdate.size() > 0) {
					doUpdate(fields);
				}

				log.info("[IncreaseSync] Handle table [{}] total count: {}, Insert:{},Update:{},Delete:{} ",
						tableNameMapString, countTotal, countInsert, countUpdate, countDelete);
			}

			private void doInsert(List<String> fields) {
				long ret = synchronizer.executeInsert(cacheInsert);
				log.info("[IncreaseSync] Handle table [{}] data Insert count: {}", tableNameMapString, ret);
				cacheInsert.clear();
			}

			private void doUpdate(List<String> fields) {
				long ret = synchronizer.executeUpdate(cacheUpdate);
				log.info("[IncreaseSync] Handle table [{}] data Update count: {}", tableNameMapString, ret);
				cacheUpdate.clear();
			}

			private void doDelete(List<String> fields) {
				long ret = synchronizer.executeDelete(cacheDelete);
				log.info("[IncreaseSync] Handle table [{}] data Delete count: {}", tableNameMapString, ret);
				cacheDelete.clear();
			}

		});

		return dbSwitchTableResult;
	}

	private DbSwitchTableResult doIncreaseColumnSynchronize(IDatabaseWriter writer, DbSwitchTableResult dbSwitchTableResult) {
		final int BATCH_SIZE = fetchSize;

		AtomicLong syncCount = dbSwitchTableResult.getSyncCount();
		AtomicLong syncBytes = dbSwitchTableResult.getSyncBytes();

		//字段映射
		List<String> sourceFields = new ArrayList<>();
		List<String> targetFields = new ArrayList<>();
		Map<String, String> columnNameMaps = new HashMap<>();
		for (int i = 0; i < targetColumnDescriptions.size(); ++i) {
			ColumnDescription scd = sourceColumnDescriptions.get(i);
			ColumnDescription tcd = targetColumnDescriptions.get(i);
			if (!StringUtils.isEmpty(tcd.getFieldName())) {
				sourceFields.add(scd.getFieldName());
				targetFields.add(tcd.getFieldName());
				columnNameMaps.put(scd.getFieldName(), tcd.getFieldName());
			}
		}

		TaskIncreaseParamEntity.TaskIncreaseParamEntityBuilder increaseTaskBuilder = TaskIncreaseParamEntity.builder();
		increaseTaskBuilder.sourceProductType(sourceProductType);
		increaseTaskBuilder.sourceDataSource(sourceDataSource);
		increaseTaskBuilder.sourceSchemaName(sourceSchemaName);
		increaseTaskBuilder.sourceTableName(sourceTableName);
		increaseTaskBuilder.targetProductType(targetProductType);
		increaseTaskBuilder.targetDataSource(targetDataSource);
		increaseTaskBuilder.targetSchemaName(targetSchemaName);
		increaseTaskBuilder.targetTableName(targetTableName);
		increaseTaskBuilder.sourceType(sourceProperties.getSourceType());
		increaseTaskBuilder.sourceSql(sourceProperties.getSourceSql());
		increaseTaskBuilder.sourcePrimaryKeys(sourcePrimaryKeys);
		increaseTaskBuilder.fieldColumns(sourceFields);
		increaseTaskBuilder.columnsMap(columnNameMaps);
		increaseTaskBuilder.increaseColumn(increaseColumn);
		increaseTaskBuilder.startVal(sourceProperties.getConfigMap().get(sourceTableName).getIncreaseStartVal());
		increaseTaskBuilder.endVal(sourceProperties.getConfigMap().get(sourceTableName).getIncreaseEndVal());

		TaskIncreaseParamEntity taskIncreaseParam = increaseTaskBuilder.build();

		IDatabaseSynchronize synchronizer = DatabaseSynchronizeFactory
				.createDatabaseWriter(writer.getDataSource(), targetProductType);
		synchronizer.prepareIncrease(targetSchemaName, targetTableName, targetFields, targetPrimaryKeys);

		IDatabaseChangeCaculator calculator = new ChangeCalculatorService();
		calculator.setFetchSize(fetchSize);
		calculator.setRecordIdentical(false);
		calculator.setCheckJdbcType(false);

		// 执行实际的变化同步过程
		calculator.executeCalculate(taskIncreaseParam, new IDatabaseRowHandler() {

			private long countInsert = 0;
			private long countUpdate = 0;
			private long countDelete = 0;
			private long countTotal = 0;
			private long cacheBytes = 0;
			private final List<Object[]> cacheInsert = new LinkedList<>();
			private final List<Object[]> cacheUpdate = new LinkedList<>();
			private final List<Object[]> cacheDelete = new LinkedList<>();

			@Override
			public void handle(List<String> fields, Object[] record, RecordChangeTypeEnum flag) {
				if (flag == RecordChangeTypeEnum.VALUE_INSERT) {
					cacheInsert.add(record);
					countInsert++;
				} else if (flag == RecordChangeTypeEnum.VALUE_CHANGED) {
					cacheUpdate.add(record);
					countUpdate++;
				} else {
					cacheDelete.add(record);
					countDelete++;
				}

				long bytes = SizeOf.newInstance().sizeOf(record);
				cacheBytes += bytes;
				syncBytes.getAndAdd(bytes);
				countTotal++;
				syncCount.getAndAdd(1);
				checkFull(fields);
			}

			/**
			 * 检测缓存是否已满，如果已满执行同步操作
			 *
			 * @param fields 同步的字段列表
			 */
			private void checkFull(List<String> fields) {
				if (cacheInsert.size() >= BATCH_SIZE || cacheUpdate.size() >= BATCH_SIZE
						|| cacheDelete.size() >= BATCH_SIZE || cacheBytes >= MAX_CACHE_BYTES_SIZE) {
					if (cacheDelete.size() > 0) {
						doDelete(fields);
					}

					if (cacheInsert.size() > 0) {
						doInsert(fields);
					}

					if (cacheUpdate.size() > 0) {
						doUpdate(fields);
					}

					log.info("[IncreaseColumnSync] Handle table [{}] data one batch size: {}",
							tableNameMapString, BytesUnitUtils.bytesSizeToHuman(cacheBytes));
					cacheBytes = 0;
				}
			}

			@Override
			public void destroy(List<String> fields) {
				if (cacheDelete.size() > 0) {
					doDelete(fields);
				}

				if (cacheInsert.size() > 0) {
					doInsert(fields);
				}

				if (cacheUpdate.size() > 0) {
					doUpdate(fields);
				}

				log.info("[IncreaseColumnSync] Handle table [{}] total count: {}, Insert:{},Update:{},Delete:{} ",
						tableNameMapString, countTotal, countInsert, countUpdate, countDelete);
			}

			private void doInsert(List<String> fields) {
				long ret = synchronizer.executeInsert(cacheInsert);
				log.info("[IncreaseColumnSync] Handle table [{}] data Insert count: {}", tableNameMapString, ret);
				cacheInsert.clear();
			}

			private void doUpdate(List<String> fields) {
				long ret = synchronizer.executeUpdate(cacheUpdate);
				log.info("[IncreaseColumnSync] Handle table [{}] data Update count: {}", tableNameMapString, ret);
				cacheUpdate.clear();
			}

			private void doDelete(List<String> fields) {
				long ret = synchronizer.executeDelete(cacheDelete);
				log.info("[IncreaseColumnSync] Handle table [{}] data Delete count: {}", tableNameMapString, ret);
				cacheDelete.clear();
			}

		});

		return dbSwitchTableResult;
	}

	/**
	 * https://cwiki.apache.org/confluence/display/Hive/JDBC+Storage+Handler
	 *
	 * @return Map<String, String>
	 */
	public Map<String, String> getTblProperties() {
		Map<String, String> ret = new HashMap<>();
		if (ProductTypeEnum.HIVE.equals(targetProductType)) {
			// hive.sql.database.type: MYSQL, POSTGRES, ORACLE, DERBY, DB2
			final List<ProductTypeEnum> supportedProductTypes =
					Arrays.asList(ProductTypeEnum.MYSQL, ProductTypeEnum.ORACLE,
							ProductTypeEnum.DB2, ProductTypeEnum.POSTGRESQL);
			ExamineUtils.check(supportedProductTypes.contains(sourceProductType),
					"Unsupported data from %s to Hive", sourceProductType.name());

			String querySql;
			if (SourceType.TABLE.getCode().equals(sourceProperties.getSourceType())) {
				String fullTableName = sourceProductType.quoteSchemaTableName(sourceSchemaName, sourceTableName);
				List<String> columnNames = sourceColumnDescriptions.stream().map(ColumnDescription::getFieldName)
						.collect(Collectors.toList());
				querySql = String.format("SELECT %s FROM %s",
						columnNames.stream()
								.map(s -> sourceProductType.quoteName(s))
								.collect(Collectors.joining(",")),
						fullTableName);
			} else {
				querySql = sourceProperties.getSourceSql();
			}
			String databaseType = sourceProductType.name().toUpperCase();
			if (ProductTypeEnum.POSTGRESQL == sourceProductType) {
				databaseType = "POSTGRES";
			} else if (ProductTypeEnum.SQLSERVER == sourceProductType) {
				databaseType = "MSSQL";
			}
			ret.put("hive.sql.database.type", databaseType);
			ret.put("hive.sql.jdbc.driver", sourceProductType.getDriveClassName());
			ret.put("hive.sql.jdbc.url", sourceDataSource.getJdbcUrl());
			ret.put("hive.sql.dbcp.username", sourceDataSource.getUsername());
			ret.put("hive.sql.dbcp.password", sourceDataSource.getPassword());
			ret.put("hive.sql.query", querySql);
			ret.put("hive.sql.jdbc.read-write", "read");
			ret.put("hive.sql.jdbc.fetch.size", "2000");
			ret.put("hive.sql.dbcp.maxActive", "1");
		}
		return ret;
	}
}
