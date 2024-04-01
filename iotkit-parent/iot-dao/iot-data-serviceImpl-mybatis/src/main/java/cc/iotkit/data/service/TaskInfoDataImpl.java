/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.manager.ITaskInfoData;
import cc.iotkit.data.model.TbTaskInfo;

import cc.iotkit.model.rule.RuleAction;
import cc.iotkit.model.rule.TaskInfo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Primary
@Service
@RequiredArgsConstructor
public class TaskInfoDataImpl implements ITaskInfoData, IJPACommData<TaskInfo, String, TbTaskInfo> {

    @Resource
    private TaskInfoService taskInfoService;

    @Override
    public TaskInfoService getBaseRepository() {
        return taskInfoService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbTaskInfo.class;
    }

    @Override
    public Class getTClass() {
        return TaskInfo.class;
    }

    @Override
    public List<TaskInfo> findByUid(String uid) {
        return taskInfoService.findByUid(uid).stream().map(this::to)
                .collect(Collectors.toList());
    }

    @Override
    public Paging<TaskInfo> findByUid(String uid, int page, int size) {
        Page<TbTaskInfo> paged = taskInfoService.findPageByCondition(new LambdaQueryWrapper<TbTaskInfo>()
                .eq(TbTaskInfo::getUid, uid), page, size);
        return new Paging<>(paged.getTotal(), MapstructUtils.convert(paged.getRecords(), TaskInfo.class));
    }

    @Override
    public long countByUid(String uid) {
        return 0;
    }

    @Override
    public TaskInfo findById(String s) {
        return to(Optional.of(taskInfoService.getById(s)).orElse(null));
    }

    @Override
    public Paging<TaskInfo> findAll(PageRequest<TaskInfo> pageRequest) {
        Page<TbTaskInfo> pageByCondition = taskInfoService.findPageByCondition(new LambdaQueryWrapper<>(),
                pageRequest.getPageNum(), pageRequest.getPageSize());
        return new Paging<>(pageByCondition.getTotal(),
                pageByCondition.getRecords().stream().map(this::to).collect(Collectors.toList())
        );
    }

    @Override
    public TaskInfo save(TaskInfo data) {
        if (StringUtils.isBlank(data.getId())) {
            data.setId(UUID.randomUUID().toString());
            data.setCreateAt(System.currentTimeMillis());
        }
        taskInfoService.save(to(data));
        return data;
    }

    private TaskInfo to(TbTaskInfo tb) {
        TaskInfo convert = MapstructUtils.convert(tb, TaskInfo.class);
        assert convert != null;
        convert.setActions(JsonUtils.parseArray(tb.getActions(), RuleAction.class));
        return convert;
    }

    private TbTaskInfo to(TaskInfo t) {
        TbTaskInfo convert = MapstructUtils.convert(t, TbTaskInfo.class);
        assert convert != null;
        convert.setActions(JsonUtils.toJsonString(t.getActions()));
        return convert;
    }

}
