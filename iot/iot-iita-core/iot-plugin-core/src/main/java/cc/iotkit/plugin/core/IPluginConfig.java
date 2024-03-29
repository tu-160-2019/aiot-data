package cc.iotkit.plugin.core;


import java.util.Map;

/**
 * 插件配置接口
 *
 * @author sjg
 */
public interface IPluginConfig {

    /**
     * 获取系统中配置的插件配置项
     *
     * @param pluginId 插件id
     * @return config
     */
    Map<String, Object> getConfig(String pluginId);

}
