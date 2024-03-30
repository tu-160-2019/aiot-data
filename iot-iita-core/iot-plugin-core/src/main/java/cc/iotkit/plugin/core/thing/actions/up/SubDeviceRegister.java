package cc.iotkit.plugin.core.thing.actions.up;


import cc.iotkit.plugin.core.thing.actions.AbstractAction;
import cc.iotkit.plugin.core.thing.actions.ActionType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 子设备注册动作
 *
 * @author sjg
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class SubDeviceRegister extends AbstractAction {

    /**
     * 型号
     */
    private String model;

    /**
     * 版本号
     */
    private String version;

    /**
     * 子设备注册
     */
    private List<DeviceRegister> subs;

    @Override
    public ActionType getType() {
        return ActionType.SUB_REGISTER;
    }
}
