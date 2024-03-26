package net.srt.quartz.api;

import lombok.RequiredArgsConstructor;
import net.srt.api.module.data.governance.DataMasterApi;
import net.srt.api.module.data.governance.constant.DistributeTaskType;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterDistributeDto;
import net.srt.api.module.quartz.QuartzDataGovernanceMasterApi;
import net.srt.api.module.quartz.constant.QuartzJobType;
import net.srt.framework.common.utils.Result;
import net.srt.quartz.entity.ScheduleJobEntity;
import net.srt.quartz.enums.JobGroupEnum;
import net.srt.quartz.enums.ScheduleConcurrentEnum;
import net.srt.quartz.enums.ScheduleStatusEnum;
import net.srt.quartz.service.ScheduleJobService;
import net.srt.quartz.utils.ScheduleUtils;
import org.quartz.Scheduler;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短信服务API
 *
 * @author 阿沐 babamu@126.com
 */
@RestController
@RequiredArgsConstructor
public class QuartzDataGovernanceMasterApiImpl implements QuartzDataGovernanceMasterApi {

	private final Scheduler scheduler;
	private final DataMasterApi dataMasterApi;
	private final ScheduleJobService jobService;

	@Override
	public Result<String> release(Long id) {
		ScheduleJobEntity jobEntity = buildJobEntity(id);
		//判断是否存在,不存在，新增，存在，设置主键
		jobService.buildSystemJob(jobEntity);
		ScheduleUtils.createScheduleJob(scheduler, jobEntity);
		return Result.ok();
	}

	@Override
	public Result<String> cancel(Long id) {
		ScheduleJobEntity jobEntity = buildJobEntity(id);
		jobService.buildSystemJob(jobEntity);
		ScheduleUtils.deleteScheduleJob(scheduler, jobEntity);
		//更新任务状态为暂停
		jobService.pauseSystemJob(jobEntity);
		return Result.ok();
	}

	@Override
	public Result<String> handRun(Long id) {
		ScheduleJobEntity jobEntity = buildJobEntity(id);
		jobEntity.setOnce(true);
		jobEntity.setSaveLog(false);
		ScheduleUtils.run(scheduler, jobEntity);
		return Result.ok();
	}

	private ScheduleJobEntity buildJobEntity(Long id) {
		DataGovernanceMasterDistributeDto distributeDto = dataMasterApi.getDistributeById(id).getData();
		return ScheduleJobEntity.builder().typeId(id).projectId(distributeDto.getProjectId()).jobType(QuartzJobType.DATA_MASTER.getValue()).jobName(String.format("[%s]%s", id.toString(), distributeDto.getName())).concurrent(ScheduleConcurrentEnum.NO.getValue())
				.beanName("dataMasterDistributeTask").method("run").jobGroup(JobGroupEnum.DATA_MASTER.getValue()).saveLog(true).cronExpression(distributeDto.getCron()).status(ScheduleStatusEnum.NORMAL.getValue())
				.params(String.valueOf(id)).once(DistributeTaskType.ONCE.getValue().equals(distributeDto.getTaskType())).build();

	}
}
