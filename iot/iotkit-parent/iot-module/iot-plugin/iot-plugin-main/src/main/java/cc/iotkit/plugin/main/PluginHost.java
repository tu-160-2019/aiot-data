package cc.iotkit.plugin.main;

import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.thing.ThingService;
import cc.iotkit.data.manager.IPluginInstanceData;
import cc.iotkit.model.plugin.PluginInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 插件接口调用入口
 *
 * @author sjg
 */
@Slf4j
@Service
public class PluginHost {

    @Autowired
    private IPluginInstanceData pluginInstanceData;

    @Autowired
    private DeviceRouter deviceRouter;

    @Autowired
    private IPluginMain pluginMain;

    public void invoke(ThingService<?> service) {
        log.info("start exec device service:{}", service);
        //根据设备获取插件路由
        PluginRouter router = deviceRouter.getRouter(service.getDeviceName());
        if (router == null) {
            throw new BizException(ErrCode.PLUGIN_ROUTER_NOT_FOUND);
        }

        //根据插件路由找到要调用的主程序
        PluginInstance instance = pluginInstanceData.findInstance(router.getMainId(), router.getPluginId());
        if (instance == null) {
            throw new BizException(ErrCode.PLUGIN_INSTANCE_NOT_FOUND);
        }

        //调用插件主程序接口
        pluginMain.invoke(service);
    }
}
