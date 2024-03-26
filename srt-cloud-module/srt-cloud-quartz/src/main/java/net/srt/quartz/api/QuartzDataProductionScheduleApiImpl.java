package net.srt.quartz.api;

import lombok.RequiredArgsConstructor;
import net.srt.api.module.data.development.DataProductionScheduleApi;
import net.srt.api.module.data.development.dto.DataProductionScheduleDto;
import net.srt.api.module.quartz.QuartzDataProductionScheduleApi;
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
public class QuartzDataProductionScheduleApiImpl implements QuartzDataProductionScheduleApi {

	private final Scheduler scheduler;
	private final DataProductionScheduleApi scheduleApi;
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
	public Result<String> cancle(Long id) {
		ScheduleJobEntity jobEntity = buildJobEntity(id);
		jobService.buildSystemJob(jobEntity);
		ScheduleUtils.deleteScheduleJob(scheduler, jobEntity);
		//更新任务状态为暂停
		jobService.pauseSystemJob(jobEntity);
		return Result.ok();
	}


	private ScheduleJobEntity buildJobEntity(Long id) {
		DataProductionScheduleDto scheduleDto = scheduleApi.getById(id).getData();
		return ScheduleJobEntity.builder().typeId(id).projectId(scheduleDto.getProjectId()).jobType(QuartzJobType.DATA_PRODUCTION.getValue()).jobName(String.format("[%s]%s", id.toString(), scheduleDto.getName())).concurrent(ScheduleConcurrentEnum.NO.getValue())
				.beanName("dataProductionScheduleTask").method("run").jobGroup(JobGroupEnum.DATA_PRODUCTION.getValue()).saveLog(true).cronExpression(scheduleDto.getCron()).status(ScheduleStatusEnum.NORMAL.getValue())
				.params(String.valueOf(id)).once(scheduleDto.getIfCycle() == 0).build();
	}
}
