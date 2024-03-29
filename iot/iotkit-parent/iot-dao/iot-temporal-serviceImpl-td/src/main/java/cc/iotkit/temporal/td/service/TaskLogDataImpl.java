/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.td.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.model.rule.TaskLog;
import cc.iotkit.temporal.ITaskLogData;
import cc.iotkit.temporal.td.dao.TdTemplate;
import cc.iotkit.temporal.td.dm.TableManager;
import cc.iotkit.temporal.td.model.TbTaskLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskLogDataImpl implements ITaskLogData {

    @Autowired
    private TdTemplate tdTemplate;

    @Override
    public void deleteByTaskId(String taskId) {
        tdTemplate.update("delete from task_log where task_id=? and time<=NOW()", taskId);
    }

    @Override
    public Paging<TaskLog> findByTaskId(String taskId, int page, int size) {
        String sql = "select time,content,success,task_id from task_log where task_id=? order by time desc limit %d offset %d";
        sql = String.format(sql, size, (page - 1) * size);
        List<TbTaskLog> taskLogs = tdTemplate.query(sql, new BeanPropertyRowMapper<>(TbTaskLog.class), taskId);

        sql = "select count(*) from task_log where task_id=?";
        List<Long> counts = tdTemplate.queryForList(sql, Long.class, taskId);

        return new Paging<>(!counts.isEmpty() ? counts.get(0) : 0, taskLogs.stream().map(r ->
                        new TaskLog(r.getTime().toString(), taskId,
                                r.getContent(), r.getSuccess(), r.getTime()))
                .collect(Collectors.toList()));
    }

    @Override
    public void add(TaskLog log) {
        //使用taskId作表名
        String sql = String.format("INSERT INTO %s (%s) USING %s TAGS ('%s') VALUES (%s);",
                "task_log_" + TableManager.rightTbName(log.getTaskId()),
                "time,content,success",
                "task_log",
                log.getTaskId(),
                "?,?,?"
        );
        tdTemplate.update(sql, System.currentTimeMillis(), log.getContent(), log.getSuccess());
    }
}
