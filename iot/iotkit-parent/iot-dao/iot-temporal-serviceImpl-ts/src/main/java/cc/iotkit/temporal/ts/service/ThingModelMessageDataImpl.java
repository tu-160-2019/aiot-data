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
import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.model.stats.TimeData;
import cc.iotkit.temporal.IThingModelMessageData;
import cc.iotkit.temporal.ts.dao.TsTemplate;
import cc.iotkit.temporal.ts.dm.TableManager;
import cc.iotkit.temporal.ts.model.TsThingModelMessage;
import cc.iotkit.temporal.ts.model.TsTimeData;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
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
public class ThingModelMessageDataImpl implements IThingModelMessageData {

    @Autowired
    private TsTemplate tsTemplate;

    public Paging<ThingModelMessage> findByTypeAndIdentifier(String deviceId, String type,
                                                             String identifier,
                                                             int page, int size) {


        Table<Record> table = table("thing_model_message");
        Condition whereConditions = field("device_id").eq(deviceId);
        SelectJoinStep<Record9<Object, Object, Object, Object, Object, Object, Object, Object, Object>> step = TableManager.getSqlBuilder().select(field("time"), field("mid"),
                field("product_key"), field("device_name"), field("type"),
                field("identifier"), field("code"), field("data"),
                field("report_time")).from(table);


        if (StringUtils.isNotBlank(type)) {
            whereConditions.and(field("type").eq(type));
        }
        if (StringUtils.isNotBlank(identifier)) {
            whereConditions.and(field("identifier").eq(identifier));
        }

        String sql = step.where(whereConditions).orderBy(field("time").desc()).limit(size).offset((page - 1) * size).getSQL(ParamType.INLINED);

        List<TsThingModelMessage> ruleLogs = tsTemplate.query(sql,
                new BeanPropertyRowMapper<>(TsThingModelMessage.class)
        );

        String countSql = TableManager.getSqlBuilder().selectCount().from(table).where(whereConditions).getSQL(ParamType.INLINED);
        Long count = tsTemplate.queryForObject(countSql, Long.class);

        return new Paging<>(count, ruleLogs.stream().map(r ->
                new ThingModelMessage(r.getTime().toString(), r.getMid(),
                        deviceId, r.getProductKey(), r.getDeviceName(),
                        r.getUid(), r.getType(), r.getIdentifier(), r.getCode(),
                        r.getData(),
                        r.getTime().getTime(), r.getReportTime()))
                .collect(Collectors.toList()));
    }

    @Override
    public List<TimeData> getDeviceMessageStatsWithUid(String uid, long start, long end) {

        Table<Record> table = table("thing_model_message");

        Condition con = field("time").greaterOrEqual(new Date(start)).and(field("time").lessOrEqual(new Date(end)));
        if (StringUtils.isNotBlank(uid)) {
            con.and(field("uid").eq(uid));
        }

        String sql = TableManager.getSqlBuilder().select(field("date_trunc('hour', \"time\")").as("time"), field("count(*)").as("data"))
                .from(table).where(con).groupBy(field("date_trunc('hour', \"time\")")).orderBy(field("time").asc()).getSQL(ParamType.INLINED);


        List<TsTimeData> query = tsTemplate.query(sql, new BeanPropertyRowMapper<>(TsTimeData.class));
        return query.stream().map(o -> {
            TimeData timeData = new TimeData();
            timeData.setData(o.getData());
            timeData.setTime(o.getTime().getTime());
            return timeData;

        }).collect(Collectors.toList());
    }

    @Override
    public void add(ThingModelMessage msg) {
        Table<Record> table = table("thing_model_message");

        String sql = TableManager.getSqlBuilder().insertInto(table,
                field("time"),
                field("device_id"),
                field("mid"),
                field("product_key"),
                field("device_name"),
                field("uid"),
                field("type"),
                field("identifier"),
                field("code"),
                field("data"), field("report_time"))
                .values(new Date(msg.getOccurred()), msg.getDeviceId(), msg.getMid(),
                        msg.getProductKey(), msg.getDeviceName(),
                        msg.getUid(), msg.getType(),
                        msg.getIdentifier(), msg.getCode(),
                        msg.getData() == null ? "{}" : JsonUtils.toJsonString(msg.getData()),
                        msg.getTime()).getSQL(ParamType.INLINED);
        tsTemplate.update(sql);
    }

    @Override
    public long count() {
        List<Long> counts = tsTemplate.queryForList("select count(*) from thing_model_message", Long.class);
        return counts.size() > 0 ? counts.get(0) : 0;
    }
}
