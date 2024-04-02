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

import cc.iotkit.model.rule.RuleAction;
import cc.iotkit.model.rule.TaskInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.List;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonJob implements Job {

    private ActionExecutorManager actionExecutorManager;

    private TaskManager taskManager;

    private TaskInfo taskInfo;

    @Override
    public void execute(JobExecutionContext context) {
        String result = "";
        boolean success = true;
        try {
            List<RuleAction> actions = taskInfo.getActions();
            log.info("start execute job,task:{}", taskInfo.getId());
            for (RuleAction action : actions) {
                actionExecutorManager.execute(action.getType(), action.getConfig());
            }
        } catch (Throwable e) {
            log.error("execute job failed", e);
            result = e.getMessage();
            success = false;
        }
        if (TaskInfo.TYPE_DELAY.equals(taskInfo.getType())) {
            taskManager.finishTask(taskInfo.getId());
        }
        taskManager.addLog(taskInfo.getId(), success, result);
    }

}