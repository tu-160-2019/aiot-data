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

import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.message.DeviceProperty;
import cc.iotkit.model.device.message.DevicePropertyCache;
import cc.iotkit.temporal.IDevicePropertyData;
import cc.iotkit.temporal.td.config.Constants;
import cc.iotkit.temporal.td.dao.TdTemplate;
import cc.iotkit.temporal.td.model.TbDeviceProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DevicePropertyDataImpl implements IDevicePropertyData {

    @Autowired
    private TdTemplate tdTemplate;
    @Autowired
    @Qualifier("deviceInfoDataCache")
    private IDeviceInfoData deviceInfoData;

    @Override
    public List<DeviceProperty> findDevicePropertyHistory(String deviceId, String name, long start, long end, int size) {
        DeviceInfo device = deviceInfoData.findByDeviceId(deviceId);
        if (device == null) {
            return new ArrayList<>();
        }

        String tbName = Constants.getProductPropertySTableName(device.getProductKey());
        List<TbDeviceProperty> deviceProperties = tdTemplate.query(String.format(
                "select time,%s as `value`,device_id from %s where device_id=? and time>=? and time<=? " +
                        "order by time asc limit 0," + size,
                name.toLowerCase(), tbName),
                new BeanPropertyRowMapper<>(TbDeviceProperty.class),
                deviceId, start, end
        );
        return deviceProperties.stream().map(p -> new DeviceProperty(
                p.getTime().toString(),
                p.getDeviceId(),
                name,
                p.getValue(),
                p.getTime()))
                .collect(Collectors.toList());
    }

    @Override
    public void addProperties(String deviceId, Map<String, DevicePropertyCache> properties, long time) {
        DeviceInfo device = deviceInfoData.findByDeviceId(deviceId);
        if (device == null) {
            return;
        }
        //获取设备旧属性
        Map<String, DevicePropertyCache> oldProperties = deviceInfoData.getProperties(deviceId);
        //用新属性覆盖
        oldProperties.putAll(properties);

        StringBuilder sbFieldNames = new StringBuilder();
        StringBuilder sbFieldPlaces = new StringBuilder();
        List<Object> args = new ArrayList<>();
        args.add(time);

        //组织sql
        oldProperties.forEach((key, val) -> {
            sbFieldNames.append(key)
                    .append(",");
            sbFieldPlaces.append("?,");
            args.add(val.getValue());
        });
        sbFieldNames.deleteCharAt(sbFieldNames.length() - 1);
        sbFieldPlaces.deleteCharAt(sbFieldPlaces.length() - 1);

        String sql = String.format("INSERT INTO %s (time,%s) USING %s TAGS ('%s') VALUES (?,%s);",
                Constants.getDevicePropertyTableName(deviceId),
                sbFieldNames.toString(),
                Constants.getProductPropertySTableName(device.getProductKey()),
                deviceId,
                sbFieldPlaces.toString());

        tdTemplate.update(sql, args.toArray());
    }

}
