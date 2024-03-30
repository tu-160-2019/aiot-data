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

import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.message.DeviceProperty;
import cc.iotkit.temporal.IDevicePropertyData;
import cc.iotkit.temporal.ts.config.Constants;
import cc.iotkit.temporal.ts.dao.TsTemplate;
import cc.iotkit.temporal.ts.model.TsDeviceProperty;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.InsertValuesStepN;
import org.jooq.Record;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.field;

@Slf4j
@Service
public class DevicePropertyDataImpl implements IDevicePropertyData {

    @Autowired
    private TsTemplate tsTemplate;
    @Autowired
    @Qualifier("deviceInfoDataCache")
    private IDeviceInfoData deviceInfoData;

    @Override
    public List<DeviceProperty> findDevicePropertyHistory(String deviceId, String name, long start, long end) {
        DeviceInfo device = deviceInfoData.findByDeviceId(deviceId);

        String tbName = Constants.getProductPropertySTableName(device.getProductKey());
        Condition con = field("time").greaterOrEqual(new Date(start)).and(field("time").lessOrEqual(new Date(end)))
                .and(DSL.field("device_id").eq(deviceId));
        String sql = DSL.select(DSL.field("time"), DSL.field("device_id"), DSL.field(name.toLowerCase()).as("value"))
                .from(tbName).where(con)
                .getSQL(ParamType.INLINED);


        List<TsDeviceProperty> list = tsTemplate.query(sql, new BeanPropertyRowMapper<>(TsDeviceProperty.class));


        return list.stream().map(
                o->{
                    DeviceProperty deviceProperty = new DeviceProperty();
                    BeanUtils.copyProperties(o,deviceProperty);
                    deviceProperty.setTime(o.getTime().getTime());
                    return deviceProperty;
                }
        ).collect(Collectors.toList());

    }

    @Override
    public void addProperties(String deviceId, Map<String, Object> properties, long time) {
        DeviceInfo device = deviceInfoData.findByDeviceId(deviceId);
        if (device == null) {
            return;
        }
        //获取设备旧属性
        Map<String, Object> oldProperties = deviceInfoData.getProperties(deviceId);
        //用新属性覆盖
        oldProperties.putAll(properties);

        List<Field<Object>> fields = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        fields.add(DSL.field("time"));
        fields.add(DSL.field("device_id"));
        values.add(new Date(time));
        values.add(deviceId);
        //组织sql
        oldProperties.forEach((key, val) -> {
            fields.add(DSL.field(key));
            values.add(val);
        });
        String tbName = Constants.getProductPropertySTableName(device.getProductKey());

        //组织sql
        InsertValuesStepN<Record> step = DSL.insertInto(DSL.table(tbName), (Collection<? extends Field<Object>>) fields).values(values);
        String sql = step.getSQL(ParamType.INLINED);
        tsTemplate.batchUpdate(sql);

    }

}
