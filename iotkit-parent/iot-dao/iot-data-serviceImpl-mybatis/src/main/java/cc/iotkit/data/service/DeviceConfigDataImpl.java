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
import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.manager.IDeviceConfigData;
import cc.iotkit.data.model.TbDeviceConfig;
import cc.iotkit.model.device.DeviceConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Primary
@Service
public class DeviceConfigDataImpl implements IDeviceConfigData, IJPACommData<DeviceConfig, String> {
//public class DeviceConfigDataImpl implements IDeviceConfigData, IJPACommData<DeviceConfig, String, TbDeviceConfig> {

    @Autowired
    private DeviceConfigService deviceConfigService;

    @Override
    public DeviceConfig findByDeviceName(String deviceName) {
        return MapstructUtils.convert(deviceConfigService.getOne(new LambdaQueryWrapper<TbDeviceConfig>().eq(TbDeviceConfig::getDeviceName, deviceName)), DeviceConfig.class);
    }

    @Override
    public DeviceConfig findByDeviceId(String deviceId) {
        return MapstructUtils.convert(deviceConfigService.findByDeviceId(deviceId), DeviceConfig.class);
    }

    @Override
    public DeviceConfigService getBaseRepository() {
        return deviceConfigService;
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
        deviceConfigService.save(MapstructUtils.convert(data, TbDeviceConfig.class));
        return data;
    }






}
