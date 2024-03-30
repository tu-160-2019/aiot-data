/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.es.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.model.rule.TaskLog;
import cc.iotkit.temporal.ITaskLogData;
import cc.iotkit.temporal.es.dao.TaskLogRepository;
import cc.iotkit.temporal.es.document.DocTaskLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class TaskLogDataImpl implements ITaskLogData {

    @Autowired
    private TaskLogRepository taskLogRepository;

    @Override
    public void deleteByTaskId(String taskId) {
        taskLogRepository.deleteByTaskId(taskId);
    }

    @Override
    public Paging<TaskLog> findByTaskId(String taskId, int page, int size) {
        Page<DocTaskLog> paged = taskLogRepository.findByTaskIdOrderByLogAtDesc(taskId, Pageable.ofSize(size).withPage(page - 1));
        return new Paging<>(paged.getTotalElements(),
                paged.getContent().stream().map(o -> MapstructUtils.convert(o, TaskLog.class))
                        .collect(Collectors.toList()));
    }

    @Override
    public void add(TaskLog log) {
        taskLogRepository.save(MapstructUtils.convert(log, DocTaskLog.class));
    }
}
