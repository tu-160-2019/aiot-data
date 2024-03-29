package cc.iotkit.plugin.core.thing.actions.up;


import cc.iotkit.plugin.core.thing.actions.AbstractAction;
import cc.iotkit.plugin.core.thing.actions.ActionType;
import cc.iotkit.plugin.core.thing.actions.EventLevel;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * 事件上报
 *
 * @author sjg
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class EventReport extends AbstractAction {

    /**
     * 事件名
     */
    private String name;

    /**
     * 事件级别
     */
    private EventLevel level;

    /**
     * 事件参数
     */
    private Map<String, Object> params;

    @Override
    public ActionType getType() {
        return ActionType.EVENT_REPORT;
    }
}
