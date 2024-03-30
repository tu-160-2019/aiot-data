package cc.iotkit.plugins.http.service;

import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.IDeviceAction;
import cc.iotkit.plugin.core.thing.model.ThingDevice;
import cc.iotkit.plugin.core.thing.model.ThingProduct;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 测试服务
 *
 * @author sjg
 */
@Slf4j
public class FakeThingService implements IThingService {

    @Override
    public ActionResult post(String pluginId, IDeviceAction action) {
        log.info("post action:{}", action);
        return ActionResult.builder().code(0).build();
    }

    @Override
    public ThingProduct getProduct(String pk) {
        return ThingProduct.builder()
                .productKey("cGCrkK7Ex4FESAwe")
                .productSecret("xdkKUymrEGSCYWswqCvSPyRSFvH5j7CU")
                .build();
    }

    @Override
    public ThingDevice getDevice(String dn) {
        return ThingDevice.builder()
                .productKey("cGCrkK7Ex4FESAwe")
                .deviceName(dn)
                .secret("mBCr3TKstTj2KeM6")
                .build();
    }

    @Override
    public Map<String, ?> getProperty(String dn) {
        return new JsonObject().put("powerstate", 1).getMap();
    }
}
