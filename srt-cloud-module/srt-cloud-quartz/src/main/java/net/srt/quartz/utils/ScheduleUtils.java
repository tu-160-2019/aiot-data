package net.srt.quartz.utils;

import net.srt.api.module.data.integrate.constant.AccessRunType;
import net.srt.framework.common.exception.ServerException;
import net.srt.quartz.entity.ScheduleJobEntity;
import net.srt.quartz.enums.ScheduleConcurrentEnum;
import net.srt.quartz.enums.ScheduleStatusEnum;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import java.util.Date;

/**
 * 定时任务工具类
 *
 * @author 阿沐 babamu@126.com
 */
public class ScheduleUtils {
	private final static String JOB_NAME = "TASK_NAME_";
	/**
	 * 任务调度参数key
	 */
	public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY";

	/**
	 * 获取quartz任务类
	 */
	public static Class<? extends Job> getJobClass(ScheduleJobEntity scheduleJob) {
		if (scheduleJob.getConcurrent().equals(ScheduleConcurrentEnum.NO.getValue())) {
			return ScheduleDisallowConcurrentExecution.class;
		} else {
			return ScheduleConcurrentExecution.class;
		}
	}

	/**
	 * 获取触发器key
	 */
	public static TriggerKey getTriggerKey(ScheduleJobEntity scheduleJob) {
		if (AccessRunType.HAND.getCode().equals(scheduleJob.getRunType())) {
			return TriggerKey.triggerKey(JOB_NAME + scheduleJob.getExecuteNo(), scheduleJob.getJobGroup());
		}
		return TriggerKey.triggerKey(JOB_NAME + scheduleJob.getId(), scheduleJob.getJobGroup());
	}

	/**
	 * 获取jobKey
	 */
	public static JobKey getJobKey(ScheduleJobEntity scheduleJob) {
		if (AccessRunType.HAND.getCode().equals(scheduleJob.getRunType())) {
			return JobKey.jobKey(JOB_NAME + scheduleJob.getExecuteNo(), scheduleJob.getJobGroup());
		}
		return JobKey.jobKey(JOB_NAME + scheduleJob.getId(), scheduleJob.getJobGroup());
	}

	/**
	 * 创建定时任务
	 */
	public static void createScheduleJob(Scheduler scheduler, ScheduleJobEntity scheduleJob) {
		try {
			// job key
			JobKey jobKey = getJobKey(scheduleJob);
			// 构建job信息
			JobDetail jobDetail = JobBuilder.newJob(getJobClass(scheduleJob)).withIdentity(jobKey).build();

			Trigger trigger;
			//只执行一次
			if (scheduleJob.getOnce()) {
				trigger = TriggerBuilder.newTrigger()
						.startAt(new Date())
						.withIdentity(getTriggerKey(scheduleJob))
						.withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0))
						.build();
			} else {
				// 按新的cronExpression表达式构建一个新的trigger
				trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(scheduleJob))
						.withSchedule(CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression())
								.withMisfireHandlingInstructionDoNothing()).build();
			}

			// 放入参数，运行时的方法可以获取
			jobDetail.getJobDataMap().put(JOB_PARAM_KEY, scheduleJob);

			// 判断是否存在
			if (scheduler.checkExists(jobKey)) {
				// 防止创建时存在数据问题，先移除，然后再执行创建操作
				scheduler.deleteJob(jobKey);
			}

			if (scheduleJob.getOnce()) {
				// 执行调度任务
				scheduler.scheduleJob(jobDetail, trigger);
			}

			// 判断任务是否过期
			if (!scheduleJob.getOnce() && CronUtils.getNextExecution(scheduleJob.getCronExpression()) != null) {
				// 执行调度任务
				scheduler.scheduleJob(jobDetail, trigger);
			}

			// 暂停任务
			if (ScheduleStatusEnum.PAUSE.getValue().equals(scheduleJob.getStatus())) {
				scheduler.pauseJob(jobKey);
			}
		} catch (SchedulerException e) {
			throw new ServerException("创建定时任务失败", e);
		}
	}


	/**
	 * 立即执行任务
	 */
	public static void run(Scheduler scheduler, ScheduleJobEntity scheduleJob) {
		try {
			// 参数
			JobDataMap dataMap = new JobDataMap();
			dataMap.put(JOB_PARAM_KEY, scheduleJob);

			JobKey jobKey = getJobKey(scheduleJob);
			if (scheduler.checkExists(jobKey)) {
				scheduler.triggerJob(jobKey, dataMap);
			} else {
				createScheduleJob(scheduler, scheduleJob);
			}
		} catch (SchedulerException e) {
			throw new ServerException("执行定时任务失败", e);
		}
	}

	/**
	 * 暂停任务
	 */
	public static void pauseJob(Scheduler scheduler, ScheduleJobEntity scheduleJob) {
		try {
			scheduler.pauseJob(getJobKey(scheduleJob));
		} catch (SchedulerException e) {
			throw new ServerException("暂停定时任务失败", e);
		}
	}

	/**
	 * 恢复任务
	 */
	public static void resumeJob(Scheduler scheduler, ScheduleJobEntity scheduleJob) {
		try {
			scheduler.resumeJob(getJobKey(scheduleJob));
		} catch (SchedulerException e) {
			throw new ServerException("恢复定时任务失败", e);
		}
	}

	/**
	 * 更新定时任务
	 */
	public static void updateSchedulerJob(Scheduler scheduler, ScheduleJobEntity scheduleJob) {
		// 判断是否存在
		JobKey jobKey = ScheduleUtils.getJobKey(scheduleJob);

		try {
			// 防止创建时存在数据问题，先移除，然后再执行创建操作
			if (scheduler.checkExists(jobKey)) {
				scheduler.deleteJob(jobKey);
			}
		} catch (SchedulerException e) {
			throw new ServerException("更新定时任务失败", e);
		}

		ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
	}

	/**
	 * 删除定时任务
	 */
	public static void deleteScheduleJob(Scheduler scheduler, ScheduleJobEntity scheduleJob) {
		try {
			scheduler.interrupt(getJobKey(scheduleJob));
			scheduler.deleteJob(getJobKey(scheduleJob));
		} catch (SchedulerException e) {
			throw new ServerException("删除定时任务失败", e);
		}
	}
}
