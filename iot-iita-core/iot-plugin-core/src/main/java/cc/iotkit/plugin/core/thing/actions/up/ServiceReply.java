package cc.iotkit.plugin.core.thing.actions.up;


import cc.iotkit.plugin.core.thing.actions.AbstractAction;
import cc.iotkit.plugin.core.thing.actions.ActionType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * 服务回复
 *
 * @author sjg
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class ServiceReply extends AbstractAction {

    /**
     * 服务名
     */
    private String name;

    /**
     * 回复服务id
     */
    private String replyId;

    /**
     * 状态码,0:成功,x:失败错误码
     */
    private int code;

    /**
     * 服务回复参数
     */
    private Map<String, Object> params;

    @Override
    public ActionType getType() {
        return ActionType.SERVICE_REPLY;
    }
}
