package cc.iotkit.plugin.core.thing.actions.up;


import cc.iotkit.plugin.core.thing.actions.AbstractAction;
import cc.iotkit.plugin.core.thing.actions.ActionType;
import cc.iotkit.plugin.core.thing.actions.DeviceState;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 设备在线状态变更
 *
 * @author sjg
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class DeviceStateChange extends AbstractAction {

    private DeviceState state;

    @Override
    public ActionType getType() {
        return ActionType.STATE_CHANGE;
    }
}
