package cc.iotkit.plugin.core.thing.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备状态
 * @author sjg
 */
@Getter
@AllArgsConstructor
public enum DeviceState {

    //在线
    ONLINE("online"),
    //离线
    OFFLINE("offline");

    private final String state;

}
