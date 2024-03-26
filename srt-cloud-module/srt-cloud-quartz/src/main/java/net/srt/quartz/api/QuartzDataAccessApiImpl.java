package net.srt.quartz.api;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.srt.api.module.data.integrate.DataAccessApi;
import net.srt.api.module.data.integrate.constant.AccessRunType;
import net.srt.api.module.data.integrate.constant.TaskType;
import net.srt.api.module.data.integrate.dto.DataAccessDto;
import net.srt.api.module.data.integrate.dto.DataAccessRunDto;
import net.srt.api.module.quartz.QuartzDataAccessApi;
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
import srt.cloud.framework.dbswitch.common.util.SingletonObject;
import srt.cloud.framework.dbswitch.common.util.UuidUtils;

/**
 * 短信服务API
 *
 * @author 阿沐 babamu@126.com
 */
@RestController
@RequiredArgsConstructor
public class QuartzDataAccessApiImpl implements QuartzDataAccessApi {

	private final Scheduler scheduler;
	private final DataAccessApi dataAccessApi;
	private final ScheduleJobService jobService;

	@Override
	public Result<String> releaseAccess(Long id) {
		DataAccessRunDto dataAccessRunDto = new DataAccessRunDto();
		dataAccessRunDto.setDataAccessId(id);
		dataAccessRunDto.setRunType(AccessRunType.AUTO.getCode());
		ScheduleJobEntity jobEntity = buildJobEntity(dataAccessRunDto);
		//判断是否存在,不存在，新增，存在，设置主键
		jobService.buildSystemJob(jobEntity);
		ScheduleUtils.createScheduleJob(scheduler, jobEntity);
		return Result.ok();
	}

	@Override
	public Result<String> cancleAccess(Long id) {
		DataAccessRunDto dataAccessRunDto = new DataAccessRunDto();
		dataAccessRunDto.setDataAccessId(id);
		dataAccessRunDto.setRunType(AccessRunType.AUTO.getCode());
		ScheduleJobEntity jobEntity = buildJobEntity(dataAccessRunDto);
		jobService.buildSystemJob(jobEntity);
		ScheduleUtils.deleteScheduleJob(scheduler, jobEntity);
		//更新任务状态为暂停
		jobService.pauseSystemJob(jobEntity);
		return Result.ok();
	}

	@Override
	public Result<String> handRun(Long id) {
		DataAccessRunDto dataAccessRunDto = new DataAccessRunDto();
		dataAccessRunDto.setDataAccessId(id);
		dataAccessRunDto.setRunType(AccessRunType.HAND.getCode());
		dataAccessRunDto.setExecuteNo(UuidUtils.generateUuid());
		ScheduleJobEntity jobEntity = buildJobEntity(dataAccessRunDto);
		jobEntity.setOnce(true);
		jobEntity.setSaveLog(false);
		ScheduleUtils.run(scheduler, jobEntity);
		return Result.ok();
	}

	@Override
	public Result<String> stopHandTask(String executeNo) {
		ScheduleJobEntity jobEntity = new ScheduleJobEntity();
		jobEntity.setExecuteNo(executeNo);
		jobEntity.setRunType(AccessRunType.HAND.getCode());
		jobEntity.setJobGroup(JobGroupEnum.DATA_ACCESS.getValue());
		ScheduleUtils.deleteScheduleJob(scheduler, jobEntity);
		return Result.ok();
	}


	@SneakyThrows
	private ScheduleJobEntity buildJobEntity(DataAccessRunDto dataAccessRunDto) {
		Long id = dataAccessRunDto.getDataAccessId();
		DataAccessDto dataAccessDto = dataAccessApi.getById(id).getData();
		return ScheduleJobEntity.builder().typeId(id).projectId(dataAccessDto.getProjectId()).jobType(QuartzJobType.DATA_ACCESS.getValue()).jobName(String.format("[%s]%s", id.toString(), dataAccessDto.getTaskName())).concurrent(ScheduleConcurrentEnum.NO.getValue())
				.beanName("dataAccessTask").method("run").jobGroup(JobGroupEnum.DATA_ACCESS.getValue()).saveLog(true).cronExpression(dataAccessDto.getCron()).status(ScheduleStatusEnum.NORMAL.getValue())
				.params(SingletonObject.OBJECT_MAPPER.writeValueAsString(dataAccessRunDto)).once(TaskType.ONE_TIME_FULL_SYNC.getCode().equals(dataAccessDto.getTaskType())).runType(dataAccessRunDto.getRunType()).executeNo(dataAccessRunDto.getExecuteNo()).build();

	}
}
