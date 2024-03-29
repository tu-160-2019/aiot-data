/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.cache;

import cc.iotkit.common.constant.Constants;
import cc.iotkit.model.device.DeviceInfo;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeviceInfoCachePut {

    @CachePut(value = Constants.CACHE_DEVICE_INFO, key = "#root.method.name+#deviceId")
    public DeviceInfo findByDeviceId(String deviceId, DeviceInfo deviceInfo) {
        return deviceInfo;
    }

    @CachePut(value = Constants.CACHE_DEVICE_INFO, key = "#root.method.name+#deviceName")
    public DeviceInfo findByDeviceName(String deviceName, DeviceInfo deviceInfo) {
        return deviceInfo;
    }

    @CachePut(value = Constants.CACHE_DEVICE_INFO, key = "#root.method.name+#parentId")
    public List<String> findSubDeviceIds(String parentId, List<String> subDeviceIds) {
        return subDeviceIds;
    }

}
