package cc.iotkit.plugin.core.thing;

import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.down.DeviceConfig;
import cc.iotkit.plugin.core.thing.actions.down.PropertyGet;
import cc.iotkit.plugin.core.thing.actions.down.PropertySet;
import cc.iotkit.plugin.core.thing.actions.down.ServiceInvoke;


/**
 * 设备接口
 *
 * @author sjg
 */
public interface IDevice {

    /**
     * 执行设备配置动作
     *
     * @param action 动作
     * @return result
     */
    ActionResult config(DeviceConfig action);

    /**
     * 执行设备属性获取动作
     *
     * @param action 动作
     * @return result
     */
    ActionResult propertyGet(PropertyGet action);

    /**
     * 执行设备属性设置动作
     *
     * @param action 动作
     * @return result
     */
    ActionResult propertySet(PropertySet action);

    /**
     * 执行设备服务调用动作
     *
     * @param action 动作
     * @return result
     */
    ActionResult serviceInvoke(ServiceInvoke action);

}
