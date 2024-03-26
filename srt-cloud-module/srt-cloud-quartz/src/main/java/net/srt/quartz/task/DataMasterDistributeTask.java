package net.srt.quartz.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.srt.api.module.data.governance.DataMasterApi;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterDistributeDto;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterDistributeLogDto;
import net.srt.api.module.data.integrate.DataDatabaseApi;
import net.srt.api.module.data.integrate.constant.CommonRunStatus;
import net.srt.flink.common.utils.LogUtil;
import net.srt.framework.security.cache.TokenStoreCache;
import net.srt.quartz.task.master.AbstractMasterAdapter;
import net.srt.quartz.task.master.MasterDistributeFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @ClassName DataProductionScheduleTask
 * @Author zrx
 * @Date 2023/1/19 14:39
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataMasterDistributeTask {

	private final DataDatabaseApi databaseApi;
	private final DataMasterApi dataMasterApi;
	private final TokenStoreCache tokenStoreCache;

	public void run(String id, Thread currentThread) {
		log.info("DataMasterDistributeTask run start");
		Long disId = Long.parseLong(id);
		//获取派发相关配置
		DataGovernanceMasterDistributeDto distributeDto = dataMasterApi.getDistributeById(disId).getData();
		//添加日志
		DataGovernanceMasterDistributeLogDto distributeLogDto = new DataGovernanceMasterDistributeLogDto();
		distributeLogDto.setDistributeId(disId);
		distributeLogDto.setCreator(distributeDto.getCreator());
		distributeLogDto.setOrgId(distributeDto.getOrgId());
		distributeLogDto.setProjectId(distributeDto.getProjectId());
		distributeLogDto.setMasterModelId(distributeDto.getMasterModelId());
		distributeLogDto.setRunStatus(CommonRunStatus.RUNNING.getCode());
		distributeLogDto.setStartTime(new Date());
		distributeLogDto = dataMasterApi.addDistributeLog(distributeLogDto).getData();
		try {
			AbstractMasterAdapter masterAdapter = MasterDistributeFactory.createMasterAdapter(distributeDto);
			masterAdapter.setProject(tokenStoreCache.getProject(distributeDto.getProjectId()));
			masterAdapter.prepare(dataMasterApi, databaseApi, distributeLogDto, currentThread);
			masterAdapter.distribute();
		} catch (Exception e) {
			distributeLogDto.setRunStatus(CommonRunStatus.FAILED.getCode());
			distributeLogDto.setEndTime(new Date());
			distributeLogDto.setErrorInfo(LogUtil.getError(e));
			dataMasterApi.upDistributeLog(distributeLogDto);
		}
	}

}
