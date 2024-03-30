/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.ruleengine.task;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.data.manager.ITaskInfoData;
import cc.iotkit.model.rule.TaskInfo;
import cc.iotkit.model.rule.TaskLog;
import cc.iotkit.temporal.ITaskLogData;
import cn.hutool.core.collection.CollectionUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TaskManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private ITaskInfoData taskInfoData;

    @Lazy
    @Autowired
    private ITaskLogData taskLogData;

    public TaskManager() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(this::initTask, 1, TimeUnit.SECONDS);
    }

    public void initTask() {
        int idx = 1;
        while (true) {
            PageRequest<TaskInfo> pageRequest = new PageRequest<>();
            pageRequest.setPageNum(idx);
            pageRequest.setPageSize(100);
            Paging<TaskInfo> all = taskInfoData.findAll(pageRequest);
            List<TaskInfo> tasks = all.getRows();
            // 如果记录为空，直接跳出循环
            if (CollectionUtil.isEmpty(tasks)) {
                break;
            }
            tasks.forEach(task -> {
                try {
                    if (!TaskInfo.STATE_RUNNING.equals(task.getState())) {
                        return;
                    }
                    log.info("got task {} to init", task.getId());
                    addTask(task);
                } catch (Throwable e) {
                    log.error("add task error", e);
                }
            });
            idx++;
        }
    }

    private void addTask(TaskInfo task) throws SchedulerException {
        // 指明job的名称，所在组的名称，以及绑定job类
        JobDataMap jobData = new JobDataMap();
        jobData.put("taskInfo", task);
        JobDetail jobDetail = JobBuilder.newJob(CommonJob.class)
                .withIdentity(task.getId(), task.getUid())
                .usingJobData(jobData)
                .build();

        ScheduleBuilder<?> scheduleBuilder = getScheduleBuilder(task);
        if (scheduleBuilder == null) {
            log.error("get task builder failed");
            return;
        }

        // 定义调度触发规则
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(task.getId(), task.getUid())// 触发器key
                .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.SECOND))
                .withSchedule(scheduleBuilder)
                .startNow().build();

        // 把作业和触发器注册到任务调度中
        Scheduler scheduler = getScheduler();
        scheduler.scheduleJob(jobDetail, trigger);

        // 启动
        if (!scheduler.isShutdown()) {
            scheduler.start();
        }
    }

    private ScheduleBuilder<?> getScheduleBuilder(TaskInfo task) {
        if (TaskInfo.TYPE_TIMER.equals(task.getType())) {
            return CronScheduleBuilder.cronSchedule(task.getExpression());
        }
        if (TaskInfo.TYPE_DELAY.equals(task.getType())) {
            Long delayTime = task.delayTime();
            if (delayTime == null) {
                return null;
            }
            delayTime = System.currentTimeMillis() + delayTime * 1000;

            return DailyTimeIntervalScheduleBuilder
                    .dailyTimeIntervalSchedule()
                    .startingDailyAt(TimeOfDay.hourAndMinuteAndSecondFromDate(new Date(delayTime)))
                    .endingDailyAt(TimeOfDay.hourAndMinuteAndSecondFromDate(new Date(delayTime + 1000)))
                    .withRepeatCount(1)
                    ;
        }
        return null;
    }

    public void saveTask(TaskInfo task) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(task.getId(), task.getUid());
        Trigger oldTrigger = getScheduler().getTrigger(triggerKey);
        if (oldTrigger == null) {
            log.warn("saveTask:trigger does not exist");
            addTask(task);
            return;
        }
        Scheduler scheduler = getScheduler();
        scheduler.pauseTrigger(triggerKey);
        scheduler.unscheduleJob(triggerKey);
        scheduler.deleteJob(JobKey.jobKey(task.getId(), task.getUid()));

        addTask(task);
    }

    public void renewTask(TaskInfo task) throws SchedulerException {
        saveTask(task);
    }

    public TaskInfo updateTaskState(String taskId, String state, String reason) {
        TaskInfo taskInfo = taskInfoData.findById(taskId);
        if (taskInfo == null) {
            return null;
        }
        taskInfo.setState(state);
        taskInfo.setReason(reason);
        taskInfoData.save(taskInfo);
        return taskInfo;
    }

    public void finishTask(String taskId) {
        TaskInfo taskInfo = taskInfoData.findById(taskId);
        if (taskInfo == null) {
            return;
        }
        taskInfo.setState(TaskInfo.STATE_FINISHED);
        taskInfoData.save(taskInfo);
    }

    @SneakyThrows
    public void pauseTask(String taskId, String reason) {
        TaskInfo taskInfo = updateTaskState(taskId, TaskInfo.STATE_STOP, reason);
        if (taskInfo == null) {
            return;
        }
        Scheduler scheduler = getScheduler();
        TriggerKey triggerKey = new TriggerKey(taskInfo.getId(), taskInfo.getUid());
        if (!scheduler.checkExists(triggerKey)) {
            return;
        }

        scheduler.pauseTrigger(triggerKey);
        scheduler.pauseJob(JobKey.jobKey(taskInfo.getId(), taskInfo.getUid()));
    }

    @SneakyThrows
    public void deleteTask(String taskId, String reason) {
        TaskInfo taskInfo = updateTaskState(taskId, TaskInfo.STATE_STOP, reason);
        if (taskInfo == null) {
            return;
        }
        Scheduler scheduler = getScheduler();
        TriggerKey triggerKey = new TriggerKey(taskInfo.getId(), taskInfo.getUid());
        if (!scheduler.checkExists(triggerKey)) {
            return;
        }
        scheduler.deleteJob(JobKey.jobKey(taskInfo.getId(), taskInfo.getUid()));
    }

    @SneakyThrows
    public void resumeTask(String taskId, String reason) {
        TaskInfo taskInfo = updateTaskState(taskId, TaskInfo.STATE_RUNNING, reason);
        if (taskInfo == null) {
            return;
        }
        Scheduler scheduler = getScheduler();
        TriggerKey triggerKey = new TriggerKey(taskInfo.getId(), taskInfo.getUid());
        if (!scheduler.checkExists(triggerKey)) {
            addTask(taskInfo);
        }

        scheduler.resumeTrigger(triggerKey);
        scheduler.resumeJob(JobKey.jobKey(taskInfo.getId(), taskInfo.getUid()));
    }

    public void addLog(String taskId, boolean success, String content) {
        taskLogData.add(TaskLog.builder()
                .id(UUID.randomUUID().toString())
                .taskId(taskId)
                .success(success)
                .content(content)
                .logAt(System.currentTimeMillis())
                .build());
    }

    public Scheduler getScheduler() {
        return this.applicationContext.getBean("scheduler", Scheduler.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
