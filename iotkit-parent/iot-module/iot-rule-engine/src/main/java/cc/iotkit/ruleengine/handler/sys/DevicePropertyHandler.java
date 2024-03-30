/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.ruleengine.handler.sys;

import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.data.manager.IThingModelData;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.message.DevicePropertyCache;
import cc.iotkit.model.product.ThingModel;
import cc.iotkit.ruleengine.handler.DeviceMessageHandler;
import cc.iotkit.temporal.IDevicePropertyData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备属性消息消费入库
 */
@Slf4j
@Component
public class DevicePropertyHandler implements DeviceMessageHandler {

    @Autowired
    private IDevicePropertyData devicePropertyData;
    @Autowired
    @Qualifier("deviceInfoDataCache")
    private IDeviceInfoData deviceInfoData;
    @Autowired
    @Qualifier("thingModelDataCache")
    private IThingModelData thingModelData;

    @Override
    public void handle(ThingModelMessage msg) {
        if (!ThingModelMessage.TYPE_PROPERTY.equals(msg.getType())) {
            return;
        }
        if (!ThingModelMessage.ID_PROPERTY_REPORT.equals(msg.getIdentifier())) {
            return;
        }

        if (!(msg.getData() instanceof Map)) {
            return;
        }

        Map<String, Object> properties = (Map<String, Object>) msg.getData();
        String deviceId = msg.getDeviceId();
        DeviceInfo deviceInfo = deviceInfoData.findByDeviceId(deviceId);
        if (deviceInfo == null) {
            return;
        }

        //物模型校验，过滤非物模型属性
        ThingModel thingModel = thingModelData.findByProductKey(deviceInfo.getProductKey());
        if (thingModel == null) {
            return;
        }

        //物模型属性
        Map<String, ThingModel.DataType> thingModelProperties = thingModel.getModel().
                getProperties().stream().collect(Collectors.toMap(
                ThingModel.Property::getIdentifier, ThingModel.Property::getDataType));

        Map<String, DevicePropertyCache> addProperties = new HashMap<>();
        Long occurred = msg.getOccurred();
        if (occurred == null) {
            occurred = System.currentTimeMillis();
        }

        //删除非属性字段
        Long finalOccurred = occurred;
        properties.forEach((key, val) -> {
            if (thingModelProperties.containsKey(key)) {
                DevicePropertyCache propertyCache = new DevicePropertyCache();
                propertyCache.setValue(val);
                propertyCache.setOccurred(finalOccurred);
                addProperties.put(key, propertyCache);
                handleLocate(deviceInfo, val, thingModelProperties.get(key));
            }
        });

        //更新设备当前属性
        updateDeviceCurrentProperties(deviceId, addProperties);

        //保存属性记录
        devicePropertyData.addProperties(deviceId, addProperties, occurred);
    }


    private void handleLocate(DeviceInfo deviceInfo, Object data, ThingModel.DataType dataType) {
        if (!"position".equals(dataType.getType())) {
            return;
        }
        //如果是定位属性需要做一些处理
        Object specs = dataType.getSpecs();
        String locateType = "";
        if (specs instanceof Map) {
            Object objlocateType = ((Map<?, ?>) specs).get("locateType");
            //定位方式
            if (objlocateType != null) {
                locateType = objlocateType.toString();
            }
            if (StringUtils.isBlank(locateType)) {
                return;
            }
            if ("lonLat".equals(locateType)) {
                //经纬度定位格式：经度,纬度
                String[] lonLats = data.toString().split(",");
                deviceInfo.getLocate().setLongitude(lonLats[0]);
                deviceInfo.getLocate().setLatitude(lonLats[1]);
                deviceInfoData.save(deviceInfo);
            } else if ("basestation".equals(locateType)) {
                //基站定位
            } else if ("ipinfo".equals(locateType)) {
                //ip定位
            }
        }
    }

    /**
     * 更新设备当前属性
     */
    private void updateDeviceCurrentProperties(String deviceId, Map<String, DevicePropertyCache> properties) {
        try {
            log.info("save device property,deviceId:{},property:{}", deviceId, JsonUtils.toJsonString(properties));
            deviceInfoData.saveProperties(deviceId, properties);
        } catch (Throwable e) {
            log.error("save device current properties error", e);
        }
    }
}
