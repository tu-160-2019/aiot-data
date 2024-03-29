package cc.iotkit.plugin.core.thing;

import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.IDeviceAction;
import cc.iotkit.plugin.core.thing.model.ThingDevice;
import cc.iotkit.plugin.core.thing.model.ThingProduct;

import java.util.Map;

/**
 * 设备服务接口
 *
 * @author sjg
 */
public interface IThingService {

    /**
     * 提交设备行为
     *
     * @param action IDeviceAction
     * @return result
     */
    ActionResult post(String pluginId, IDeviceAction action);

    /**
     * 获取产品信息
     *
     * @param pk 产品key
     * @return Product
     */
    ThingProduct getProduct(String pk);

    /**
     * 获取设备信息
     *
     * @param deviceName 设备dn
     * @return DeviceInfo
     */
    ThingDevice getDevice(String deviceName);

    /**
     * 获取设备当前属性数据
     *
     * @param deviceName 设备dn
     * @return 当前属性
     */
    Map<String, ?> getProperty(String deviceName);

}
