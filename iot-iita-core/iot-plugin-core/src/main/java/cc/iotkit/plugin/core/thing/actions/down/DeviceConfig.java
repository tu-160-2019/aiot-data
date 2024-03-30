package cc.iotkit.plugin.core.thing.actions.down;


import cc.iotkit.plugin.core.thing.actions.AbstractAction;
import cc.iotkit.plugin.core.thing.actions.ActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * 设备配置
 *
 * @author sjg
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DeviceConfig extends AbstractAction {

    /**
     * 配置模块
     */
    private String module;

    /**
     * 配置信息
     */
    private Map<String, Object> config;

    @Override
    public ActionType getType() {
        return ActionType.CONFIG;
    }
}
