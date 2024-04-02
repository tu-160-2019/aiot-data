/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.ruleengine.config;

import cc.iotkit.model.rule.TaskInfo;
import cc.iotkit.ruleengine.task.ActionExecutorManager;
import cc.iotkit.ruleengine.task.CommonJob;
import cc.iotkit.ruleengine.task.TaskManager;
import org.quartz.JobDetail;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

@Component
public class JobFactory extends AdaptableJobFactory {
    @Autowired
    private AutowireCapableBeanFactory capableBeanFactory;
    @Autowired
    private ActionExecutorManager actionExecutorManager;
    @Autowired
    private TaskManager taskManager;

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        JobDetail jobDetail = bundle.getJobDetail();
        Object objTask = jobDetail.getJobDataMap().get("taskInfo");

        Class<?> clz = jobDetail.getJobClass();
        Object jobInstance;
        if (clz == CommonJob.class && objTask instanceof TaskInfo) {
            jobInstance = new CommonJob(actionExecutorManager, taskManager, (TaskInfo) objTask);
        } else {
            jobInstance = super.createJobInstance(bundle);
        }
        //进行注入
        capableBeanFactory.autowireBean(jobInstance);
        return jobInstance;
    }
}