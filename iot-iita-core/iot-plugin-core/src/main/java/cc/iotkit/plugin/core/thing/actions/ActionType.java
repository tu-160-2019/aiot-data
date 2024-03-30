package cc.iotkit.plugin.core.thing.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备物行为类型
 *
 * @author sjg
 */
@Getter
@AllArgsConstructor
public enum ActionType {

    //注册
    REGISTER("register"),
    //子注册
    SUB_REGISTER("sub_register"),
    //设备拓扑更新
    TOPOLOGY("topology"),
    //在离线状态变更
    STATE_CHANGE("state_change"),
    //属性上报
    PROPERTY_REPORT("property_report"),
    //事件上报
    EVENT_REPORT("event_report"),
    //服务回复
    SERVICE_REPLY("service_reply"),

    //属性设置
    PROPERTY_SET("property_set"),
    //属性获取
    PROPERTY_GET("property_get"),
    //服务调用
    SERVICE_INVOKE("service_invoke"),
    //配置
    CONFIG("config");

    private final String type;

}
