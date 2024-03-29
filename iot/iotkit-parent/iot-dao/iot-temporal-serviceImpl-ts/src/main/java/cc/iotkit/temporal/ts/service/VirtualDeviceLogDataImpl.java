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
import cc.iotkit.model.device.VirtualDeviceLog;
import cc.iotkit.temporal.IVirtualDeviceLogData;
import cc.iotkit.temporal.ts.dao.TsTemplate;
import cc.iotkit.temporal.ts.dm.TableManager;
import cc.iotkit.temporal.ts.model.TsVirtualDeviceLog;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
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
public class VirtualDeviceLogDataImpl implements IVirtualDeviceLogData {

    @Autowired
    private TsTemplate tsTemplate;

    @Override
    public Paging<VirtualDeviceLog> findByVirtualDeviceId(String virtualDeviceId, int page, int size) {

        Table<Record> table = table("virtual_device_log");

        Condition whereConditions = field("virtual_device_id").eq(virtualDeviceId.toLowerCase());
        DSLContext sqlBuilder = TableManager.getSqlBuilder();
        String sql = sqlBuilder.select(field("time"), field("virtual_device_id"),
                        field("virtual_device_name"), field("device_total"), field("result")).from(table).where(whereConditions)
                .orderBy(field("time").desc()).limit(size).offset((page - 1) * size).getSQL(ParamType.INLINED);

        List<TsVirtualDeviceLog> logs = tsTemplate.query(sql, new BeanPropertyRowMapper<>(TsVirtualDeviceLog.class));

        String countSql = sqlBuilder.selectCount().from(table).where(whereConditions).getSQL(ParamType.INLINED);

        Long count = tsTemplate.queryForObject(countSql, Long.class);

        return new Paging<>(count, logs.stream().map(r ->
                new VirtualDeviceLog(r.getTime().toString(), virtualDeviceId,
                        r.getVirtualDeviceName(),
                        r.getDeviceTotal(), r.getResult(),
                        r.getTime().getTime()))
                .collect(Collectors.toList()));
    }

    @Override
    public void add(VirtualDeviceLog log) {
        Table<Record> table = table("virtual_device_log");

        String sql = TableManager.getSqlBuilder().insertInto(table, field("time"), field("virtual_device_id"),
                        field("virtual_device_name"),
                        field("device_total"), field("result"))
                .values(new Date(), log.getVirtualDeviceId(), log.getVirtualDeviceName(),
                        log.getDeviceTotal(), log.getResult()).getSQL(ParamType.INLINED);

        tsTemplate.update(sql);
    }
}
