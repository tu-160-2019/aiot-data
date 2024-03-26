package net.srt.quartz.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.srt.api.module.data.governance.DataMetadataCollectApi;
import net.srt.api.module.data.governance.constant.BuiltInMetamodel;
import net.srt.api.module.data.governance.constant.BuiltInMetamodelProperty;
import net.srt.api.module.data.governance.constant.DbType;
import net.srt.api.module.data.governance.constant.MetadataCollectRunStatus;
import net.srt.api.module.data.governance.constant.MetadataStrategyType;
import net.srt.api.module.data.governance.dto.DataGovernanceMetadataCollectDto;
import net.srt.api.module.data.governance.dto.DataGovernanceMetadataCollectRecordDto;
import net.srt.api.module.data.governance.dto.DataGovernanceMetadataDto;
import net.srt.api.module.data.governance.dto.DataGovernanceMetadataPropertyDto;
import net.srt.api.module.data.integrate.DataDatabaseApi;
import net.srt.api.module.data.integrate.DataProjectApi;
import net.srt.api.module.data.integrate.dto.DataDatabaseDto;
import net.srt.flink.common.utils.LogUtil;
import net.srt.framework.common.cache.bean.DataProjectCacheBean;
import net.srt.framework.common.utils.DateUtils;
import org.springframework.stereotype.Component;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.StringUtil;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.TableDescription;
import srt.cloud.framework.dbswitch.core.service.IMetaDataByJdbcService;
import srt.cloud.framework.dbswitch.core.service.impl.MetaDataByJdbcServiceImpl;

import java.util.Date;
import java.util.List;

/**
 * @ClassName DataGovernanceMetadataCollectTask
 * @Author zrx
 * @Date 2023/04/-2 13:12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataGovernanceMetadataCollectTask {

	private final DataMetadataCollectApi dataMetadataCollectApi;
	private final DataDatabaseApi dataDatabaseApi;
	private final DataProjectApi dataProjectApi;

	public void run(String metadataCollectId, Thread currentThread) {
		log.info("metadata collect task start to run...");
		Long collectTaskId = Long.parseLong(metadataCollectId);
		DataGovernanceMetadataCollectDto metadataCollectDto = dataMetadataCollectApi.getById(collectTaskId).getData();
		if (metadataCollectDto == null) {
			log.error("metadata collect task not found...");
			return;
		}
		Long projectId = metadataCollectDto.getProjectId();
		Long orgId = metadataCollectDto.getOrgId();
		Long creator = metadataCollectDto.getCreator();
		//生成同步任务
		DataGovernanceMetadataCollectRecordDto collectRecord = new DataGovernanceMetadataCollectRecordDto();
		collectRecord.setOrgId(orgId);
		collectRecord.setProjectId(projectId);
		collectRecord.setMetadataCollectId(collectTaskId);
		collectRecord.setStatus(MetadataCollectRunStatus.RUNNING.getCode());
		collectRecord.setStartTime(new Date());
		collectRecord.setCreator(creator);
		collectRecord.setCreateTime(new Date());
		collectRecord = dataMetadataCollectApi.addCollectRecord(collectRecord);
		StringBuilder realTimeLog = new StringBuilder();
		try {
			realTimeLog.append(DateUtils.formatDateTime(new Date())).append(" ").append("Start to collect database info...\r\n");
			Integer dbType = metadataCollectDto.getDbType();
			//如果是数据库
			DataDatabaseDto databaseDto;
			if (DbType.DATABASE.getValue().equals(dbType)) {
				databaseDto = dataDatabaseApi.getById(metadataCollectDto.getDatabaseId()).getData();
				if (databaseDto == null) {
					realTimeLog.append(DateUtils.formatDateTime(new Date())).append(" ").append("Database not exists or get database info failed,get metadata info failed\r\n");
					collectRecord.setRealTimeLog(realTimeLog.toString());
					collectRecord.setErrorLog(DateUtils.formatDateTime(new Date()) + " Database not exists or get database info failed,get metadata info failed\r\n");
					collectRecord.setEndTime(new Date());
					collectRecord.setStatus(MetadataCollectRunStatus.FAILED.getCode());
					dataMetadataCollectApi.upCollectRecord(collectRecord);
					return;
				}
			} else {
				//获取中台库信息
				databaseDto = new DataDatabaseDto();
				DataProjectCacheBean project = dataProjectApi.getById(projectId).getData();
				databaseDto.setId(-1L);
				databaseDto.setDatabaseIp(project.getDbIp());
				databaseDto.setDatabasePort(project.getDbPort());
				databaseDto.setName(project.getName() + "<中台库>");
				databaseDto.setDatabaseSchema(project.getDbSchema());
				databaseDto.setDatabaseName(project.getDbName());
				databaseDto.setJdbcUrl(project.getDbUrl());
				databaseDto.setUserName(project.getDbUsername());
				databaseDto.setPassword(project.getDbPassword());
				databaseDto.setDatabaseType(project.getDbType());
			}
			DataGovernanceMetadataDto rootMetadata = dataMetadataCollectApi.getMetadataById(metadataCollectDto.getMetadataId()).getData();
			//获取入库策略
			Integer strategy = metadataCollectDto.getStrategy();
			//创建库元数据信息
			DataGovernanceMetadataDto databaseMetadata = new DataGovernanceMetadataDto();
			databaseMetadata.setCreator(creator);
			databaseMetadata.setOrgId(orgId);
			databaseMetadata.setProjectId(projectId);
			databaseMetadata.setCollectTaskId(collectTaskId);
			databaseMetadata.setParentId(metadataCollectDto.getMetadataId());
			databaseMetadata.setName(databaseDto.getName());
			databaseMetadata.setPath(rootMetadata.getPath() + "/" + databaseMetadata.getName());
			databaseMetadata.setCode(databaseDto.getDatabaseName());
			//设置为库类型
			databaseMetadata.setMetamodelId(BuiltInMetamodel.SCHEMA.getId());
			databaseMetadata.setIcon(BuiltInMetamodel.SCHEMA.getIcon());
			databaseMetadata.setIfLeaf(0);
			databaseMetadata.setDbType(dbType);
			databaseMetadata.setDatasourceId(databaseDto.getId());
			//判断是否有该库的元数据
			DataGovernanceMetadataDto dbDatabaseMetadata = dataMetadataCollectApi.getByParentIdAndDatasourceId(databaseMetadata.getParentId(), databaseMetadata.getDatasourceId()).getData();
			if (dbDatabaseMetadata != null) {
				databaseMetadata.setId(dbDatabaseMetadata.getId());
			}
			databaseMetadata = dataMetadataCollectApi.addOrUpdateMetadata(databaseMetadata);
			//添加属性
			addOrUpdateProperty(databaseMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.SCHEMA_COMMENT, projectId, databaseMetadata.getId(), databaseDto.getName()));
			addOrUpdateProperty(databaseMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.SCHEMA_TYPE, projectId, databaseMetadata.getId(), ProductTypeEnum.getByIndex(databaseDto.getDatabaseType()).name()));
			addOrUpdateProperty(databaseMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.SCHEMA_IP, projectId, databaseMetadata.getId(), databaseDto.getDatabaseIp()));
			addOrUpdateProperty(databaseMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.SCHEMA_PORT, projectId, databaseMetadata.getId(), databaseDto.getDatabasePort()));
			addOrUpdateProperty(databaseMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.SCHEMA_DATABASE, projectId, databaseMetadata.getId(), databaseDto.getDatabaseName()));
			addOrUpdateProperty(databaseMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.SCHEMA_USERNAME, projectId, databaseMetadata.getId(), databaseDto.getUserName()));
			addOrUpdateProperty(databaseMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.SCHEMA_PASSWORD, projectId, databaseMetadata.getId(), databaseDto.getPassword()));
			addOrUpdateProperty(databaseMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.SCHEMA_JDBC_URL, projectId, databaseMetadata.getId(), databaseDto.getJdbcUrl()));
			realTimeLog.append(DateUtils.formatDateTime(new Date())).append(" ").append("Start to collect tables info...\r\n");
			updateRealTimeLog(collectRecord, realTimeLog);
			//查询表元数据信息
			ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(databaseDto.getDatabaseType());
			IMetaDataByJdbcService metaDataService = new MetaDataByJdbcServiceImpl(productTypeEnum);

			List<TableDescription> tables = metaDataService.queryTableList(databaseDto.getJdbcUrl(), databaseDto.getUserName(), databaseDto.getPassword(),
					databaseDto.getDatabaseSchema());

			if (currentThread.isInterrupted()) {
				interrupt(collectRecord, realTimeLog);
				return;
			}
			List<DataGovernanceMetadataDto> dbTables = dataMetadataCollectApi.listParentIdAndDatasourceId(databaseMetadata.getId(), databaseDto.getId(), BuiltInMetamodel.TABLE.getId()).getData();
			//如果是全量，查询数据库中已有的数据
			if (MetadataStrategyType.ALL.getValue().equals(strategy)) {
				//如果库里有，tables中没有，删除
				for (DataGovernanceMetadataDto dbTable : dbTables) {
					if (tables.stream().noneMatch(item -> item.getTableName().equals(dbTable.getCode()))) {
						dataMetadataCollectApi.deleteMetadata(dbTable.getId());
					}
				}
			}
			//采集表的元数据
			for (TableDescription table : tables) {
				if (currentThread.isInterrupted()) {
					interrupt(collectRecord, realTimeLog);
					return;
				}
				realTimeLog.append(DateUtils.formatDateTime(new Date())).append(" ").append(String.format("Start to collect table [%s] info...、\r\n", table.getTableName()));
				updateRealTimeLog(collectRecord, realTimeLog);
				//创建表元数据信息
				DataGovernanceMetadataDto tableMetadata = new DataGovernanceMetadataDto();
				tableMetadata.setCreator(creator);
				tableMetadata.setOrgId(orgId);
				tableMetadata.setProjectId(projectId);
				tableMetadata.setCollectTaskId(collectTaskId);
				tableMetadata.setParentId(databaseMetadata.getId());
				tableMetadata.setName(StringUtil.isNotBlank(table.getRemarks()) ? table.getRemarks() : table.getTableName());
				tableMetadata.setPath(databaseMetadata.getPath() + "/" + tableMetadata.getName());
				tableMetadata.setCode(table.getTableName());
				//设置为表类型
				tableMetadata.setMetamodelId(BuiltInMetamodel.TABLE.getId());
				tableMetadata.setIcon(BuiltInMetamodel.TABLE.getIcon());
				tableMetadata.setIfLeaf(0);
				tableMetadata.setDbType(dbType);
				tableMetadata.setDatasourceId(databaseDto.getId());
				//判断是否有该库的元数据
				DataGovernanceMetadataDto finalTableMetadata = tableMetadata;
				DataGovernanceMetadataDto dbTableMetadata = dbTables.stream().filter(item -> item.getCode().equals(finalTableMetadata.getCode())).findFirst().orElse(null);
				if (dbTableMetadata != null) {
					tableMetadata.setId(dbTableMetadata.getId());
				}
				tableMetadata = dataMetadataCollectApi.addOrUpdateMetadata(tableMetadata);
				//添加属性
				addOrUpdateProperty(tableMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.TABLE_COMMENT, projectId, tableMetadata.getId(), table.getRemarks()));
				addOrUpdateProperty(tableMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.TABLE_SPACE, projectId, tableMetadata.getId(), null));
				//获取表的字段
				List<ColumnDescription> columns = metaDataService.queryTableColumnMeta(databaseDto.getJdbcUrl(), databaseDto.getUserName(), databaseDto.getPassword(), databaseDto.getDatabaseSchema(), table.getTableName());
				List<String> pks = metaDataService.queryTablePrimaryKeys(databaseDto.getJdbcUrl(), databaseDto.getUserName(), databaseDto.getPassword(), databaseDto.getDatabaseSchema(), table.getTableName());

				List<DataGovernanceMetadataDto> dbColumns = dataMetadataCollectApi.listParentIdAndDatasourceId(tableMetadata.getId(), databaseDto.getId(), BuiltInMetamodel.COLUMN.getId()).getData();
				//如果是全量
				if (MetadataStrategyType.ALL.getValue().equals(strategy)) {
					//如果库里有，columns 中没有，删除
					for (DataGovernanceMetadataDto dbColumn : dbColumns) {
						if (columns.stream().noneMatch(item -> item.getFieldName().equals(dbColumn.getCode()))) {
							dataMetadataCollectApi.deleteMetadata(dbColumn.getId());
						}
					}
				}
				for (ColumnDescription column : columns) {
					if (pks.contains(column.getFieldName())) {
						column.setPk(true);
					}
					DataGovernanceMetadataDto columnMetadata = new DataGovernanceMetadataDto();
					columnMetadata.setCreator(creator);
					columnMetadata.setOrgId(orgId);
					columnMetadata.setProjectId(projectId);
					columnMetadata.setCollectTaskId(collectTaskId);
					columnMetadata.setParentId(tableMetadata.getId());
					columnMetadata.setName(StringUtil.isNotBlank(column.getRemarks()) ? column.getRemarks() : column.getFieldName());
					columnMetadata.setPath(tableMetadata.getPath() + "/" + columnMetadata.getName());
					columnMetadata.setCode(column.getFieldName());
					//设置为字段类型
					columnMetadata.setMetamodelId(BuiltInMetamodel.COLUMN.getId());
					columnMetadata.setIcon(BuiltInMetamodel.COLUMN.getIcon());
					columnMetadata.setIfLeaf(0);
					columnMetadata.setDbType(dbType);
					columnMetadata.setDatasourceId(databaseDto.getId());
					//判断是否有该元数据
					DataGovernanceMetadataDto finalColumnMetadata = columnMetadata;
					DataGovernanceMetadataDto dbColumnMetadata = dbColumns.stream().filter(item -> item.getCode().equals(finalColumnMetadata.getCode())).findFirst().orElse(null);
					//DataGovernanceMetadataDto dbColumnMetadata = dataMetadataApi.getByParentIdAndOtherInfo(columnMetadata.getParentId(), columnMetadata.getDatasourceId(), columnMetadata.getCode(), columnMetadata.getMetamodelId()).getData();
					if (dbColumnMetadata != null) {
						columnMetadata.setId(dbColumnMetadata.getId());
					}
					columnMetadata = dataMetadataCollectApi.addOrUpdateMetadata(columnMetadata);
					//添加属性
					addOrUpdateProperty(columnMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.COLUMN_COMMENT, projectId, columnMetadata.getId(), column.getRemarks()));
					addOrUpdateProperty(columnMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.COLUMN_DATA_TYPE, projectId, columnMetadata.getId(), column.getFieldTypeName()));
					addOrUpdateProperty(columnMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.COLUMN_DATA_LENGTH, projectId, columnMetadata.getId(), String.valueOf(column.getDisplaySize())));
					addOrUpdateProperty(columnMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.COLUMN_DATA_PRECISION, projectId, columnMetadata.getId(), String.valueOf(column.getPrecisionSize())));
					addOrUpdateProperty(columnMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.COLUMN_DATA_SCALE, projectId, columnMetadata.getId(), String.valueOf(column.getScaleSize())));
					addOrUpdateProperty(columnMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.COLUMN_COL_KEY, projectId, columnMetadata.getId(), column.isPk() ? "是" : "否"));
					addOrUpdateProperty(columnMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.COLUMN_UNI_KEY, projectId, columnMetadata.getId(), column.isNonIndexUnique() ? "否" : "是"));
					addOrUpdateProperty(columnMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.COLUMN_NULLABLE, projectId, columnMetadata.getId(), column.isNullable() ? "是" : "否"));
					addOrUpdateProperty(columnMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.COLUMN_AUTO_INCREMENT, projectId, columnMetadata.getId(), column.isAutoIncrement() ? "是" : "否"));
					addOrUpdateProperty(columnMetadata, BuiltInMetamodelProperty.buildProerty(BuiltInMetamodelProperty.COLUMN_DATA_DEFAULT, projectId, columnMetadata.getId(), column.getDefaultValue()));
				}
				realTimeLog.append(DateUtils.formatDateTime(new Date())).append(" ").append(String.format("Collect table [%s] info succeed\r\n", table.getTableName()));
				updateRealTimeLog(collectRecord, realTimeLog);
			}
			realTimeLog.append(DateUtils.formatDateTime(new Date())).append(" ").append("Collect database info succeed\r\n");
			realTimeLog.append(DateUtils.formatDateTime(new Date())).append(" ").append("All metadata collect succeed\r\n");
			collectRecord.setEndTime(new Date());
			collectRecord.setStatus(MetadataCollectRunStatus.SUCCESS.getCode());
			collectRecord.setRealTimeLog(realTimeLog.toString());
			dataMetadataCollectApi.upCollectRecord(collectRecord);
			log.info("metadata collect task success end");
		} catch (Exception e) {
			realTimeLog.append(LogUtil.getError(e));
			collectRecord.setStatus(MetadataCollectRunStatus.FAILED.getCode());
			collectRecord.setRealTimeLog(realTimeLog.toString());
			collectRecord.setErrorLog(LogUtil.getError(e));
			collectRecord.setEndTime(new Date());
			dataMetadataCollectApi.upCollectRecord(collectRecord);
			log.info("metadata collect task failed end");
		}
	}

	private void interrupt(DataGovernanceMetadataCollectRecordDto collectRecord, StringBuilder realTimeLog) {
		collectRecord.setStatus(MetadataCollectRunStatus.FAILED.getCode());
		realTimeLog.append(DateUtils.formatDateTime(new Date())).append(" ").append("The collect task has been canceled when is running\r\n");
		collectRecord.setRealTimeLog(realTimeLog.toString());
		collectRecord.setErrorLog(DateUtils.formatDateTime(new Date()) + " The collect task has been canceled when is running");
		collectRecord.setEndTime(new Date());
		dataMetadataCollectApi.upCollectRecord(collectRecord);
	}

	/**
	 * 添加或删除属性
	 */
	private void addOrUpdateProperty(DataGovernanceMetadataDto metadataDto, DataGovernanceMetadataPropertyDto metadataPropertyDto) {
		DataGovernanceMetadataPropertyDto dbProperty = dataMetadataCollectApi.getByPropertyIdAndMetadataId(metadataPropertyDto.getMetamodelPropertyId(), metadataDto.getId()).getData();
		if (dbProperty != null) {
			metadataPropertyDto.setId(dbProperty.getId());
		}
		dataMetadataCollectApi.addOrUpdateMetadataProperty(metadataPropertyDto);
	}

	/**
	 * 更新日志
	 *
	 * @param collectRecord
	 * @param realTimeLog
	 */
	private void updateRealTimeLog(DataGovernanceMetadataCollectRecordDto collectRecord, StringBuilder realTimeLog) {
		collectRecord.setRealTimeLog(realTimeLog.toString());
		dataMetadataCollectApi.upCollectRecord(collectRecord);
	}


}


