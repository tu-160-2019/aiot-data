package cc.iotkit.plugin.main.script;

import cc.iotkit.data.manager.IPluginInfoData;
import cc.iotkit.script.IScriptEngine;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于插件的脚本引擎，为了规避graalvm js加载问题，不直接调用使用tcp通讯交互
 *
 * @author sjg
 */
@Slf4j
@Service
public class PluginScriptServer {
    @Autowired
    private IPluginInfoData pluginInfoData;

    @Autowired
    private ScriptVerticle scriptVerticle;

    private static final Map<String, IScriptEngine> PLUGIN_SCRIPT_ENGINES = new HashMap<>();

    @PostConstruct
    public void init() {
        Vertx vertx = Vertx.vertx();
        Future<String> future = vertx.deployVerticle(scriptVerticle);
        future.onSuccess((s -> {
            log.info("plugin script server started success");
        }));
        future.onFailure(Throwable::printStackTrace);
    }

    public IScriptEngine getScriptEngine(String pluginId) {
        if (!PLUGIN_SCRIPT_ENGINES.containsKey(pluginId)) {
            PLUGIN_SCRIPT_ENGINES.put(pluginId,new PluginScriptEngine(pluginId));
        }
        return PLUGIN_SCRIPT_ENGINES.get(pluginId);
    }

}
