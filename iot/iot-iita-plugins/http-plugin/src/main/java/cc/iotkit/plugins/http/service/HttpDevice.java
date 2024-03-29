package cc.iotkit.plugins.http.service;

import cc.iotkit.plugin.core.thing.IDevice;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.down.DeviceConfig;
import cc.iotkit.plugin.core.thing.actions.down.PropertyGet;
import cc.iotkit.plugin.core.thing.actions.down.PropertySet;
import cc.iotkit.plugin.core.thing.actions.down.ServiceInvoke;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * http设备下行接口
 *
 * @author sjg
 */
@Service
public class HttpDevice implements IDevice {

    @Autowired
    private HttpVerticle httpVerticle;

    @Override
    public ActionResult config(DeviceConfig action) {
        return ActionResult.builder().code(0).reason("").build();
    }

    @Override
    public ActionResult propertyGet(PropertyGet action) {
        throw new UnsupportedOperationException("不支持该功能");
    }

    @Override
    public ActionResult propertySet(PropertySet action) {
        throw new UnsupportedOperationException("不支持该功能");
    }

    @Override
    public ActionResult serviceInvoke(ServiceInvoke action) {
        throw new UnsupportedOperationException("不支持该功能");
    }

}
