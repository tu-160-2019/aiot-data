package cc.iotkit.plugin.core.thing.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事件级别
 *
 * @author sjg
 */
@Getter
@AllArgsConstructor
public enum EventLevel {

    //信息
    INFO("info"),
    //告警
    WARN("warn"),
    //错误
    ERROR("error");

    private final String type;

}
