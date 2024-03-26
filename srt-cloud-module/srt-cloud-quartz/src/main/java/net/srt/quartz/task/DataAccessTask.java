package net.srt.quartz.task;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.srt.api.module.data.integrate.DataAccessApi;
import net.srt.api.module.data.integrate.DataDatabaseApi;
import net.srt.api.module.data.integrate.DataProjectApi;
import net.srt.api.module.data.integrate.DataTableApi;
import net.srt.api.module.data.integrate.constant.AccessMode;
import net.srt.api.module.data.integrate.constant.CommonRunStatus;
import net.srt.api.module.data.integrate.constant.DataHouseLayer;
import net.srt.api.module.data.integrate.constant.TaskType;
import net.srt.api.module.data.integrate.dto.DataAccessDto;
import net.srt.api.module.data.integrate.dto.DataAccessIncreaseLogDto;
import net.srt.api.module.data.integrate.dto.DataAccessRunDto;
import net.srt.api.module.data.integrate.dto.DataAccessTaskDto;
import net.srt.api.module.data.integrate.dto.DataDatabaseDto;
import net.srt.api.module.data.integrate.dto.DataTableDto;
import net.srt.api.module.data.integrate.dto.PreviewNameMapperDto;
import net.srt.api.module.data.integrate.dto.QueryIncreaseLog;
import net.srt.flink.common.utils.LogUtil;
import net.srt.flink.common.utils.ThreadUtil;
import net.srt.framework.common.cache.bean.DataProjectCacheBean;
import net.srt.framework.common.utils.DateUtils;
import net.srt.framework.security.cache.TokenStoreCache;
import net.srt.lineage.constant.RelationType;
import net.srt.lineage.node.Column;
import net.srt.lineage.node.Database;
import net.srt.lineage.node.Table;
import net.srt.lineage.relation.ColumnRelation;
import net.srt.lineage.relation.DatabaseRelation;
import net.srt.lineage.relation.DatabaseTableRelation;
import net.srt.lineage.relation.TableColumnRelation;
import net.srt.lineage.relation.TableRelation;
import net.srt.lineage.repository.ColumnRelationRepository;
import net.srt.lineage.repository.ColumnRepository;
import net.srt.lineage.repository.DatabaseRelationRepository;
import net.srt.lineage.repository.DatabaseRepository;
import net.srt.lineage.repository.DatabaseTableRelationRepository;
import net.srt.lineage.repository.TableColumnRelationRepository;
import net.srt.lineage.repository.TableRelationRepository;
import net.srt.lineage.repository.TableRepository;
import net.srt.quartz.utils.CronUtils;
import org.springframework.stereotype.Component;
import srt.cloud.framework.dbswitch.common.constant.ChangeDataSyncType;
import srt.cloud.framework.dbswitch.common.constant.MapperType;
import srt.cloud.framework.dbswitch.common.entity.MapperConfig;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.SingletonObject;
import srt.cloud.framework.dbswitch.common.util.StringUtil;
import srt.cloud.framework.dbswitch.core.model.TableDescription;
import srt.cloud.framework.dbswitch.core.service.IMetaDataByDatasourceService;
import srt.cloud.framework.dbswitch.core.service.IMetaDataByJdbcService;
import srt.cloud.framework.dbswitch.core.service.impl.MetaDataByDataSourceServiceImpl;
import srt.cloud.framework.dbswitch.core.service.impl.MetaDataByJdbcServiceImpl;
import srt.cloud.framework.dbswitch.data.config.DbswichProperties;
import srt.cloud.framework.dbswitch.data.domain.DbSwitchResult;
import srt.cloud.framework.dbswitch.data.entity.SourceDataSourceProperties;
import srt.cloud.framework.dbswitch.data.entity.TargetDataSourceProperties;
import srt.cloud.framework.dbswitch.data.service.MigrationService;
import srt.cloud.framework.dbswitch.data.util.BytesUnitUtils;
import srt.cloud.framework.dbswitch.data.util.DataSourceUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @ClassName DataAccessTask
 * @Author zrx
 * @Date 2022/10/26 13:12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataAccessTask {

	private final DataDatabaseApi dataDatabaseApi;
	private final DataAccessApi dataAccessApi;
	private final DataTableApi dataTableApi;
	private final DatabaseRepository databaseRepository;
	private final DatabaseRelationRepository databaseRelationRepository;
	private final TableRepository tableRepository;
	private final DatabaseTableRelationRepository databaseTableRelationRepository;
	private final TableRelationRepository tableRelationRepository;
	private final ColumnRepository columnRepository;
	private final TableColumnRelationRepository tableColumnRelationRepository;
	private final ColumnRelationRepository columnRelationRepository;

	private final DataProjectApi dataProjectApi;
	private final TokenStoreCache tokenStoreCache;

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

	private static final DateTimeFormatter TIMESTAMP_FORMAT_NEW = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@SneakyThrows
	public void run(String runDto, Thread currentThread) {
		DataAccessRunDto accessRunDto = SingletonObject.OBJECT_MAPPER.readValue(runDto, DataAccessRunDto.class);
		log.info("Start run data-access");
		DataAccessDto dataAccessDto = dataAccessApi.getById(accessRunDto.getDataAccessId()).getData();
		DbswichProperties dbswichProperties = dataAccessDto.getDataAccessJson();
		SourceDataSourceProperties sourceDataSourceProperties = dbswichProperties.getSource().get(0);
		TargetDataSourceProperties targetDataSourceProperties = dbswichProperties.getTarget();
		//如果是增量字段同步
		Map<String, DataAccessIncreaseLogDto> increaseLogDtoMap = buildIncrease(dataAccessDto, sourceDataSourceProperties, targetDataSourceProperties);
		DataAccessTaskDto dataAccessTaskDto = new DataAccessTaskDto();
		dataAccessTaskDto.setRunType(accessRunDto.getRunType());
		dataAccessTaskDto.setExecuteNo(accessRunDto.getExecuteNo());
		dataAccessTaskDto.setCreator(dataAccessDto.getCreator());
		dataAccessTaskDto.setOrgId(dataAccessDto.getOrgId());
		dataAccessTaskDto.setProjectId(dataAccessDto.getProjectId());
		dataAccessTaskDto.setDataAccessId(accessRunDto.getDataAccessId());
		dataAccessTaskDto.setRunStatus(CommonRunStatus.RUNNING.getCode());
		dataAccessTaskDto.setStartTime(new Date());
		dataAccessTaskDto.setId(dataAccessApi.addTask(dataAccessTaskDto).getData());
		dataAccessTaskDto.setUpdateTaskAccess(false);
		StringBuffer realTimeLog = new StringBuffer();
		MigrationService service = buildMigrationService(dataAccessDto, dbswichProperties, dataAccessTaskDto, realTimeLog, increaseLogDtoMap);
		DbSwitchResult dbSwitchResult;
		AtomicBoolean runEnd = new AtomicBoolean(false);
		try {
			//监控任务是否被停止
			new Thread(() -> {
				while (!currentThread.isInterrupted() && !runEnd.get()) {
					ThreadUtil.sleep(5000);
				}
				if (currentThread.isInterrupted()) {
					service.interrupt();
				}
			}).start();
			realTimeLog.append(DateUtils.formatDateTime(new Date())).append(" ").append("Start to run data access task\r\n");
			updateRealTimeLog(dataAccessTaskDto, realTimeLog);
			//执行任务
			dbSwitchResult = service.run();
			runEnd.set(true);
		} catch (Exception e) {
			e.printStackTrace();
			runEnd.set(true);
			realTimeLog.append(LogUtil.getError(e));
			realTimeLog.append(DateUtils.formatDateTime(new Date())).append(" ").append("Data access task end failed\r\n");
			dataAccessTaskDto.setRealTimeLog(realTimeLog.toString());
			dataAccessTaskDto.setEndTime(new Date());
			dataAccessTaskDto.setRunStatus(CommonRunStatus.FAILED.getCode());
			dataAccessTaskDto.setNextRunTime(TaskType.ONE_TIME_FULL_PERIODIC_INCR_SYNC.getCode().equals(dataAccessDto.getTaskType()) ? CronUtils.getNextExecution(dataAccessDto.getCron()) : null);
			dataAccessTaskDto.setErrorInfo(LogUtil.getError(e));
			dataAccessTaskDto.setUpdateTaskAccess(true);
			dataAccessApi.updateTask(dataAccessTaskDto);
			return;
		}
		dataAccessTaskDto.setEndTime(new Date());
		dataAccessTaskDto.setDataCount(dbSwitchResult.getTotalRowCount().get());
		dataAccessTaskDto.setTableSuccessCount(dbSwitchResult.getTotalTableSuccessCount().get());
		dataAccessTaskDto.setTableFailCount(dbSwitchResult.getTotalTableFailCount().get());
		dataAccessTaskDto.setByteCount(BytesUnitUtils.bytesSizeToHuman(dbSwitchResult.getTotalBytes().get()));
		dataAccessTaskDto.setRunStatus(dbSwitchResult.getIfAllSuccess().get() ? CommonRunStatus.SUCCESS.getCode() : CommonRunStatus.FAILED.getCode());
		dataAccessTaskDto.setErrorInfo(dbSwitchResult.getIfAllSuccess().get() ? null : String.format("有%s张表同步失败，可以在同步结果查看！", dataAccessTaskDto.getTableFailCount()));
		dataAccessTaskDto.setNextRunTime(TaskType.ONE_TIME_FULL_PERIODIC_INCR_SYNC.getCode().equals(dataAccessDto.getTaskType()) ? CronUtils.getNextExecution(dataAccessDto.getCron()) : null);
		realTimeLog.append(DateUtils.formatDateTime(new Date())).append(" ").append("Data access task end ").append(dbSwitchResult.getIfAllSuccess().get() ? "succeed" : "failed").append("\r\n");
		realTimeLog.append(DateUtils.formatDateTime(new Date())).append(" ").append(String.format("Data count:[%s],tableSuccessCount:[%s],tableFailCount:[%s],byteCount:[%s]",
				dataAccessTaskDto.getDataCount(), dataAccessTaskDto.getTableSuccessCount(), dataAccessTaskDto.getTableFailCount(), dataAccessTaskDto.getByteCount()))
				.append("\r\n");
		dataAccessTaskDto.setRealTimeLog(realTimeLog.toString());
		//更新最终的同步记录
		dataAccessTaskDto.setUpdateTaskAccess(true);
		dataAccessApi.updateTask(dataAccessTaskDto);
		log.info("Run data-access success");
		//构建库级血缘
		buildLineage(dataAccessDto);
	}

	private Map<String, DataAccessIncreaseLogDto> buildIncrease(DataAccessDto dataAccessDto, SourceDataSourceProperties sourceDataSourceProperties, TargetDataSourceProperties targetDataSourceProperties) {
		Map<String, DataAccessIncreaseLogDto> increaseLogMap = new HashMap<>();
		if (ChangeDataSyncType.INCREASE_COLUMN.name().equals(targetDataSourceProperties.getChangeDataSyncType())) {
			try (HikariDataSource sourceDataSource = DataSourceUtils.createSourceDataSource(sourceDataSourceProperties)) {
				ProductTypeEnum sourceProductType = sourceDataSourceProperties.getSourceProductType();
				IMetaDataByJdbcService jdbcService = new MetaDataByJdbcServiceImpl(sourceProductType);
				List<String> tableList;
				//如果是sql
				if (sourceDataSourceProperties.getSourceType() == 2) {
					tableList = new ArrayList<>();
					tableList.add(sourceDataSourceProperties.getSourceSql());
				} else {
					IMetaDataByDatasourceService sourceMetaDataService = new MetaDataByDataSourceServiceImpl(sourceDataSource, sourceProductType);
					tableList = sourceMetaDataService.queryTableList(sourceDataSourceProperties.getSourceSchema()).stream().map(TableDescription::getTableName).collect(Collectors.toList());
					//包含
					if (sourceDataSourceProperties.getIncludeOrExclude() == 1) {
						tableList = StringUtil.isNotBlank(sourceDataSourceProperties.getSourceIncludes()) ? Arrays.asList(sourceDataSourceProperties.getSourceIncludes().split(",")) : tableList;
					} else {
						String sourceExcludes = sourceDataSourceProperties.getSourceExcludes();
						List<String> excludes = new ArrayList<>();
						if (StringUtil.isNotBlank(sourceExcludes)) {
							excludes = Arrays.asList(sourceExcludes.split(","));
						}
						List<String> finalExcludes = excludes;
						tableList = tableList.stream().filter(item -> !finalExcludes.contains(item)).collect(Collectors.toList());
					}
				}
				Map<String, MapperConfig> configMap = sourceDataSourceProperties.getConfigMap();
				configMap = configMap == null ? new HashMap<>() : configMap;
				sourceDataSourceProperties.setConfigMap(configMap);
				for (String tableName : tableList) {
					//获取字段目前的最大值
					if (MapperType.ALL.name().equals(sourceDataSourceProperties.getMapperType())) {
						MapperConfig mapperConfig = new MapperConfig();
						increaseLogMap.put(tableName, buildConfigMap(dataAccessDto, sourceDataSourceProperties, jdbcService, configMap, tableName, mapperConfig, sourceDataSourceProperties.getIncreaseColumnName()));
					} else if (MapperType.SINGLE.name().equals(sourceDataSourceProperties.getMapperType())) {
						MapperConfig mapperConfig = configMap.get(tableName);
						if (mapperConfig != null) {
							increaseLogMap.put(tableName, buildConfigMap(dataAccessDto, sourceDataSourceProperties, jdbcService, configMap, tableName, mapperConfig, mapperConfig.getIncreaseColumnName()));
						}
					}
				}
			}
		}
		return increaseLogMap;
	}

	private DataAccessIncreaseLogDto buildConfigMap(DataAccessDto dataAccessDto, SourceDataSourceProperties sourceDataSourceProperties, IMetaDataByJdbcService jdbcService, Map<String, MapperConfig> configMap, String tableName, MapperConfig mapperConfig, String increaseColumnName) {
		QueryIncreaseLog queryIncreaseLog = new QueryIncreaseLog(dataAccessDto.getId(), dataAccessDto.getSourceDatabaseId(), dataAccessDto.getTargetDatabaseId(), sourceDataSourceProperties.getSourceSchema(), tableName, increaseColumnName);
		//获取该表最新的增量记录
		DataAccessIncreaseLogDto increaseLogDto = dataAccessApi.getNewestLog(queryIncreaseLog);
		Object maxVal;
		if (sourceDataSourceProperties.getSourceType() == 1) {
			maxVal = jdbcService.queryMaxVal(sourceDataSourceProperties.getUrl(), sourceDataSourceProperties.getUsername(), sourceDataSourceProperties.getPassword(), sourceDataSourceProperties.getSourceSchema(), tableName, increaseColumnName);
		} else {
			maxVal = jdbcService.queryMaxVal(sourceDataSourceProperties.getUrl(), sourceDataSourceProperties.getUsername(), sourceDataSourceProperties.getPassword(), sourceDataSourceProperties.getSourceSql(), increaseColumnName);
		}
		//获取字符串的值
		String endVal = getDataStr(maxVal);
		if (endVal == null) {
			return null;
		}
		if (increaseLogDto != null) {
			increaseLogDto.setId(null);
			increaseLogDto.setStartVal(increaseLogDto.getEndVal());
			increaseLogDto.setEndVal(endVal);
			//dataAccessApi.addIncreaseLog(increaseLogDto);
		} else {
			//没有记录，新增
			increaseLogDto = new DataAccessIncreaseLogDto();
			increaseLogDto.setDataAccessId(dataAccessDto.getId());
			increaseLogDto.setSourceDatabaseId(dataAccessDto.getSourceDatabaseId());
			increaseLogDto.setTargetDatabaseId(dataAccessDto.getTargetDatabaseId());
			increaseLogDto.setSchemaName(sourceDataSourceProperties.getSourceSchema());
			increaseLogDto.setTableName(tableName);
			increaseLogDto.setIncreaseColumn(increaseColumnName);
			increaseLogDto.setEndVal(endVal);
			increaseLogDto.setAccessMode(dataAccessDto.getAccessMode());
			//dataAccessApi.addIncreaseLog(increaseLogDto);
		}
		//保存每个表的起始值
		mapperConfig.setIncreaseStartVal(increaseLogDto.getStartVal());
		mapperConfig.setIncreaseEndVal(increaseLogDto.getEndVal());
		configMap.put(tableName, mapperConfig);
		return increaseLogDto;
	}

	private String getDataStr(Object o) {
		if (o == null) {
			return null;
		}
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
	 * 构建血缘
	 *
	 * @param dataAccessDto
	 */
	private void buildLineage(DataAccessDto dataAccessDto) {
		try {
			DataDatabaseDto sourceDatabaseDto = dataDatabaseApi.getById(dataAccessDto.getSourceDatabaseId()).getData();
			DataDatabaseDto targetDataDatabaseDto;
			if (dataAccessDto.getTargetDatabaseId() == null) {
				targetDataDatabaseDto = new DataDatabaseDto();
				DataProjectCacheBean project = dataProjectApi.getById(dataAccessDto.getProjectId()).getData();
				targetDataDatabaseDto.setId(-1L);
				targetDataDatabaseDto.setName(project.getName() + "<中台库>");
				targetDataDatabaseDto.setDatabaseName(project.getDbName());
				targetDataDatabaseDto.setDatabaseSchema(project.getDbSchema());
				targetDataDatabaseDto.setUserName(project.getDbUsername());
				targetDataDatabaseDto.setPassword(project.getDbPassword());
				targetDataDatabaseDto.setJdbcUrl(project.getDbUrl());
			} else {
				targetDataDatabaseDto = dataDatabaseApi.getById(dataAccessDto.getTargetDatabaseId()).getData();
			}
			//数据库节点
			Database sourceDatabase = addOrUpdateDatabase(sourceDatabaseDto);
			Database targetDatabase = addOrUpdateDatabase(targetDataDatabaseDto);
			//数据库关联关系
			addOrUpdateDatabaseRelation(dataAccessDto, sourceDatabase, targetDatabase);
			//构建表级血缘
			List<PreviewNameMapperDto> tableMap = dataAccessApi.getTableMap(dataAccessDto.getId()).getData();
			for (PreviewNameMapperDto tableMapper : tableMap) {
				Table sourceTable = new Table(tableMapper.getRemarks(), tableMapper.getOriginalName(), sourceDatabaseDto.getId(), sourceDatabase.getId());
				Table targetTable = new Table(tableMapper.getRemarks(), tableMapper.getTargetName(), targetDataDatabaseDto.getId(), targetDatabase.getId());
				//表节点
				sourceTable = addOrUpdateTable(sourceTable);
				targetTable = addOrUpdateTable(targetTable);
				//database<-table
				addOrUpdateDatabaseTableRelation(sourceDatabase, sourceTable);
				addOrUpdateDatabaseTableRelation(targetDatabase, targetTable);
				//table->table
				addOrUpdateTableRelation(dataAccessDto, sourceTable, targetTable);
				//构建字段级血缘
				List<PreviewNameMapperDto> columnMap = dataAccessApi.getColumnMap(dataAccessDto.getId(), sourceTable.getCode()).getData();
				for (PreviewNameMapperDto columnMapper : columnMap) {
					Column sourceColumn = new Column(columnMapper.getRemarks(), columnMapper.getOriginalName(), sourceDatabaseDto.getId(), sourceTable.getId());
					Column targetColumn = new Column(columnMapper.getRemarks(), columnMapper.getTargetName(), targetDataDatabaseDto.getId(), targetTable.getId());
					//字段节点
					sourceColumn = addOrUpdateColumn(sourceColumn);
					targetColumn = addOrUpdateColumn(targetColumn);
					//column<-table
					addOrUpdateTableColumnRelation(sourceTable, sourceColumn);
					addOrUpdateTableColumnRelation(targetTable, targetColumn);
					//column->column
					addOrUpdateColumnRelation(dataAccessDto, sourceColumn, targetColumn);
				}
			}
		} catch (Exception e) {
			log.error("Data access data-lineage build error:", e);
		}
	}

	private void addOrUpdateColumnRelation(DataAccessDto dataAccessDto, Column sourceColumn, Column targetColumn) {
		ColumnRelation dbColumnRelation = columnRelationRepository.getBySourceAndTargetId(sourceColumn.getId(), targetColumn.getId());
		ColumnRelation columnRelation = new ColumnRelation(targetColumn, RelationType.COLUMN_TO_COLUMN.getValue(), dataAccessDto.getId(), dataAccessDto.getTaskName());
		if (dbColumnRelation != null) {
			columnRelation.setId(dbColumnRelation.getRelId());
			columnRelationRepository.update(columnRelation);
		} else {
			columnRelationRepository.create(sourceColumn.getId(), targetColumn.getId(), columnRelation);
		}
	}

	private void addOrUpdateTableColumnRelation(Table table, Column column) {
		TableColumnRelation dbTableColumnRelation = tableColumnRelationRepository.getBySourceAndTargetId(column.getId(), table.getId());
		if (dbTableColumnRelation == null) {
			tableColumnRelationRepository.create(column.getId(), table.getId(), RelationType.TABLE_CONTAIN_COLUMN.getValue());
		}
	}

	private Column addOrUpdateColumn(Column column) {
		Column dbColumn = columnRepository.get(column.getDatabaseId(), column.getCode(), column.getParentId());
		if (dbColumn != null) {
			column.setId(dbColumn.getId());
			columnRepository.update(column);
			return column;
		} else {
			return columnRepository.save(column);
		}
	}

	private void addOrUpdateTableRelation(DataAccessDto dataAccessDto, Table sourceTable, Table targetTable) {
		TableRelation dbTableRelation = tableRelationRepository.getBySourceAndTargetId(sourceTable.getId(), targetTable.getId());
		TableRelation tableRelation = new TableRelation(targetTable, RelationType.TABLE_TO_TABLE.getValue(), dataAccessDto.getId(), dataAccessDto.getTaskName());
		if (dbTableRelation != null) {
			tableRelation.setId(dbTableRelation.getRelId());
			tableRelationRepository.update(tableRelation);
		} else {
			tableRelationRepository.create(sourceTable.getId(), targetTable.getId(), tableRelation);
		}
	}

	private void addOrUpdateDatabaseTableRelation(Database database, Table table) {
		DatabaseTableRelation dbDatabaseTableRelation = databaseTableRelationRepository.getBySourceAndTargetId(table.getId(), database.getId());
		if (dbDatabaseTableRelation == null) {
			databaseTableRelationRepository.create(table.getId(), database.getId(), RelationType.DATABASE_CONTAIN_TABLE.getValue());
		}
	}

	private Table addOrUpdateTable(Table table) {
		Table dbTable = tableRepository.get(table.getDatabaseId(), table.getCode(), table.getParentId());
		if (dbTable != null) {
			table.setId(dbTable.getId());
			tableRepository.update(table);
			return table;
		} else {
			return tableRepository.save(table);
		}
	}

	private void addOrUpdateDatabaseRelation(DataAccessDto dataAccessDto, Database sourceDatabase, Database targetDatabase) {
		DatabaseRelation dbDatabaseRelation = databaseRelationRepository.getBySourceAndTargetId(sourceDatabase.getId(), targetDatabase.getId());
		DatabaseRelation databaseRelation = new DatabaseRelation(targetDatabase,
				RelationType.DATABASE_TO_DATABASE.getValue(), dataAccessDto.getId(), dataAccessDto.getTaskName());
		if (dbDatabaseRelation != null) {
			databaseRelation.setId(dbDatabaseRelation.getRelId());
			databaseRelationRepository.update(databaseRelation);
		} else {
			databaseRelationRepository.create(sourceDatabase.getId(), targetDatabase.getId(), databaseRelation);
		}
	}

	private Database addOrUpdateDatabase(DataDatabaseDto dataDatabaseDto) {
		Database database = new Database(dataDatabaseDto.getName(), dataDatabaseDto.getDatabaseName(), dataDatabaseDto.getJdbcUrl(), dataDatabaseDto.getUserName(), dataDatabaseDto.getPassword(), dataDatabaseDto.getId());
		Database dbDatabase = databaseRepository.getByDatabaseId(dataDatabaseDto.getId());
		if (dbDatabase != null) {
			database.setId(dbDatabase.getId());
			databaseRepository.update(database);
			return database;
		} else {
			return databaseRepository.save(database);
		}
	}

	/**
	 * 构建同步服务
	 *
	 * @param dataAccessDto
	 * @param dbswichProperties
	 * @param dataAccessTaskDto
	 * @return
	 */
	private MigrationService buildMigrationService(DataAccessDto dataAccessDto, DbswichProperties dbswichProperties, DataAccessTaskDto dataAccessTaskDto, StringBuffer realTimeLog, Map<String, DataAccessIncreaseLogDto> increaseLogDtoMap) {
		AtomicLong totalRowCount = new AtomicLong(0);
		AtomicLong totalBytes = new AtomicLong(0);
		AtomicLong totalTableSuccessCount = new AtomicLong(0);
		AtomicLong totalTableFailCount = new AtomicLong(0);
		//构建同步参数
		return new MigrationService(dbswichProperties, tableResult -> {
			//添加每张表的同步结果
			dataAccessApi.addTaskDetail(dataAccessTaskDto.getProjectId(), dataAccessTaskDto.getOrgId(), dataAccessTaskDto.getCreator(), dataAccessTaskDto.getId(), dataAccessTaskDto.getDataAccessId(), tableResult);
			//如果是ods接入，添加 dataOds
			if (AccessMode.ODS.getValue().equals(dataAccessDto.getAccessMode())) {
				boolean addOds = true;
				if (!tableResult.getIfSuccess().get()) {
					DataProjectCacheBean project = tokenStoreCache.getProject(dataAccessDto.getProjectId());
					//如果表没创建成功，return
					IMetaDataByJdbcService jdbcService = new MetaDataByJdbcServiceImpl(ProductTypeEnum.getByIndex(project.getDbType()));
					if (!jdbcService.tableExist(dbswichProperties.getTarget().getUrl(), dbswichProperties.getTarget().getUsername(), dbswichProperties.getTarget().getPassword(), tableResult.getTargetTableName())) {
						addOds = false;
					}
				}
				if (addOds) {
					DataTableDto dataTableDto = new DataTableDto();
					dataTableDto.setLayer(DataHouseLayer.ODS.name());
					dataTableDto.setIfMaster(0);
					dataTableDto.setDataAccessId(dataAccessTaskDto.getDataAccessId());
					dataTableDto.setTableName(tableResult.getTargetTableName());
					dataTableDto.setCreator(dataAccessDto.getCreator());
					dataTableDto.setProjectId(dataAccessDto.getProjectId());
					dataTableDto.setRemarks(tableResult.getTableRemarks());
					dataTableDto.setRecentlySyncTime(tableResult.getSyncTime());
					dataTableApi.addOds(dataTableDto);
				}
			}
			totalRowCount.getAndAdd(tableResult.getSyncCount().get());
			totalBytes.getAndAdd(tableResult.getSyncBytes().get());
			if (!tableResult.getIfSuccess().get()) {
				totalTableFailCount.getAndIncrement();
			} else {
				totalTableSuccessCount.getAndIncrement();
				//添加同步记录
				DataAccessIncreaseLogDto increaseLogDto = increaseLogDtoMap.get(tableResult.getSourceTableName());
				if (increaseLogDto != null) {
					dataAccessApi.addIncreaseLog(increaseLogDto);
				}
			}
			//同步更新总的同步结果
			dataAccessTaskDto.setDataCount(totalRowCount.get());
			dataAccessTaskDto.setTableSuccessCount(totalTableSuccessCount.get());
			dataAccessTaskDto.setTableFailCount(totalTableFailCount.get());
			dataAccessTaskDto.setByteCount(BytesUnitUtils.bytesSizeToHuman(totalBytes.get()));
			if (tableResult.getIfSuccess().get()) {
				realTimeLog.append(DateUtils.formatDateTime(new Date())).append(" ")
						.append(String.format("Sync table [%s]->[%s] success ==> syncCount:[%s],syncBytes:[%s],syncMsg:[%s]",
								tableResult.getSourceTableName(), tableResult.getTargetTableName(), tableResult.getSyncCount(), BytesUnitUtils.bytesSizeToHuman(tableResult.getSyncBytes().get()), tableResult.getSuccessMsg()))
						.append("\r\n");
			} else {
				realTimeLog.append(DateUtils.formatDateTime(new Date())).append(" ")
						.append(String.format("Sync table [%s]->[%s] failed ==> [%s]",
								tableResult.getSourceTableName(), tableResult.getTargetTableName(), tableResult.getErrorMsg())).append("\r\n");
				;
			}
			dataAccessTaskDto.setRealTimeLog(realTimeLog.toString());
			dataAccessApi.updateTask(dataAccessTaskDto);
		});
	}

	/**
	 * 更新日志
	 *
	 * @param realTimeLog
	 */
	private void updateRealTimeLog(DataAccessTaskDto dataAccessTaskDto, StringBuffer realTimeLog) {
		dataAccessTaskDto.setRealTimeLog(realTimeLog.toString());
		dataAccessApi.updateTask(dataAccessTaskDto);
	}
}


