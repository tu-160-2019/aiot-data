/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.dao.DeviceConfigRepository;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.manager.IDeviceConfigData;
import cc.iotkit.data.model.TbDeviceConfig;
import cc.iotkit.model.device.DeviceConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Primary
@Service
public class DeviceConfigDataImpl implements IDeviceConfigData, IJPACommData<DeviceConfig, String> {

    @Autowired
    private DeviceConfigRepository deviceConfigRepository;

    @Override
    public DeviceConfig findByDeviceName(String deviceName) {
        return MapstructUtils.convert(deviceConfigRepository.findByDeviceName(deviceName), DeviceConfig.class);
    }

    @Override
    public DeviceConfig findByDeviceId(String deviceId) {
        return MapstructUtils.convert(deviceConfigRepository.findByDeviceId(deviceId), DeviceConfig.class);
    }

    @Override
    public JpaRepository getBaseRepository() {
        return deviceConfigRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbDeviceConfig.class;
    }

    @Override
    public Class getTClass() {
        return DeviceConfig.class;
    }


    @Override
    public DeviceConfig save(DeviceConfig data) {
        if (StringUtils.isBlank(data.getId())) {
            data.setId(UUID.randomUUID().toString());
        }
        deviceConfigRepository.save(MapstructUtils.convert(data, TbDeviceConfig.class));
        return data;
    }






}
