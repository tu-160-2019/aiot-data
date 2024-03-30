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

import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.message.DeviceProperty;
import cc.iotkit.model.device.message.DevicePropertyCache;
import cc.iotkit.temporal.IDevicePropertyData;
import cc.iotkit.temporal.iotdb.dao.IotDbTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DevicePropertyDataImpl implements IDevicePropertyData {

    @Autowired
    @Qualifier("deviceInfoDataCache")
    private IDeviceInfoData deviceInfoData;

    @Autowired
    private IotDbTemplate dbTemplate;

    @Override
    public List<DeviceProperty> findDevicePropertyHistory(String deviceId, String name, long start, long end, int size) {
        DeviceInfo device = deviceInfoData.findByDeviceId(deviceId);
        if (device == null) {
            return new ArrayList<>();
        }
        List<DeviceProperty> list=new ArrayList<>();
        List<Map<String, Object>> records = dbTemplate.query(device.getProductKey(), deviceId, start, end);
        int i=0;
        for (Map<String, Object> record : records) {
            Object val = record.get(name);
            list.add(new DeviceProperty(String.valueOf(++i),deviceId,name,val, (Long) record.get("time")));
        }

        return list;
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
        Map<String, Object> data = new HashMap<>(oldProperties.size());
        oldProperties.forEach((k, v) -> data.put(k, v.getValue()));
        //添加对齐序列
        dbTemplate.insert(device.getProductKey(), deviceId, System.currentTimeMillis(), data);
    }

}
