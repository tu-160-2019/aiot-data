package cc.iotkit.plugin.core.thing.actions.up;


import cc.iotkit.plugin.core.thing.actions.AbstractAction;
import cc.iotkit.plugin.core.thing.actions.ActionType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * 属性上报
 *
 * @author sjg
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class PropertyReport extends AbstractAction {

    /**
     * 属性参数
     */
    private Map<String, Object> params;

    @Override
    public ActionType getType() {
        return ActionType.PROPERTY_REPORT;
    }
}
