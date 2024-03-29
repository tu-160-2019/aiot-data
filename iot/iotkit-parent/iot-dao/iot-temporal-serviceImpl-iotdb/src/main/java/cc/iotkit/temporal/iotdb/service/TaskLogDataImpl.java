/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.iotdb.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.model.rule.TaskLog;
import cc.iotkit.temporal.ITaskLogData;
import org.springframework.stereotype.Service;

@Service
public class TaskLogDataImpl implements ITaskLogData {

    @Override
    public void deleteByTaskId(String taskId) {
    }

    @Override
    public Paging<TaskLog> findByTaskId(String taskId, int page, int size) {
        return new Paging<>();
    }

    @Override
    public void add(TaskLog log) {
    }
}
