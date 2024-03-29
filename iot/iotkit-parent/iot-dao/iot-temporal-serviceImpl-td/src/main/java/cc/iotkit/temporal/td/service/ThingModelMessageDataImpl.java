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
import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.model.stats.TimeData;
import cc.iotkit.temporal.IThingModelMessageData;
import cc.iotkit.temporal.td.dao.TdTemplate;
import cc.iotkit.temporal.td.model.TbThingModelMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ThingModelMessageDataImpl implements IThingModelMessageData {

    @Autowired
    private TdTemplate tdTemplate;

    @Override
    public Paging<ThingModelMessage> findByTypeAndIdentifier(String deviceId, String type,
                                                             String identifier,
                                                             int page, int size) {
        String sql = "select time,mid,product_key,device_name,type,identifier,code,data,report_time " +
                "from thing_model_message where device_id=? %s order by time desc limit %d offset %d";

        //构建动态条件
        List<Object> args = new ArrayList<>();
        args.add(deviceId);
        StringBuilder sbCond = new StringBuilder();
        if (StringUtils.isNotBlank(type)) {
            sbCond.append(" and type=? ");
            args.add(type);
        }
        if (StringUtils.isNotBlank(identifier)) {
            sbCond.append("and identifier=? ");
            args.add(identifier);
        }

        sql = String.format(sql, sbCond.toString(), size, (page - 1) * size);
        List<TbThingModelMessage> ruleLogs = tdTemplate.query(sql,
                new BeanPropertyRowMapper<>(TbThingModelMessage.class),
                args.toArray()
        );

        sql = String.format("select count(*) from thing_model_message where device_id=? %s",
                sbCond.toString());
        List<Long> counts = tdTemplate.queryForList(sql, Long.class, args.toArray());
        long count = !counts.isEmpty() ? counts.get(0) : 0;

        return new Paging<>(count, ruleLogs.stream().map(r ->
                        new ThingModelMessage(r.getTime().toString(), r.getMid(),
                                deviceId, r.getProductKey(), r.getDeviceName(),
                                r.getUid(), r.getType(), r.getIdentifier(), r.getCode(),
                                JsonUtils.parseObject(r.getData(), Map.class),
                                r.getTime(), r.getReportTime()))
                .collect(Collectors.toList()));
    }

    @Override
    public List<TimeData> getDeviceMessageStatsWithUid(String uid, long start, long end) {
        String sql = "select time,count(*) as data from(" +
                "select TIMETRUNCATE(time,1h) as time from thing_model_message " +
                "where time>=? and time<=? " + (uid != null ? "and uid=?" : "") +
                ") a group by time order by time asc";

        List<Object> args = new ArrayList<>();
        args.add(start);
        args.add(end);
        if (uid != null) {
            args.add(uid);
        }

        return tdTemplate.query(sql, new BeanPropertyRowMapper<>(TimeData.class), args.toArray());
    }

    @Override
    public void add(ThingModelMessage msg) {
        //使用deviceId作表名
        String sql = String.format("INSERT INTO %s (%s) USING %s TAGS ('%s') VALUES (%s);",
                "thing_model_message_" + msg.getDeviceId().toLowerCase(),
                "time,mid,product_key,device_name,uid,type,identifier,code,data,report_time",
                "thing_model_message",
                msg.getDeviceId(),
                "?,?,?,?,?,?,?,?,?,?"
        );
        tdTemplate.update(sql, msg.getOccurred(), msg.getMid(),
                msg.getProductKey(), msg.getDeviceName(),
                msg.getUid(), msg.getType(),
                msg.getIdentifier(), msg.getCode(),
                msg.getData() == null ? "{}" : JsonUtils.toJsonString(msg.getData()),
                msg.getTime());
    }

    @Override
    public long count() {
        List<Long> counts = tdTemplate.queryForList("select count(*) from thing_model_message", Long.class);
        return !counts.isEmpty() ? counts.get(0) : 0;
    }
}
