package cc.iotkit.plugin.main.script;

import cc.iotkit.common.utils.CodecUtil;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.manager.IPluginInfoData;
import cc.iotkit.model.plugin.PluginInfo;
import cc.iotkit.script.IScriptEngine;
import cc.iotkit.script.ScriptEngineFactory;
import cn.hutool.core.util.IdUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ScriptVerticle extends AbstractVerticle {

    private final Map<String, VertxTcpClient> clientMap = new ConcurrentHashMap<>();

    private static final Map<String, IScriptEngine> PLUGIN_SCRIPT_ENGINES = new HashMap<>();

    private final Map<String, String> pluginScripts = new HashMap<>();

    @Setter
    private long keepAliveTimeout = Duration.ofSeconds(30).toMillis();

    @Autowired
    private IPluginInfoData pluginInfoData;

    public IScriptEngine initScriptEngine(String pluginId) {
        PluginInfo pluginInfo = pluginInfoData.findByPluginId(pluginId);
        if (pluginInfo == null) {
            return null;
        }
        String script = pluginInfo.getScript();
        if (StringUtils.isBlank(script)) {
            return null;
        }

        //缓存脚本md5，用于判断是否需要更新脚本
        pluginScripts.put(pluginId, CodecUtil.md5Str(script));

        IScriptEngine jsEngine = ScriptEngineFactory.getJsEngine(script);
        PLUGIN_SCRIPT_ENGINES.put(pluginId, ScriptEngineFactory.getJsEngine(script));
        return jsEngine;
    }

    public IScriptEngine getScriptEngine(String pluginId) {
        if (!PLUGIN_SCRIPT_ENGINES.containsKey(pluginId)) {
            return initScriptEngine(pluginId);
        }
        return PLUGIN_SCRIPT_ENGINES.get(pluginId);
    }

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
    public void checkScriptUpdate() {
        //定时检查脚本是否需要更新
        List<PluginInfo> plugins = pluginInfoData.findAll();
        for (PluginInfo plugin : plugins) {
            String pluginId = plugin.getPluginId();
            String oldMd5 = pluginScripts.get(pluginId);
            String script = plugin.getScript();
            if (script == null) {
                continue;
            }
            String md5 = CodecUtil.md5Str(script);
            if (oldMd5 != null && oldMd5.equals(md5)) {
                continue;
            }

            IScriptEngine scriptEngine = PLUGIN_SCRIPT_ENGINES.get(pluginId);
            if (scriptEngine == null) {
                continue;
            }
            log.info("plugin script has changed");
            pluginScripts.put(pluginId, md5);
            scriptEngine.setScript(script);
        }
    }

    @Override
    public void start() {
        initTcpServer();
    }

    @Override
    public void stop() {
        log.info("tcp server stopped");
    }

    /**
     * 初始TCP服务
     */
    private void initTcpServer() {
        ScriptServerConfig config = new ScriptServerConfig();
        NetServer netServer = vertx.createNetServer(
                new NetServerOptions().setHost("127.0.0.1")
                        .setPort(config.getPort()));
        netServer.connectHandler(this::acceptTcpConnection);
        netServer.listen(config.createSocketAddress(), result -> {
            if (result.succeeded()) {
                log.info("tcp server startup on {}", result.result().actualPort());
            } else {
                result.cause().printStackTrace();
            }
        });
    }

    /**
     * TCP连接处理逻辑
     *
     * @param socket socket
     */
    protected void acceptTcpConnection(NetSocket socket) {
        // 客户端连接处理
        String clientId = IdUtil.simpleUUID() + "_" + socket.remoteAddress();
        VertxTcpClient client = new VertxTcpClient(clientId);
        client.setKeepAliveTimeoutMs(keepAliveTimeout);
        try {
            // TCP异常和关闭处理
            socket.exceptionHandler(Throwable::printStackTrace).closeHandler(nil -> {
                log.debug("tcp server client [{}] closed", socket.remoteAddress());
                client.shutdown();
            });
            // 这个地方是在TCP服务初始化的时候设置的 parserSupplier
            client.setKeepAliveTimeoutMs(keepAliveTimeout);
            client.setSocket(socket);
            RecordParser parser = DataReader.getParser(buffer -> {
                try {
                    DataPackage data = DataDecoder.decode(buffer);
                    String pluginId = data.getPluginId();
                    clientMap.put(pluginId, client);
                    IScriptEngine scriptEngine = getScriptEngine(pluginId);
                    if (scriptEngine == null) {
                        data.setResult("");
                    } else {
                        //调用执行脚本方法返回结果
                        String result = scriptEngine.invokeMethod(data.getMethod(), data.getArgs());
                        data.setResult(result);
                    }
                    sendMsg(pluginId, DataEncoder.encode(data));
                } catch (Exception e) {
                    log.error("decode error", e);
                }
            });
            client.setParser(parser);
            log.debug("accept tcp client [{}] connection", socket.remoteAddress());
        } catch (Exception e) {
            log.error("acceptTcpConnection error", e);
            client.shutdown();
        }
    }

    public void sendMsg(String pluginId, Buffer msg) {
        VertxTcpClient tcpClient = clientMap.get(pluginId);
        if (tcpClient != null) {
            tcpClient.sendMessage(msg);
        }
    }

}
