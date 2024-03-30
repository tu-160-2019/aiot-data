/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.plugin.main;

import cc.iotkit.common.redis.utils.RedisUtils;
import org.springframework.stereotype.Component;

/**
 * 设备路由
 *
 * @author sjg
 */
@Component
public class DeviceRouter {

    private static final String DEVICE_ROUTER = "str:device:router:%s";

    private String getDeviceRouter(String deviceName) {
        return String.format(DEVICE_ROUTER, deviceName);
    }

    public void putRouter(String deviceName, PluginRouter pluginRouter) {
        RedisUtils.setCacheObject(getDeviceRouter(deviceName), pluginRouter);
    }

    public void removeRouter(String deviceName) {
        RedisUtils.deleteObject(getDeviceRouter(deviceName));
    }

    public PluginRouter getRouter(String deviceName) {
        return RedisUtils.getCacheObject(getDeviceRouter(deviceName));
    }

}
