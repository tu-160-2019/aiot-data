/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.ts.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.model.rule.TaskLog;
import cc.iotkit.temporal.ITaskLogData;
import cc.iotkit.temporal.ts.dao.TsTemplate;
import cc.iotkit.temporal.ts.dm.TableManager;
import cc.iotkit.temporal.ts.model.TsTaskLog;
import org.jooq.InsertValuesStep4;
import org.jooq.Record;
import org.jooq.Record4;
import org.jooq.SelectForUpdateStep;
import org.jooq.conf.ParamType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Service
public class TaskLogDataImpl implements ITaskLogData {

    @Autowired
    private TsTemplate tsTemplate;

    @Override
    public void deleteByTaskId(String taskId) {
        tsTemplate.update("delete from task_log where task_id=? and time<=NOW()", taskId);
    }

    @Override
    public Paging<TaskLog> findByTaskId(String taskId, int page, int size) {
        SelectForUpdateStep<Record4<Object, Object, Object, Object>> sqlStep = TableManager.getSqlBuilder()
                .select(field("time"), field("content"), field("success"), field("task_id"))
                .from(table("task_log"))
                .where(field("task_id").eq(taskId))
                .orderBy(field("time").desc())
                .limit(size)
                .offset((page - 1) * size);

        // Get the SQL string from the query
        String sql = sqlStep.getSQL(ParamType.INLINED);
        List<TsTaskLog> taskLogs = tsTemplate.query(sql, new BeanPropertyRowMapper<>(TsTaskLog.class));

        String whereSql = TableManager.getSqlBuilder().selectCount().from(table("task_log"))
                .where(field("task_id").eq(taskId)).getSQL(ParamType.INLINED);
        Long count = tsTemplate.queryForObject(whereSql, new BeanPropertyRowMapper<>(Long.class));

        return new Paging<>(count, taskLogs.stream().map(r ->
                        new TaskLog(r.getTime().toString(), taskId,
                                r.getContent(), r.getSuccess(), r.getTime()))
                .collect(Collectors.toList()));
    }

    @Override
    public void add(TaskLog log) {

        InsertValuesStep4<Record, Object, Object, Object, Object> sqlStep
                = TableManager.getSqlBuilder().insertInto(table("tag_log"),
                field("time"),
                field("task_id"),
                field("content"), field("success")).values(
                new Date(),
                log.getTaskId(),
                log.getContent(), log.getSuccess());

        tsTemplate.update(sqlStep.getSQL(ParamType.INLINED));
    }
}
