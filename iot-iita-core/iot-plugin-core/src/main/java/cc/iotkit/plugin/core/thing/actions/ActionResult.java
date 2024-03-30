package cc.iotkit.plugin.core.thing.actions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 动作执行结果
 *
 * @author sjg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionResult {

    /**
     * 状态码，0:成功，x:其它错误码
     */
    private int code;

    /**
     * 失败原因
     */
    private String reason;

}
