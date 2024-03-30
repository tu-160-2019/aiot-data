package cc.iotkit.manager.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.manager.dto.bo.ruleinfo.RuleInfoBo;
import cc.iotkit.manager.dto.bo.ruleinfo.RuleLogBo;
import cc.iotkit.manager.dto.bo.taskinfo.TaskInfoBo;
import cc.iotkit.manager.dto.bo.taskinfo.TaskLogBo;
import cc.iotkit.manager.dto.vo.ruleinfo.RuleInfoVo;
import cc.iotkit.manager.dto.vo.ruleinfo.RuleLogVo;
import cc.iotkit.manager.dto.vo.taskinfo.TaskInfoVo;
import cc.iotkit.manager.dto.vo.taskinfo.TaskLogVo;

/**
 * @Author: jay
 * @Date: 2023/5/30 18:14
 * @Version: V1.0
 * @Description: 规则引擎服务接口
 */
public interface IRuleEngineService {
    Paging<RuleInfoVo> selectPageList(PageRequest<RuleInfoBo> request);

    boolean saveRule(RuleInfoBo ruleInfoBo);

    boolean pauseRule(String ruleId);

    boolean resumeRule(String ruleId);

    boolean deleteRule(String ruleId);

    Paging<RuleLogVo> selectRuleLogPageList(PageRequest<RuleLogBo> request);

    boolean clearRuleLogs(String ruleId);

    Paging<TaskInfoVo> selectTaskPageList(PageRequest<TaskInfoBo> request);

    boolean saveTask(TaskInfoBo taskInfo);

    boolean pauseTask(String taskId);

    boolean resumeTask(String data);

    boolean renewTask(String taskId);

    boolean deleteTask(String taskId);

    Paging<TaskLogVo> selectTaskLogPageList(PageRequest<TaskLogBo> request);

    boolean clearTaskLogs(String taskId);
}
