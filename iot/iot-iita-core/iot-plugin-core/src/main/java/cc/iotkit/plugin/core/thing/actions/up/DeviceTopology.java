package cc.iotkit.plugin.core.thing.actions.up;


import cc.iotkit.plugin.core.thing.actions.AbstractAction;
import cc.iotkit.plugin.core.thing.actions.ActionType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 设备拓扑更新
 *
 * @author sjg
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class DeviceTopology extends AbstractAction {

    /**
     * 父设备下的子设备列表
     */
    private List<String> subDevices;

    @Override
    public ActionType getType() {
        return ActionType.TOPOLOGY;
    }
}
