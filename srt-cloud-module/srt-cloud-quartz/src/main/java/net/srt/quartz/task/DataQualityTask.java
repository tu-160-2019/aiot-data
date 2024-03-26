package net.srt.quartz.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.srt.api.module.data.governance.DataMetadataApi;
import net.srt.api.module.data.governance.DataQualityApi;
import net.srt.api.module.data.governance.constant.DbType;
import net.srt.api.module.data.governance.dto.DataGovernanceMetadataDto;
import net.srt.api.module.data.governance.dto.DataGovernanceQualityConfigDto;
import net.srt.api.module.data.governance.dto.DataGovernanceQualityTaskDto;
import net.srt.api.module.data.governance.dto.quality.QualityCheck;
import net.srt.api.module.data.governance.dto.quality.QulaityColumn;
import net.srt.api.module.data.integrate.DataDatabaseApi;
import net.srt.api.module.data.integrate.constant.CommonRunStatus;
import net.srt.api.module.data.integrate.dto.DataDatabaseDto;
import net.srt.flink.common.utils.LogUtil;
import net.srt.framework.common.cache.bean.DataProjectCacheBean;
import net.srt.framework.common.utils.DateUtils;
import net.srt.framework.security.cache.TokenStoreCache;
import net.srt.quartz.task.quality.AbstractQualityAdapter;
import net.srt.quartz.task.quality.QualityFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName DataProductionScheduleTask
 * @Author zrx
 * @Date 2023/1/19 14:39
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataQualityTask {

	private final DataQualityApi dataQualityApi;
	private final DataMetadataApi dataMetadataApi;
	private final DataDatabaseApi dataDatabaseApi;
	private final TokenStoreCache tokenStoreCache;

	public void run(String id, Thread currentThread) {

		log.info("DataQualityTask run start");

		Long configId = Long.parseLong(id);
		DataGovernanceQualityConfigDto configDto = dataQualityApi.getById(configId).getData();
		Long projectId = configDto.getProjectId();
		List<Integer> metadataIds = configDto.getMetadataIds();
		Integer ruleId = configDto.getRuleId();
		List<QualityCheck> qualityChecks = new ArrayList<>();

		//添加质量检测任务
		DataGovernanceQualityTaskDto qualityTaskDto = new DataGovernanceQualityTaskDto();
		try {
			qualityTaskDto.setCreator(configDto.getCreator());
			qualityTaskDto.setOrgId(configDto.getOrgId());
			qualityTaskDto.setQualityConfigId(configId);
			qualityTaskDto.setName(configDto.getName() + " - " + DateUtils.formatDateTime(new Date()));
			qualityTaskDto.setStatus(CommonRunStatus.RUNNING.getCode());
			qualityTaskDto.setStartTime(new Date());
			qualityTaskDto.setProjectId(configDto.getProjectId());
			qualityTaskDto = dataQualityApi.addQualityTask(qualityTaskDto).getData();

			for (Integer metadataId : metadataIds) {
				DataGovernanceMetadataDto columnMeta = dataMetadataApi.getById(metadataId).getData();
				DataGovernanceMetadataDto tableMeta = dataMetadataApi.getById(columnMeta.getParentId().intValue()).getData();
				QualityCheck qualityCheck = new QualityCheck();
				qualityCheck.setRuleId(ruleId);
				qualityCheck.setParam(configDto.getParam());
				qualityCheck.setTableMetadataId(tableMeta.getId());
				qualityCheck.setTableName(tableMeta.getCode());
				QulaityColumn qulaityColumn = new QulaityColumn(columnMeta.getId().intValue(), columnMeta.getCode());
				if (DbType.MIDDLE_DB.getValue().equals(tableMeta.getDbType())) {
					DataProjectCacheBean project = tokenStoreCache.getProject(projectId);
					qualityCheck.setDatabaseName(project.getDbName());
					qualityCheck.setDatabaseSchema(project.getDbSchema());
					qualityCheck.setJdbcUrl(project.getDbUrl());
					qualityCheck.setUserName(project.getDbUsername());
					qualityCheck.setPassword(project.getDbPassword());
					qualityCheck.setDatabaseType(project.getDbType());
				} else {
					DataDatabaseDto database = dataDatabaseApi.getById(tableMeta.getDatasourceId()).getData();
					qualityCheck.setDatabaseName(database.getDatabaseName());
					qualityCheck.setDatabaseSchema(database.getDatabaseSchema());
					qualityCheck.setJdbcUrl(database.getJdbcUrl());
					qualityCheck.setUserName(database.getUserName());
					qualityCheck.setPassword(database.getPassword());
					qualityCheck.setDatabaseType(database.getDatabaseType());
				}
				if (qualityChecks.contains(qualityCheck)) {
					qualityCheck = qualityChecks.get(qualityChecks.indexOf(qualityCheck));
					qualityCheck.getQulaityColumns().add(qulaityColumn);
				} else {
					qualityChecks.add(qualityCheck);
					qualityCheck.getQulaityColumns().add(qulaityColumn);
				}
			}

			//每张表依次质量检测
			for (QualityCheck qualityCheck : qualityChecks) {
				if (currentThread.isInterrupted()) {
					log.error("DataQualityTask run interrupted");
					qualityTaskDto.setEndTime(new Date());
					qualityTaskDto.setStatus(CommonRunStatus.FAILED.getCode());
					qualityTaskDto.setErrorLog("DataQualityTask run interrupted");
					dataQualityApi.updateQualityTask(qualityTaskDto);
					return;
				}
				//获取质量检测的适配器
				AbstractQualityAdapter qualityAdapter = QualityFactory.createQualityAdapter(qualityCheck);
				qualityAdapter.prepare(dataQualityApi, qualityTaskDto);
				qualityAdapter.check();
			}

			qualityTaskDto.setEndTime(new Date());
			qualityTaskDto.setStatus(CommonRunStatus.SUCCESS.getCode());
			dataQualityApi.updateQualityTask(qualityTaskDto);
			log.info("DataQualityTask run end");

		} catch (Exception e) {
			log.error("DataQualityTask run error", e);
			qualityTaskDto.setEndTime(new Date());
			qualityTaskDto.setStatus(CommonRunStatus.FAILED.getCode());
			qualityTaskDto.setErrorLog(LogUtil.getError(e));
			dataQualityApi.updateQualityTask(qualityTaskDto);
		}

	}

}
