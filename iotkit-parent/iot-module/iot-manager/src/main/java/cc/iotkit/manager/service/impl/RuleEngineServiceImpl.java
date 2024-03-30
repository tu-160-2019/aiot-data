package cc.iotkit.manager.service.impl;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.satoken.utils.AuthUtil;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.ReflectUtil;
import cc.iotkit.data.manager.IRuleInfoData;
import cc.iotkit.data.manager.ITaskInfoData;
import cc.iotkit.manager.dto.bo.ruleinfo.RuleInfoBo;
import cc.iotkit.manager.dto.bo.ruleinfo.RuleLogBo;
import cc.iotkit.manager.dto.bo.taskinfo.TaskInfoBo;
import cc.iotkit.manager.dto.bo.taskinfo.TaskLogBo;
import cc.iotkit.manager.dto.vo.ruleinfo.RuleInfoVo;
import cc.iotkit.manager.dto.vo.ruleinfo.RuleLogVo;
import cc.iotkit.manager.dto.vo.taskinfo.TaskInfoVo;
import cc.iotkit.manager.dto.vo.taskinfo.TaskLogVo;
import cc.iotkit.manager.service.DataOwnerService;
import cc.iotkit.manager.service.IRuleEngineService;
import cc.iotkit.model.rule.RuleInfo;
import cc.iotkit.model.rule.RuleLog;
import cc.iotkit.model.rule.TaskInfo;
import cc.iotkit.model.rule.TaskLog;
import cc.iotkit.ruleengine.rule.RuleManager;
import cc.iotkit.ruleengine.task.TaskManager;
import cc.iotkit.temporal.IRuleLogData;
import cc.iotkit.temporal.ITaskLogData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @Author: jay
 * @Date: 2023/5/30 18:15
 * @Version: V1.0
 * @Description: 规则引擎服务实现
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class RuleEngineServiceImpl implements IRuleEngineService {
    @Autowired
    private ITaskInfoData taskInfoData;

    @Autowired
    private IRuleInfoData ruleInfoData;

    @Autowired
    private IRuleLogData ruleLogData;

    @Autowired
    private DataOwnerService dataOwnerService;

    @Autowired
    private TaskManager taskManager;

    @Autowired
    private RuleManager ruleManager;

    @Autowired
    private ITaskLogData taskLogData;

    @Override
    public Paging<RuleInfoVo> selectPageList(PageRequest<RuleInfoBo> request) {
        RuleInfoBo data = request.getData();
        String type = data.getType();
        Integer page = request.getPageNum();
        Integer size = request.getPageSize();
        if (AuthUtil.isAdmin()) {
            return MapstructUtils.convert(ruleInfoData.findByType(type, page, size), RuleInfoVo.class);
        } else {
            return MapstructUtils.convert(ruleInfoData.findByUidAndType(AuthUtil.getUserId(), type, page, size), RuleInfoVo.class);
        }
    }

    @Override
    public boolean saveRule(RuleInfoBo ruleInfoBo) {
        RuleInfo rule = ruleInfoBo.to(RuleInfo.class);
        if (StringUtils.isBlank(rule.getId())) {
            rule.setId(UUID.randomUUID().toString());
            rule.setState(RuleInfo.STATE_STOPPED);
            rule.setCreateAt(System.currentTimeMillis());
            rule.setUid(AuthUtil.getUserId());
            ruleInfoData.save(rule);
            ruleManager.add(rule);
        } else {
            RuleInfo ruleInfo = ruleInfoData.findById(rule.getId());
            if (ruleInfo == null) {
                throw new BizException(ErrCode.RULE_NOT_FOUND);
            }
            if (RuleInfo.STATE_RUNNING.equals(ruleInfo.getState())) {
                throw new BizException(ErrCode.RULE_ALREADY_RUNNING);
            }

            dataOwnerService.checkOwner(ruleInfo);

            ruleInfo.setListeners(rule.getListeners());
            ruleInfo.setFilters(rule.getFilters());
            ruleInfo.setActions(rule.getActions());
            ruleInfo.setName(rule.getName());
            ruleInfo.setDesc(rule.getDesc());

            ruleInfoData.save(ruleInfo);
        }
        return true;
    }

    @Override
    public boolean pauseRule(String ruleId) {
        RuleInfo ruleInfo = ruleInfoData.findById(ruleId);
        if (ruleInfo == null) {
            throw new BizException(ErrCode.RULE_NOT_FOUND);
        }
        dataOwnerService.checkOwner(ruleInfo);
        ruleInfo.setState(RuleInfo.STATE_STOPPED);
        ruleInfoData.save(ruleInfo);
        ruleManager.pause(ruleInfo.getId());
        return true;
    }

    @Override
    public boolean resumeRule(String ruleId) {
        RuleInfo ruleInfo = ruleInfoData.findById(ruleId);
        if (ruleInfo == null) {
            throw new BizException(ErrCode.RULE_NOT_FOUND);
        }
        dataOwnerService.checkOwner(ruleInfo);
        ruleInfo.setState(RuleInfo.STATE_RUNNING);
        ruleInfoData.save(ruleInfo);
        ruleManager.resume(ruleInfo);
        return true;
    }

    @Override
    public boolean deleteRule(String ruleId) {
        RuleInfo ruleInfo = ruleInfoData.findById(ruleId);
        if (ruleInfo == null) {
            throw new BizException(ErrCode.RULE_NOT_FOUND);
        }
        dataOwnerService.checkOwner(ruleInfo);
        try {
            ruleInfoData.deleteById(ruleInfo.getId());
            ruleManager.remove(ruleInfo.getId());
            ruleLogData.deleteByRuleId(ruleId);
        } catch (Throwable e) {
            log.warn("删除失败", e);
        }
        return true;
    }

    @Override
    public Paging<RuleLogVo> selectRuleLogPageList(PageRequest<RuleLogBo> request) {
        RuleLog ruleLog = request.getData().to(RuleLog.class);

        return ruleLogData.findByRuleId(ruleLog.getRuleId(), request.getPageNum(), request.getPageSize()).to(RuleLogVo.class);
    }

    @Override
    public boolean clearRuleLogs(String ruleId) {
        ruleLogData.deleteByRuleId(ruleId);
        return true;
    }

    @Override
    public Paging<TaskInfoVo> selectTaskPageList(PageRequest<TaskInfoBo> request) {
        if (AuthUtil.isAdmin()) {
            return taskInfoData.findAll(request.to(TaskInfo.class)).to(TaskInfoVo.class);
        }
        return taskInfoData.findByUid(AuthUtil.getUserId(), request.getPageNum(), request.getPageSize()).to(TaskInfoVo.class);
    }

    @Override
    public boolean saveTask(TaskInfoBo bo) {
        TaskInfo taskInfo = bo.to(TaskInfo.class);
        if (StringUtils.isBlank(taskInfo.getId())) {
            taskInfo.setId(UUID.randomUUID().toString());
            taskInfo.setUid(AuthUtil.getUserId());
            taskInfo.setCreateAt(System.currentTimeMillis());
            taskInfo.setState(TaskInfo.STATE_STOP);
        } else {
            TaskInfo oldTask = taskInfoData.findById(taskInfo.getId());
            if (oldTask == null) {
                throw new BizException(ErrCode.TASK_NOT_FOUND);
            }
            taskInfo = ReflectUtil.copyNoNulls(taskInfo, oldTask);
            dataOwnerService.checkOwner(taskInfo);
        }

        taskInfoData.save(taskInfo);
        return true;
    }

    @Override
    public boolean pauseTask(String taskId) {
        TaskInfo taskInfo = taskInfoData.findById(taskId);
        if (taskInfo == null) {
            throw new BizException(ErrCode.TASK_NOT_FOUND);
        }
        dataOwnerService.checkOwner(taskInfo);
        taskManager.pauseTask(taskId, "stop by " + AuthUtil.getUserId());
        return true;
    }

    @Override
    public boolean resumeTask(String taskId) {
        TaskInfo taskInfo = taskInfoData.findById(taskId);
        if (taskInfo == null) {
            throw new BizException(ErrCode.TASK_NOT_FOUND);
        }
        dataOwnerService.checkOwner(taskInfo);
        taskManager.resumeTask(taskId, "resume by " + AuthUtil.getUserId());
        return true;
    }

    @Override
    public boolean renewTask(String taskId) {
        TaskInfo taskInfo = taskInfoData.findById(taskId);
        if (taskInfo == null) {
            throw new BizException(ErrCode.TASK_NOT_FOUND);
        }
        dataOwnerService.checkOwner(taskInfo);
        try {
            taskManager.renewTask(taskInfo);
            taskManager.updateTaskState(taskId, TaskInfo.STATE_RUNNING, "renew by " + AuthUtil.getUserId());
        } catch (SchedulerException e) {
            log.error("renew task error", e);
            throw new BizException(ErrCode.RENEW_TASK_ERROR);
        }
        return true;
    }

    @Override
    public boolean deleteTask(String taskId) {
        TaskInfo taskInfo = taskInfoData.findById(taskId);
        if (taskInfo == null) {
            throw new BizException(ErrCode.TASK_NOT_FOUND);
        }

        dataOwnerService.checkOwner(taskInfo);
        taskManager.deleteTask(taskId, "delete by " + AuthUtil.getUserId());
        taskInfoData.deleteById(taskId);
        try {
            taskLogData.deleteByTaskId(taskId);
        } catch (Throwable e) {
            log.error("delete task logs failed", e);
        }
        return true;
    }

    @Override
    public Paging<TaskLogVo> selectTaskLogPageList(PageRequest<TaskLogBo> request) {
        TaskLog taskLog = request.getData().to(TaskLog.class);
        Paging<TaskLog> byTaskId = taskLogData.findByTaskId(taskLog.getTaskId(), request.getPageNum(), request.getPageSize());
        return byTaskId.to(TaskLogVo.class);
    }

    @Override
    public boolean clearTaskLogs(String taskId) {
        taskLogData.deleteByTaskId(taskId);
        return true;
    }
}
