package cc.iotkit.model.plugin;

import cc.iotkit.model.BaseModel;
import cc.iotkit.model.Id;
import lombok.*;

import java.io.Serializable;

/**
 * 插件实例
 *
 * @author sjg
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PluginInstance extends BaseModel implements Id<Long>, Serializable {

    private Long id;

    /**
     * 插件主程序id
     */
    private String mainId;

    /**
     * 插件id
     */
    private Long pluginId;

    /**
     * 插件主程序所在ip
     */
    private String ip;

    /**
     * 插件主程序端口
     */
    private int port;

    /**
     * 心跳时间
     * 心路时间超过30秒需要剔除
     */
    private Long heartbeatAt;

}
