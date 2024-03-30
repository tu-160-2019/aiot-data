package cc.iotkit.plugin.main.script;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 数据包
 *
 * @author sjg
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataPackage {

    /**
     * 消息id
     */
    private String mid;

    /**
     * 插件id
     */
    private String pluginId;

    /**
     * 调用方法
     */
    private String method;

    /**
     * 方法参数
     */
    private String args;

    /**
     * 执行结果
     */
    private String result;

}
