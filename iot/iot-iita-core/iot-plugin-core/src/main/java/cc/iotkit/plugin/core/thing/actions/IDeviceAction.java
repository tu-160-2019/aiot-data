package cc.iotkit.plugin.core.thing.actions;

/**
 * 设备行为
 *
 * @author sjg
 */
public interface IDeviceAction {

    /**
     * 获取唯一标识id
     *
     * @return id
     */
    String getId();

    /**
     * 设置id
     *
     * @param id id
     */
    void setId(String id);

    /**
     * 获取类型
     *
     * @return ActionType
     */
    ActionType getType();

    /**
     * 设备类型
     *
     * @param type type
     */
    void setType(ActionType type);

    /**
     * 获取产品key
     *
     * @return ProductKey
     */
    String getProductKey();

    /**
     * 设置产品key
     *
     * @param productKey pk
     */
    void setProductKey(String productKey);

    /**
     * 获取设备DN
     *
     * @return DN
     */
    String getDeviceName();

    /**
     * 设置设备DN
     *
     * @param deviceName dn
     */
    void setDeviceName(String deviceName);

    /**
     * 获取时间
     *
     * @return timespan
     */
    Long getTime();

    /**
     * 设置时间
     *
     * @param time timestamp
     */
    void setTime(Long time);
}
