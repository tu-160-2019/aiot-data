package net.srt.quartz.utils;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import net.srt.framework.common.utils.ExceptionUtils;
import net.srt.quartz.entity.ScheduleJobEntity;
import net.srt.quartz.entity.ScheduleJobLogEntity;
import net.srt.quartz.enums.ScheduleStatusEnum;
import net.srt.quartz.service.ScheduleJobLogService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Objects;

@Slf4j
public abstract class AbstractScheduleJob implements InterruptableJob {
	private static final ThreadLocal<Date> THREAD_LOCAL = new ThreadLocal<>();

	/**
	 * 记录当前线程
	 */
	private Thread currentThread;

	@Override
	public void interrupt() {
		currentThread.interrupt();
	}

	@Override
	public void execute(JobExecutionContext context) {
		//记录线程
		currentThread = Thread.currentThread();
		ScheduleJobEntity scheduleJob = new ScheduleJobEntity();
		ScheduleJobEntity jobEntity = (ScheduleJobEntity) context.getMergedJobDataMap().get(ScheduleUtils.JOB_PARAM_KEY);
		BeanUtils.copyProperties(jobEntity, scheduleJob);

		try {
			THREAD_LOCAL.set(new Date());
			doExecute(scheduleJob);
			if (scheduleJob.isSaveLog()) {
				saveLog(scheduleJob, null);
			}
		} catch (Exception e) {
			log.error("任务执行失败，任务ID：{}", scheduleJob.getId(), e);
			if (scheduleJob.isSaveLog()) {
				saveLog(scheduleJob, e);
			}
		}
	}

	/**
	 * 执行spring bean方法
	 */
	protected void doExecute(ScheduleJobEntity scheduleJob) throws Exception {
		log.info("准备执行任务，任务名称：{}", scheduleJob.getJobName());

		Object bean = SpringUtil.getBean(scheduleJob.getBeanName());
		Method method = bean.getClass().getDeclaredMethod(scheduleJob.getMethod(), String.class, Thread.class);
		method.invoke(bean, scheduleJob.getParams(), currentThread);

		log.info("任务执行完毕，任务名称：{}", scheduleJob.getJobName());
	}

	/**
	 * 保存 log
	 */
	protected void saveLog(ScheduleJobEntity scheduleJob, Exception e) {
		Date startTime = THREAD_LOCAL.get();
		THREAD_LOCAL.remove();

		// 执行总时长
		long times = System.currentTimeMillis() - startTime.getTime();

		// 保存执行记录
		ScheduleJobLogEntity log = new ScheduleJobLogEntity();
		log.setProjectId(scheduleJob.getProjectId());
		log.setTypeId(scheduleJob.getTypeId());
		log.setJobType(scheduleJob.getJobType());
		log.setJobId(scheduleJob.getId());
		log.setJobName(scheduleJob.getJobName());
		log.setJobGroup(scheduleJob.getJobGroup());
		log.setBeanName(scheduleJob.getBeanName());
		log.setMethod(scheduleJob.getMethod());
		log.setParams(scheduleJob.getParams());
		log.setTimes(times);
		log.setCreateTime(new Date());

		if (e != null) {
			log.setStatus(ScheduleStatusEnum.PAUSE.getValue());
			String error = ExceptionUtils.getExceptionMessage(e);
			log.setError(error);
		} else {
			log.setStatus(ScheduleStatusEnum.NORMAL.getValue());
		}

		// 保存日志
		SpringUtil.getBean(ScheduleJobLogService.class).save(log);
	}

}
