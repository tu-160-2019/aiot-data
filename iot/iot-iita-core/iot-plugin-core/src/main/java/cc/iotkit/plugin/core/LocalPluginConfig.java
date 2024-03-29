package cc.iotkit.plugin.core;


import java.util.HashMap;
import java.util.Map;

/**
 * 本地独立运行的插件配置
 *
 * @author sjg
 */
public class LocalPluginConfig implements IPluginConfig {
    @Override
    public Map<String, Object> getConfig(String pluginId) {
        //本地的直接用程序中默认值
        return new HashMap<>(0);
    }
}
