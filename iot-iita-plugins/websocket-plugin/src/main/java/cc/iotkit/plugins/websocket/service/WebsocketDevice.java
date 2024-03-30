package cc.iotkit.plugins.websocket.service;

import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.plugin.core.thing.IDevice;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.down.DeviceConfig;
import cc.iotkit.plugin.core.thing.actions.down.PropertyGet;
import cc.iotkit.plugin.core.thing.actions.down.PropertySet;
import cc.iotkit.plugin.core.thing.actions.down.ServiceInvoke;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * websocket设备服务
 *
 * @author tfd
 */
@Service
public class WebsocketDevice implements IDevice {

    @Autowired
    private WebsocketVerticle websocketVerticle;

    @Override
    public ActionResult config(DeviceConfig action) {
        return ActionResult.builder().code(0).reason("").build();
    }

    @Override
    public ActionResult propertyGet(PropertyGet action) {
        return send(
                action.getDeviceName(),
                new JsonObject()
                        .put("id", action.getId())
                        .put("method", "thing.service.property.get")
                        .put("params", action.getKeys())
                        .toString()
        );
    }

    @Override
    public ActionResult propertySet(PropertySet action) {
        return send(
                action.getDeviceName(),
                new JsonObject()
                        .put("id", action.getId())
                        .put("method", "thing.service.property.set")
                        .put("params", action.getParams())
                        .toString()
        );
    }

    @Override
    public ActionResult serviceInvoke(ServiceInvoke action) {
        return send(
                action.getDeviceName(),
                new JsonObject()
                        .put("id", action.getId())
                        .put("method", "thing.service." + action.getName())
                        .put("params", action.getParams())
                        .toString()
        );
    }

    private ActionResult send(String deviceName, String payload) {
        try {
            websocketVerticle.send(
                    deviceName,
                    payload
            );
            return ActionResult.builder().code(0).reason("").build();
        } catch (BizException e) {
            return ActionResult.builder().code(e.getCode()).reason(e.getMessage()).build();
        } catch (Exception e) {
            return ActionResult.builder().code(ErrCode.UNKNOWN_EXCEPTION.getKey()).reason(e.getMessage()).build();
        }
    }

}
