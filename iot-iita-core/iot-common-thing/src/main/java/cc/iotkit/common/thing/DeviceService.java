package cc.iotkit.common.thing;


/**
 * 通用设备服务
 *
 * @author sjg
 */
public interface DeviceService {

    /**
     * 调用设备服务
     *
     * @param service 服务
     */
    void invoke(ThingService<?> service);

}
