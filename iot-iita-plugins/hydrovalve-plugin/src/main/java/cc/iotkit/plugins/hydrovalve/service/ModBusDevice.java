package cc.iotkit.plugins.hydrovalve.service;

import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.plugin.core.thing.IDevice;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.down.DeviceConfig;
import cc.iotkit.plugin.core.thing.actions.down.PropertyGet;
import cc.iotkit.plugin.core.thing.actions.down.PropertySet;
import cc.iotkit.plugin.core.thing.actions.down.ServiceInvoke;
import cc.iotkit.plugins.hydrovalve.analysis.ModBusConstants;
import cc.iotkit.plugins.hydrovalve.analysis.ModBusEntity;
import cc.iotkit.plugins.hydrovalve.analysis.ModBusRtuAnalysis;
import cc.iotkit.plugins.hydrovalve.utils.ByteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author：tfd
 * @Date：2024/1/10 11:06
 */
@Service
public class ModBusDevice implements IDevice {

    @Autowired
    private ModbusVerticle modbusVerticle;

    ModBusRtuAnalysis analysis=new ModBusRtuAnalysis();

    @Override
    public ActionResult config(DeviceConfig action) {
        return ActionResult.builder().code(0).reason("").build();
    }

    @Override
    public ActionResult propertyGet(PropertyGet action) {
        return null;
    }

    @Override
    public ActionResult propertySet(PropertySet action) {
        ModBusEntity read=new ModBusEntity();
        String devAddr=action.getDeviceName().split("_")[1];
        read.setFunc(ModBusConstants.FUN_CODE6);
        read.setDevAddr(Byte.parseByte(devAddr));
        Integer addr=0;
        for (Map.Entry<String, ?> entry : action.getParams().entrySet()) {
            int val = Integer.parseInt((String) entry.getValue());
            String a1= StringUtils.leftPad(addr.toHexString(addr),4,'0')+StringUtils.leftPad(addr.toHexString(val),4,'0');
            read.setData(ByteUtils.hexStrToBinaryStr(a1));
            byte[] msg = analysis.packCmd4Entity(read);
            modbusVerticle.sendMsg(msg);
        }
        return ActionResult.builder().code(0).reason("success").build();
    }

    @Override
    public ActionResult serviceInvoke(ServiceInvoke action) {
        return null;
    }
}
