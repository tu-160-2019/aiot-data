package cc.iotkit.plugin.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 插件路由
 *
 * @author sjg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PluginRouter {

    private String mainId;

    private String pluginId;

}
