package cc.iotkit.plugin.core;

import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.common.utils.file.FileUtils;
import cc.iotkit.script.IScriptEngine;
import cc.iotkit.script.ScriptEngineFactory;

import java.net.URL;
import java.nio.charset.Charset;

/**
 * 本地独立运行的插件脚本实现
 *
 * @author sjg
 */
public class LocalPluginScript implements IPluginScript {

    private IScriptEngine scriptEngine;

    public LocalPluginScript(String scriptPath) {
        if (StringUtils.isBlank(scriptPath)) {
            return;
        }
        URL resource = LocalPluginScript.class.getClassLoader().getResource(scriptPath);
        if (resource == null) {
            return;
        }

        String script = FileUtils.readString(resource.getFile(), Charset.defaultCharset());
        initScriptEngine(script);
    }

    public IScriptEngine initScriptEngine(String script) {
        if (StringUtils.isBlank(script)) {
            return null;
        }

        scriptEngine = ScriptEngineFactory.getJsEngine(script);
        return scriptEngine;
    }

    @Override
    public IScriptEngine getScriptEngine(String pluginId) {
        return scriptEngine;
    }

    @Override
    public void reloadScript(String pluginId) {
    }
}
