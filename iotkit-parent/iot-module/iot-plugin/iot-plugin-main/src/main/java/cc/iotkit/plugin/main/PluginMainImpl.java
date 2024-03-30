package cc.iotkit.plugin.main;

import cc.iotkit.common.constant.Constants;
import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.thing.DeviceService;
import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.common.thing.ThingService;
import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.data.manager.IPluginInfoData;
import cc.iotkit.data.manager.IProductData;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.plugin.PluginInfo;
import cc.iotkit.model.product.Product;
import cc.iotkit.mq.MqProducer;
import cc.iotkit.plugin.core.thing.IDevice;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.down.PropertyGet;
import cc.iotkit.plugin.core.thing.actions.down.PropertySet;
import cc.iotkit.plugin.core.thing.actions.down.ServiceInvoke;
import cc.iotkit.plugin.main.script.PluginScriptServer;
import cc.iotkit.script.IScriptEngine;
import com.gitee.starblues.integration.user.PluginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 插件主程序接口服务
 *
 * @author sjg
 */
@Slf4j
@Service
public class PluginMainImpl implements IPluginMain, DeviceService {

    @Autowired
    private IPluginInfoData pluginInfoData;

    @Autowired
    @Qualifier("productDataCache")
    IProductData productData;

    @Autowired
    @Qualifier("deviceInfoDataCache")
    private IDeviceInfoData deviceInfoData;

    @Autowired
    private DeviceRouter deviceRouter;

    @Autowired
    private PluginUser pluginUser;

    @Autowired
    private IThingService thingService;

    @Autowired
    private MqProducer<ThingModelMessage> producer;

    @Autowired
    private PluginScriptServer pluginScriptServer;

    @Override
    public IScriptEngine getScriptEngine(String pluginId) {
        return pluginScriptServer.getScriptEngine(pluginId);
    }

    @Override
    public void reloadScript(String pluginId) {
    }

    @Override
    public void invoke(@RequestBody ThingService<?> service) {
        log.info("start exec device service:{}", service);
        DeviceInfo deviceInfo = deviceInfoData.findByDeviceName(service.getDeviceName());
        Product product = productData.findByProductKey(service.getProductKey());
        String linkPk = service.getProductKey();
        String linkDn = service.getDeviceName();

        if (product.isTransparent()) {
            //如果是透传设备，取父级设备进行链路查找
            DeviceInfo parent = deviceInfoData.findByDeviceId(deviceInfo.getParentId());
            if(parent==null){
                throw new BizException(ErrCode.DEVICE_NOT_FOUND,"未找到父级设备");
            }
            linkPk = parent.getProductKey();
            linkDn = parent.getDeviceName();
        }

        PluginRouter router = deviceRouter.getRouter(service.getDeviceName());
        if (router == null) {
            throw new BizException(ErrCode.PLUGIN_ROUTER_NOT_FOUND);
        }

        //获取插件中的设备服务接口
        List<IDevice> deviceServices = pluginUser.getBeanByInterface(router.getPluginId(), IDevice.class);
        if (deviceServices.isEmpty()) {
            throw new BizException(ErrCode.PLUGIN_SERVICE_NOT_FOUND);
        }

        IDevice deviceService = deviceServices.get(0);
        String type = service.getType();
        String identifier = service.getIdentifier();
        ActionResult result = null;

        if (ThingService.TYPE_SERVICE.equals(type)) {
            if (!(service.getParams() instanceof Map)) {
                throw new BizException(ErrCode.PARAMS_EXCEPTION);
            }
            Map<String, ?> params = (Map<String, ?>) service.getParams();
            //服务调用
            ServiceInvoke action = ServiceInvoke.builder()
                    .id(service.getMid())
                    .productKey(linkPk)
                    .deviceName(linkDn)
                    .name(service.getIdentifier())
                    .params(params)
                    .build();
            //调用插件设备服务接口
            result = deviceService.serviceInvoke(action);
            publish(service, deviceInfo.getDeviceId(), result.getCode());
        } else if (ThingService.TYPE_PROPERTY.equals(type)) {
            if ("set".equals(identifier)) {
                if (!(service.getParams() instanceof Map)) {
                    throw new BizException(ErrCode.PARAMS_EXCEPTION);
                }
                Map<String, ?> params = (Map<String, ?>) service.getParams();
                //属性设置
                PropertySet action = PropertySet.builder()
                        .id(service.getMid())
                        .productKey(linkPk)
                        .deviceName(linkDn)
                        .params(params)
                        .build();
                //调用插件设备服务接口
                result = deviceService.propertySet(action);
                publish(service, deviceInfo.getDeviceId(), result.getCode());
            } else if ("get".equals(identifier)) {
                //属性获取
                PropertyGet action = PropertyGet.builder()
                        .id(service.getMid())
                        .productKey(linkPk)
                        .deviceName(linkDn)
                        .keys((List<String>) service.getParams())
                        .build();
                //调用插件设备服务接口
                result = deviceService.propertyGet(action);
                publish(service, deviceInfo.getDeviceId(), result.getCode());
            }
        }

        if (result == null || result.getCode() != 0) {
            throw new BizException(ErrCode.DEVICE_ACTION_FAILED, result == null ? "" : result.getReason());
        }
    }

    private void publish(ThingService<?> service, String deviceId, int code) {
        //产生下发消息作为下行日志保存
        ThingModelMessage message = ThingModelMessage.builder()
                .id(UUID.randomUUID().toString())
                .deviceId(deviceId)
                .mid(service.getMid())
                .productKey(service.getProductKey())
                .deviceName(service.getDeviceName())
                .identifier(service.getIdentifier())
                .type(service.getType())
                .data(service.getParams())
                .code(code)
                .occurred(System.currentTimeMillis())
                .time(System.currentTimeMillis())
                .build();

        producer.publish(Constants.THING_MODEL_MESSAGE_TOPIC, message);
    }

    @Override
    public Map<String, Object> getConfig(String pluginId) {
        //获取系统插件配置项
        PluginInfo plugin = pluginInfoData.findByPluginId(pluginId);
        if (plugin == null) {
            return new HashMap<>(0);
        }
        return JsonUtils.parseObject(plugin.getConfig(), Map.class);
    }
}
