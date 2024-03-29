package cc.iotkit.plugins.dlt645.service;

import cc.iotkit.plugin.core.thing.IDevice;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.down.DeviceConfig;
import cc.iotkit.plugin.core.thing.actions.down.PropertyGet;
import cc.iotkit.plugin.core.thing.actions.down.PropertySet;
import cc.iotkit.plugin.core.thing.actions.down.ServiceInvoke;
import cc.iotkit.plugins.dlt645.analysis.DLT645Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author：tfd
 * @Date：2023/12/14 16:22
 */
@Service
public class DLT645Device implements IDevice {

    @Autowired
    private TcpClientVerticle dlt645Verticle;

    @Override
    public ActionResult config(DeviceConfig action) {
        return ActionResult.builder().code(0).reason("").build();
    }

    @Override
    public ActionResult propertyGet(PropertyGet action) {
        for (String key:action.getKeys()){
            String msg=DLT645Converter.packData(action.getDeviceName(),"读数据",key.replaceFirst("p",""));
            dlt645Verticle.sendMsg(msg);
        }
        return ActionResult.builder().code(0).reason("success").build();
    }

    @Override
    public ActionResult propertySet(PropertySet action) {
        return null;
    }

    @Override
    public ActionResult serviceInvoke(ServiceInvoke action) {
        return null;
    }
}
