/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.manager.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.manager.dto.bo.ruleinfo.RuleInfoBo;
import cc.iotkit.manager.dto.bo.ruleinfo.RuleLogBo;
import cc.iotkit.manager.dto.bo.taskinfo.TaskInfoBo;
import cc.iotkit.manager.dto.bo.taskinfo.TaskLogBo;
import cc.iotkit.manager.dto.vo.ruleinfo.RuleInfoVo;
import cc.iotkit.manager.dto.vo.ruleinfo.RuleLogVo;
import cc.iotkit.manager.dto.vo.taskinfo.TaskInfoVo;
import cc.iotkit.manager.dto.vo.taskinfo.TaskLogVo;
import cc.iotkit.manager.service.IRuleEngineService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"规则引擎"})
@Slf4j
@RestController
@RequestMapping("/rule_engine")
public class RuleEngineController {

    @Autowired
    IRuleEngineService ruleEngineService;

    @ApiOperation("规则列表")
    @SaCheckPermission("iot:rule:query")
    @PostMapping("/list")
    public Paging<RuleInfoVo> rules(
            @Validated @RequestBody
                    PageRequest<RuleInfoBo> request
    ) {
        return ruleEngineService.selectPageList(request);
    }

    @ApiOperation("保存规则")
    @SaCheckPermission("iot:rule:eidt")
    @PostMapping("/edit")
    public boolean saveRule(@RequestBody @Validated Request<RuleInfoBo> ruleInfoBo) {
        return ruleEngineService.saveRule(ruleInfoBo.getData());
    }

    @ApiOperation("暂停规则")
    @SaCheckPermission("iot:rule:edit")
    @PostMapping("/pause")
    public boolean pauseRule(@Validated @RequestBody Request<String> request) {
        String ruleId = request.getData();
        return ruleEngineService.pauseRule(ruleId);
    }

    @ApiOperation("恢复规则")
    @SaCheckPermission("iot:rule:edit")
    @PostMapping("/resume")
    public boolean resumeRule(@Validated @RequestBody Request<String> request) {
        String ruleId = request.getData();
        return ruleEngineService.resumeRule(ruleId);
    }

    @ApiOperation("删除规则")
    @SaCheckPermission("iot:rule:remove")
    @PostMapping("/delete")
    public boolean deleteRule(@Validated @RequestBody Request<String> request) {
        String ruleId = request.getData();
        return ruleEngineService.deleteRule(ruleId);
    }

    @ApiOperation("规则日志")
    @SaCheckPermission("iot:rule:query")
    @PostMapping("/ruleLog/list")
    public Paging<RuleLogVo> getRuleLogs(
            @Validated @RequestBody PageRequest<RuleLogBo> request
    ) {
        return ruleEngineService.selectRuleLogPageList(request);
    }

    @ApiOperation("清理日志")
    @SaCheckPermission("iot:rule:remove")
    @PostMapping("/ruleLog/clear")
    public boolean clearRuleLogs(@Validated @RequestBody Request<String> request) {
        String ruleId = request.getData();
        return ruleEngineService.clearRuleLogs(ruleId);

    }

    @ApiOperation("定时任务列表")
    @SaCheckPermission("iot:task:query")
    @PostMapping("/tasks/list")
    public Paging<TaskInfoVo> tasks(@Validated @RequestBody PageRequest<TaskInfoBo> request) {
        return ruleEngineService.selectTaskPageList(request);
    }

    @ApiOperation("保存定时任务")
    @SaCheckPermission("iot:task:edit")
    @PostMapping("/task/save")
    public boolean saveTask(@Validated @RequestBody Request<TaskInfoBo> taskInfo) {
        return ruleEngineService.saveTask(taskInfo.getData());
    }

    @ApiOperation("停止定时任务")
    @SaCheckPermission("iot:task:edit")
    @PostMapping("/task/pause")
    public boolean pauseTask(@Validated @RequestBody Request<String> request) {
        String taskId = request.getData();
        return ruleEngineService.pauseTask(taskId);
    }

    @ApiOperation("恢复定时任务")
    @SaCheckPermission("iot:task:edit")
    @PostMapping("/task/resume")
    public boolean resumeTask(@Validated @RequestBody Request<String> request) {
        return ruleEngineService.resumeTask(request.getData());
    }

    @ApiOperation("更新定时任务")
    @SaCheckPermission("iot:task:edit")
    @PostMapping("/task/renew")
    public boolean renewTask(@Validated @RequestBody Request<String> request) {
        String taskId = request.getData();
        return ruleEngineService.renewTask(taskId);

    }

    @ApiOperation("删除定时任务")
    @SaCheckPermission("iot:task:remove")
    @PostMapping("/task/delete")
    public boolean deleteTask(@Validated @RequestBody Request<String> request) {
        String taskId = request.getData();
        return ruleEngineService.deleteTask(taskId);

    }

    @ApiOperation("定时任务日志list")
    @SaCheckPermission("iot:task:query")
    @PostMapping("/taskLogs/list")
    public Paging<TaskLogVo> getTaskLogs(
            @Validated @RequestBody PageRequest<TaskLogBo> request
    ) {
        return ruleEngineService.selectTaskLogPageList(request);
    }

    @ApiOperation("清除定时任务日志")
    @SaCheckPermission("iot:task:remove")
    @PostMapping("/taskLogs/clear")
    public boolean clearTaskLogs(@Validated @RequestBody Request<String> request) {
        return ruleEngineService.clearTaskLogs(request.getData());
    }

}
