package cc.iotkit.plugin.main;

import cc.iotkit.common.constant.Constants;
import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.common.utils.DeviceUtil;
import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.common.utils.UniqueIdUtil;
import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.data.manager.IProductData;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.product.Product;
import cc.iotkit.mq.MqProducer;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.*;
import cc.iotkit.plugin.core.thing.actions.up.*;
import cc.iotkit.plugin.core.thing.model.ThingDevice;
import cc.iotkit.plugin.core.thing.model.ThingProduct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author sjg
 */
@Slf4j
@Service
public class ThingServiceImpl implements IThingService {

    @Autowired
    @Qualifier("productDataCache")
    private IProductData productData;

    @Autowired
    @Qualifier("deviceInfoDataCache")
    private IDeviceInfoData deviceInfoData;

    @Autowired
    private MqProducer<ThingModelMessage> producer;

    @Autowired
    private DeviceRouter deviceRouter;

    @Override
    public ActionResult post(String pluginId, IDeviceAction action) {
        try {
            log.info("receive plugin:{}, action:{}", pluginId, action);
            String deviceName = action.getDeviceName();

            //添加设备路由
            deviceRouter.putRouter(deviceName, new PluginRouter(IPluginMain.MAIN_ID, pluginId));

            DeviceInfo device = getDeviceInfo(deviceName);
            if (device == null) {
                log.warn("device:{} is not found.", deviceName);
            }

            ActionType type = action.getType();
            switch (type) {
                case REGISTER:
                    //设备注册
                    registerDevice(device, (DeviceRegister) action, null);
                    break;
                case SUB_REGISTER:
                    //子设备注册
                    subRegisterDevice(pluginId, device, (SubDeviceRegister) action);
                    break;
                case STATE_CHANGE:
                    deviceStateChange(device, (DeviceStateChange) action);
                    break;
                case EVENT_REPORT:
                    EventReport eventReport = (EventReport) action;
                    publishMsg(
                            device, action,
                            ThingModelMessage.builder()
                                    .type(ThingModelMessage.TYPE_EVENT)
                                    .identifier(eventReport.getName())
                                    .data(eventReport.getParams())
                                    .time(eventReport.getTime())
                                    .build()
                    );
                    break;
                case PROPERTY_REPORT:
                    PropertyReport propertyReport = (PropertyReport) action;
                    publishMsg(
                            device, action,
                            ThingModelMessage.builder()
                                    .type(ThingModelMessage.TYPE_PROPERTY)
                                    .identifier(ThingModelMessage.ID_PROPERTY_REPORT)
                                    .data(propertyReport.getParams())
                                    .time(propertyReport.getTime())
                                    .occurred(propertyReport.getTime())
                                    .build()
                    );
                    break;
                case SERVICE_REPLY:
                    ServiceReply serviceReply = (ServiceReply) action;
                    publishMsg(
                            device, action,
                            ThingModelMessage.builder()
                                    .type(ThingModelMessage.TYPE_SERVICE)
                                    .identifier(serviceReply.getName() + "_reply")
                                    .mid(serviceReply.getReplyId())
                                    .code(serviceReply.getCode())
                                    .data(serviceReply.getParams())
                                    .time(serviceReply.getTime())
                                    .build()
                    );
                    break;

                case TOPOLOGY:
                    deviceTopologyUpdate(device, (DeviceTopology) action);
                    break;
                default:
                    return ActionResult.builder().code(ErrCode.PARAMS_EXCEPTION.getKey()).build();
            }

            return new ActionResult();
        } catch (Throwable e) {
            log.error("action process error", e);
            return ActionResult.builder().code(1).reason(e.getMessage()).build();
        }
    }

    @Override
    public ThingProduct getProduct(String pk) {
        try {
            Product product = productData.findByProductKey(pk);
            if (product == null) {
                return null;
            }
            return ThingProduct.builder()
                    .category(product.getCategory())
                    .productKey(product.getProductKey())
                    .name(product.getName())
                    .nodeType(product.getNodeType())
                    .productSecret(product.getProductSecret())
                    .build();
        } catch (Throwable e) {
            log.error("get product error", e);
            return null;
        }
    }

    public DeviceInfo getDeviceInfo(String dn) {
        try {
            return deviceInfoData.findByDeviceName(dn);
        } catch (Throwable e) {
            log.error("get device error", e);
            return null;
        }
    }

    @Override
    public ThingDevice getDevice(String dn) {
        DeviceInfo deviceInfo = getDeviceInfo(dn);
        if (deviceInfo == null) {
            return null;
        }
        return ThingDevice.builder()
                .deviceId(deviceInfo.getDeviceId())
                .deviceName(deviceInfo.getDeviceName())
                .model(deviceInfo.getModel())
                .productKey(deviceInfo.getProductKey())
                .secret(deviceInfo.getSecret())
                .build();
    }

    @Override
    public Map<String, ?> getProperty(String deviceName) {
        DeviceInfo device = getDeviceInfo(deviceName);
        if (device == null) {
            return new HashMap<>(0);
        }
        return device.getProperty();
    }

    private String registerDevice(DeviceInfo device, DeviceRegister register, String parentId) {
        String productKey = register.getProductKey();
        //指定了pk需验证
        if (StringUtils.isNotBlank(productKey)) {
            ThingProduct product = getProduct(productKey);
            if (product == null) {
                throw new BizException(ErrCode.PRODUCT_NOT_FOUND);
            }
        }

        if (device != null) {
            log.info("device already registered");
            if (parentId != null) {
                device.setParentId(parentId);
                deviceInfoData.save(device);
            }
            return device.getDeviceId();
        } else {
            //不存在,注册新设备
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setId(DeviceUtil.newDeviceId(register.getDeviceName()));
            deviceInfo.setDeviceId(deviceInfo.getId());
            deviceInfo.setProductKey(productKey);
            deviceInfo.setDeviceName(register.getDeviceName());
            deviceInfo.setModel(register.getModel());
            deviceInfo.setParentId(parentId);
            deviceInfo.setSecret(RandomStringUtils.randomAlphabetic(16));
            //默认离线
            deviceInfo.setState(new DeviceInfo.State(false, null, null));
            deviceInfo.setCreateAt(System.currentTimeMillis());
            deviceInfoData.save(deviceInfo);

            log.info("device registered:{}", JsonUtils.toJsonString(device));
            publishMsg(
                    deviceInfo,
                    register,
                    ThingModelMessage.builder()
                            .type(ThingModelMessage.TYPE_LIFETIME)
                            .identifier("register")
                            .time(System.currentTimeMillis())
                            .build()
            );
            return deviceInfo.getDeviceId();
        }
    }

    private void subRegisterDevice(String pluginId, DeviceInfo parent, SubDeviceRegister register) {
        //先注册父设备
        String parentId = registerDevice(parent,
                DeviceRegister.builder()
                        .productKey(register.getProductKey())
                        .deviceName(register.getDeviceName())
                        .model(register.getModel())
                        .version(register.getVersion())
                        .build(), null);

        for (DeviceRegister sub : register.getSubs()) {
            String productKey = sub.getProductKey();
            String deviceName = sub.getDeviceName();
            DeviceInfo subDevice = getDeviceInfo(sub.getDeviceName());
            //添加设备路由
            deviceRouter.putRouter(deviceName, new PluginRouter(IPluginMain.MAIN_ID, pluginId));
            //注册子设备
            registerDevice(subDevice,
                    DeviceRegister.builder()
                            .productKey(productKey)
                            .deviceName(deviceName)
                            .model(sub.getModel())
                            .version(sub.getVersion())
                            .build()
                    , parentId
            );
        }
    }

    private void deviceStateChange(DeviceInfo device, DeviceStateChange action) {
        DeviceState state = action.getState();
        if (state == DeviceState.ONLINE) {
            device.getState().setOnline(true);
            device.getState().setOnlineTime(System.currentTimeMillis());
        } else {
            device.getState().setOnline(false);
            device.getState().setOfflineTime(System.currentTimeMillis());
        }
        deviceInfoData.save(device);

        publishMsg(
                device, action,
                ThingModelMessage.builder()
                        .type(ThingModelMessage.TYPE_STATE)
                        .identifier(action.getState().getState())
                        .time(System.currentTimeMillis())
                        .build()
        );
    }

    private void deviceTopologyUpdate(DeviceInfo device, DeviceTopology topology) {
        //设备拓扑关系更新
        for (String deviceName : topology.getSubDevices()) {
            DeviceInfo subDevice = getDeviceInfo(deviceName);
            subDevice.setParentId(device.getDeviceId());
            deviceInfoData.save(subDevice);
        }
    }

    private void publishMsg(DeviceInfo device, IDeviceAction action, ThingModelMessage message) {
        try {
            message.setId(UUID.randomUUID().toString());
            message.setMid(UniqueIdUtil.newRequestId());
            message.setDeviceId(device.getDeviceId());
            message.setProductKey(device.getProductKey());
            message.setDeviceName(device.getDeviceName());
            message.setUid(device.getUid());
            if (message.getOccurred() == null) {
                message.setOccurred(action.getTime());
            }
            if (message.getTime() == null) {
                message.setTime(System.currentTimeMillis());
            }
            if (message.getData() == null) {
                message.setData(new HashMap<>(0));
            }

            log.info("publish thingModel msg:{}", message);
            producer.publish(Constants.THING_MODEL_MESSAGE_TOPIC, message);
        } catch (Throwable e) {
            log.error("send thing model message error", e);
        }
    }

}
