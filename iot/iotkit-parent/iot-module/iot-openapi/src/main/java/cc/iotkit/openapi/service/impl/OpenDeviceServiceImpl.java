package cc.iotkit.openapi.service.impl;

import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.utils.DeviceUtil;
import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.data.manager.IProductData;
import cc.iotkit.data.manager.IThingModelData;
import cc.iotkit.manager.service.DeviceCtrlService;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.product.Product;
import cc.iotkit.model.product.ThingModel;
import cc.iotkit.openapi.dto.bo.device.OpenapiDeviceBo;
import cc.iotkit.openapi.dto.vo.OpenDeviceInfoVo;
import cc.iotkit.openapi.dto.vo.OpenDevicePropertyVo;
import cc.iotkit.openapi.dto.vo.OpenPropertyVo;
import cc.iotkit.openapi.service.OpenDeviceService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class OpenDeviceServiceImpl implements OpenDeviceService {

    @Autowired
    @Qualifier("deviceInfoDataCache")
    private IDeviceInfoData deviceInfoData;

    @Autowired
    @Qualifier("productDataCache")
    private IProductData productData;

    @Autowired
    @Qualifier("thingModelDataCache")
    private IThingModelData thingModelData;

    @Autowired
    private DeviceCtrlService deviceCtrlService;

    @Override
    public DeviceInfo getDetail(OpenapiDeviceBo data) {
        DeviceInfo deviceInfo = deviceInfoData.findByDeviceName(data.getDeviceName());
        deviceInfo.setProperty(deviceInfoData.getProperties(deviceInfo.getDeviceId()));
        return deviceInfo;
    }

    @Override
    public OpenDeviceInfoVo addDevice(OpenapiDeviceBo bo) {
        String productKey = bo.getProductKey();
        String deviceName = bo.getDeviceName();
        String parentId = bo.getParentId();

        Product product = productData.findByProductKey(productKey);
        if (product == null) {
            throw new BizException(ErrCode.PRODUCT_NOT_FOUND);
        }
        //同产品不可重复设备名
        DeviceInfo deviceRepetition = deviceInfoData.findByDeviceName(deviceName);
        if (deviceRepetition != null) {
            throw new BizException(ErrCode.MODEL_DEVICE_ALREADY);
        }
        //生成设备密钥
        String chars = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678";
        int maxPos = chars.length();
        StringBuilder secret = new StringBuilder();
        for (var i = 0; i < 16; i++) {
            secret.append(chars.charAt((int) Math.floor(Math.random() * maxPos)));
        }

        DeviceInfo device = new DeviceInfo();
        device.setId(DeviceUtil.newDeviceId(deviceName));
        device.setUid(product.getUid());
        device.setDeviceId(device.getId());
        device.setProductKey(productKey);
        device.setDeviceName(deviceName);
        device.setSecret(secret.toString());
        device.setState(new DeviceInfo.State(false, null, null));
//        device.setLocate(new DeviceInfo.Locate(deviceInfo.getLongitude(),deviceInfo.getLatitude()));
        device.setCreateAt(System.currentTimeMillis());
        if (StringUtils.isNotBlank(parentId)) {
            device.setParentId(parentId);
        }
        deviceInfoData.save(device);
        return MapstructUtils.convert(device, OpenDeviceInfoVo.class);
    }

    @Override
    public boolean deleteDevice(OpenapiDeviceBo bo) {
        DeviceInfo deviceRepetition = deviceInfoData.findByDeviceName(bo.getDeviceName());
        if (deviceRepetition == null){
            throw new BizException(ErrCode.DEVICE_NOT_FOUND);
        }
        deviceInfoData.deleteById(deviceRepetition.getDeviceId());
        return true;
    }

    @Override
    public String setProperty(String productKey, String deviceName, String args) {
        DeviceInfo deviceRepetition = deviceInfoData.findByDeviceName(deviceName);
        if (deviceRepetition == null){
            throw new BizException(ErrCode.DEVICE_NOT_FOUND);
        }
        return deviceCtrlService.setProperty(deviceRepetition.getDeviceId(), JsonUtils.parseObject(args,Map.class), true);
    }

    @Override
    public OpenDevicePropertyVo getDevicePropertyStatus(OpenapiDeviceBo bo) {
        ThingModel thingModel = thingModelData.findByProductKey(bo.getProductKey());
        OpenDevicePropertyVo propertyVo = MapstructUtils.convert(thingModel, OpenDevicePropertyVo.class);
        DeviceInfo deviceInfo = deviceInfoData.findByDeviceName(bo.getDeviceName());
        List<OpenPropertyVo> openPropertyVos = new ArrayList<>();
        if (propertyVo != null){
            Map<String, ?> properties = deviceInfoData.getProperties(deviceInfo.getDeviceId());
            for (ThingModel.Property property : propertyVo.getModel().getProperties()) {
                OpenPropertyVo openPropertyVo = new OpenPropertyVo(property.getIdentifier(), property.getDataType(), property.getName(), property.getAccessMode(), property.getDescription(), property.getUnit());
                Map<String,Object> map = (Map<String, Object>) properties.get(openPropertyVo.getIdentifier());
                if (map != null){
                    openPropertyVo.setTime(String.valueOf(map.get("occurred")));
                    openPropertyVo.setValue(String.valueOf(map.get("value")));
                }
                openPropertyVos.add(openPropertyVo);
            }
            propertyVo.setProperty(openPropertyVos);
        }
        return propertyVo;
    }

}
