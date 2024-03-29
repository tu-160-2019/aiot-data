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
 * 属性设置
 *
 * @author sjg
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PropertySet extends AbstractAction {

    /**
     * 属性参数
     */
    private Map<String, ?> params;

    @Override
    public ActionType getType() {
        return ActionType.PROPERTY_SET;
    }
}
