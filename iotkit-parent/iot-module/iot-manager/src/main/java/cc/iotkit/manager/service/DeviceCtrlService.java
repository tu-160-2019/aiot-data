/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.manager.service;

import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.thing.DeviceService;
import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.common.thing.ThingService;
import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.common.utils.UniqueIdUtil;
import cc.iotkit.data.manager.IDeviceConfigData;
import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.model.device.DeviceConfig;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.temporal.IThingModelMessageData;
import cc.iotkit.virtualdevice.VirtualManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static cc.iotkit.common.enums.ErrCode.DEVICE_NOT_FOUND;
import static cc.iotkit.common.enums.ErrCode.DEVICE_OFFLINE;

@Slf4j
@Service
public class DeviceCtrlService {

    @Autowired
    @Qualifier("deviceInfoDataCache")
    private IDeviceInfoData deviceInfoData;
    @Autowired
    private DataOwnerService dataOwnerService;
    @Autowired
    private ThingModelService thingModelService;
    @Autowired
    private IThingModelMessageData thingModelMessageData;
    @Autowired
    private VirtualManager virtualManager;
    @Autowired
    private IDeviceConfigData deviceConfigData;
    @Autowired
    private DeviceService deviceService;

    /**
     * 设备服务调用
     */
    public String invokeService(String deviceId, String service,
                                Map<String, Object> args) {
        return invokeService(deviceId, service, args, true);
    }

    /**
     * 设备服务调用
     */
    public String invokeService(String deviceId, String service,
                                Map<String, Object> args, boolean checkOwner) {
        DeviceInfo device = getAndCheckDevice(deviceId, checkOwner);

        return send(deviceId, device.getProductKey(), device.getDeviceName(),
                args, ThingModelMessage.TYPE_SERVICE, service);
    }

    public String otaUpgrade(String deviceId, boolean checkOwner, Object data) {
        DeviceInfo device = getAndCheckDevice(deviceId, checkOwner);
        return send(deviceId, device.getProductKey(), device.getDeviceName(),
                data, ThingModelMessage.TYPE_OTA, "ota");
    }

    /**
     * 设备属性获取
     */
    public String getProperty(String deviceId, List<String> properties,
                              boolean checkOwner) {
        DeviceInfo device = getAndCheckDevice(deviceId, checkOwner);

        return send(deviceId, device.getProductKey(), device.getDeviceName(), properties,
                ThingModelMessage.TYPE_PROPERTY, ThingModelMessage.ID_PROPERTY_GET);
    }

    /**
     * 设备属性设置
     */
    public String setProperty(String deviceId, Map<String, Object> properties) {
        return setProperty(deviceId, properties, true);
    }

    /**
     * 设备属性设置
     */
    public String setProperty(String deviceId, Map<String, Object> properties,
                              boolean checkOwner) {
        DeviceInfo device = getAndCheckDevice(deviceId, checkOwner);

        return send(deviceId, device.getProductKey(), device.getDeviceName(), properties,
                ThingModelMessage.TYPE_PROPERTY, ThingModelMessage.ID_PROPERTY_SET);
    }

    /**
     * 设备配置下发
     */
    public String sendConfig(String deviceId, boolean checkOwner) {
        DeviceInfo device = getAndCheckDevice(deviceId, checkOwner);

        DeviceConfig config = deviceConfigData.findByDeviceId(deviceId);
        Map data = JsonUtils.parseObject(config.getConfig(), Map.class);

        return send(deviceId, device.getProductKey(), device.getDeviceName(), data,
                ThingModelMessage.TYPE_CONFIG, ThingModelMessage.ID_CONFIG_SET);
    }

    /**
     * 设备配置下发
     */
    public String sendConfig(String deviceId) {
        return sendConfig(deviceId, true);
    }

    /**
     * 检查设备操作权限和状态
     */
    private DeviceInfo getAndCheckDevice(String deviceId, boolean checkOwner) {
        DeviceInfo device = deviceInfoData.findByDeviceId(deviceId);
        if (device == null) {
            throw new BizException(DEVICE_NOT_FOUND);
        }

        if (checkOwner) {
            dataOwnerService.checkOwner(device);
        }

        if (!device.getState().isOnline()) {
            throw new BizException(DEVICE_OFFLINE);
        }
        return device;
    }

    /**
     * 解绑子设备
     *
     * @param deviceId 子设备id
     */
    public void unbindDevice(String deviceId) {
        DeviceInfo device = deviceInfoData.findByDeviceId(deviceId);
        DeviceInfo parent = deviceInfoData.findByDeviceId(device.getParentId());

        try {
            //下发子设备注销给网关
            send(parent.getDeviceId(), parent.getProductKey(), parent.getDeviceName(),
                    Map.of(
                            "productKey", device.getProductKey(),
                            "deviceName", device.getDeviceName()
                    ),
                    ThingModelMessage.TYPE_LIFETIME, ThingModelMessage.ID_DEREGISTER);
        } catch (Throwable e) {
            log.error("send {} message error", ThingModelMessage.ID_DEREGISTER, e);
        }

        //清除设备的父级id，不管是否发送成功都需要清除父级id
        device.setParentId("");
        deviceInfoData.save(device);
    }

    /**
     * 数据下发
     */
    private String send(String deviceId, String pk, String dn,
                        Object data, String type, String identifier) {
        ThingService<Object> thingService = ThingService.builder()
                .mid(UniqueIdUtil.newRequestId())
                .productKey(pk)
                .deviceName(dn)
                .type(type)
                .identifier(identifier)
                .params(data)
                .build();
        if (!ThingModelMessage.TYPE_CONFIG.equals(type)
                && !ThingModelMessage.TYPE_LIFETIME.equals(type)
                && !ThingModelMessage.TYPE_OTA.equals(type)
        ) {
            //非配置非OTA且非生命周期下发需要做物模型转换
            thingModelService.parseParams(thingService);
        }

        if (virtualManager.isVirtual(deviceId)) {
            //虚拟设备指令下发
            virtualManager.send(thingService);
        } else {
            //设备指令下发
            deviceService.invoke(thingService);
        }
        return thingService.getMid();
    }

}
