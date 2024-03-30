package cc.iotkit.plugin.core;

import cc.iotkit.script.IScriptEngine;

/**
 * 插件宿主接口
 *
 * @author sjg
 */
public interface IPluginScript {

    /**
     * 获取插件对应的脚本引擎
     *
     * @param pluginId 插件id
     * @return IScriptEngine
     */
    IScriptEngine getScriptEngine(String pluginId);

    /**
     * 重新加载脚本
     *
     * @param pluginId 插件id
     */
    void reloadScript(String pluginId);

}
