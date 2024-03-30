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
import cc.iotkit.model.device.VirtualDeviceLog;
import cc.iotkit.temporal.IVirtualDeviceLogData;
import cc.iotkit.temporal.td.dao.TdTemplate;
import cc.iotkit.temporal.td.model.TbVirtualDeviceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VirtualDeviceLogDataImpl implements IVirtualDeviceLogData {

    @Autowired
    private TdTemplate tdTemplate;

    @Override
    public Paging<VirtualDeviceLog> findByVirtualDeviceId(String virtualDeviceId, int page, int size) {
        String sql = "select time,virtual_device_id,virtual_device_name,device_total,result from virtual_device_log_%s order by time desc limit %d offset %d";
        sql = String.format(sql, virtualDeviceId.toLowerCase(), size, (page - 1) * size);
        List<TbVirtualDeviceLog> logs = tdTemplate.query(sql, new BeanPropertyRowMapper<>(TbVirtualDeviceLog.class));

        sql = "select count(*) from virtual_device_log_" + virtualDeviceId.toLowerCase();
        List<Long> counts = tdTemplate.queryForList(sql, Long.class);

        return new Paging<>(!counts.isEmpty() ? counts.get(0) : 0, logs.stream().map(r ->
                new VirtualDeviceLog(r.getTime().toString(), virtualDeviceId,
                        r.getVirtualDeviceName(),
                        r.getDeviceTotal(), r.getResult(),
                        r.getTime()))
                .collect(Collectors.toList()));
    }

    @Override
    public void add(VirtualDeviceLog log) {
        //使用virtualDeviceId作表名
        String sql = String.format("INSERT INTO %s (%s) USING %s TAGS ('%s') VALUES (%s);",
                "virtual_device_log_" + log.getVirtualDeviceId().toLowerCase(),
                "time,virtual_device_name,device_total,result",
                "virtual_device_log",
                log.getVirtualDeviceId(),
                "?,?,?,?"
        );
        tdTemplate.update(sql, System.currentTimeMillis(), log.getVirtualDeviceName(),
                log.getDeviceTotal(), log.getResult());
    }
}
