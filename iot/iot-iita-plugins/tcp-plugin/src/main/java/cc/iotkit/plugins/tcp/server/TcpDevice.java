package cc.iotkit.plugins.tcp.server;

import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.plugin.core.thing.IDevice;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.down.DeviceConfig;
import cc.iotkit.plugin.core.thing.actions.down.PropertyGet;
import cc.iotkit.plugin.core.thing.actions.down.PropertySet;
import cc.iotkit.plugin.core.thing.actions.down.ServiceInvoke;
import cc.iotkit.plugins.tcp.parser.DataEncoder;
import cc.iotkit.plugins.tcp.parser.DataPackage;
import cc.iotkit.script.IScriptEngine;
import cn.hutool.core.util.HexUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.buffer.Buffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * tcp设备下行接口
 *
 * @author sjg
 */
@Service
public class TcpDevice implements IDevice {

    @Autowired
    private TcpServerVerticle tcpServerVerticle;

    private final AtomicInteger atMid = new AtomicInteger(0);

    @Override
    public ActionResult config(DeviceConfig action) {
        return ActionResult.builder().code(0).reason("").build();
    }

    @Override
    public ActionResult propertyGet(PropertyGet action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ActionResult propertySet(PropertySet action) {
        IScriptEngine scriptEngine = tcpServerVerticle.getScriptEngine();
        //使用转换脚本转换参数部分内容
        String payload = scriptEngine.invokeMethod(new TypeReference<>() {
        }, "encode", action.getParams());

        if (payload == null) {
            return ActionResult.builder().code(ErrCode.MSG_CONVERT_ERROR.getKey()).build();
        }

        if (atMid.compareAndSet(Short.MAX_VALUE / 2 - 1, 0)) {
            atMid.set(0);
        }
        byte[] bytes = HexUtil.decodeHex(payload);

        //构造数据包
        DataPackage data = DataPackage.builder()
                .addr(action.getDeviceName())
                .code(DataPackage.CODE_DATA_DOWN)
                .mid((short) atMid.getAndIncrement())
                .payload(bytes)
                .build();

        return send(action.getDeviceName(), DataEncoder.encode(data));
    }

    @Override
    public ActionResult serviceInvoke(ServiceInvoke action) {
        throw new UnsupportedOperationException();
    }

    private ActionResult send(String deviceName, Buffer msg) {
        try {
            tcpServerVerticle.sendMsg(deviceName, msg);
            return ActionResult.builder().code(0).reason("").build();
        } catch (BizException e) {
            return ActionResult.builder().code(e.getCode()).reason(e.getMessage()).build();
        } catch (Exception e) {
            return ActionResult.builder().code(ErrCode.UNKNOWN_EXCEPTION.getKey()).reason(e.getMessage()).build();
        }
    }

}
