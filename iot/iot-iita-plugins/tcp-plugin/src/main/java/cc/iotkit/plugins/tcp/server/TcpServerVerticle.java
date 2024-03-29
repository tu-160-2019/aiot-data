package cc.iotkit.plugins.tcp.server;


import cc.iotkit.common.exception.BizException;
import cc.iotkit.plugin.core.IPluginScript;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.DeviceState;
import cc.iotkit.plugin.core.thing.actions.up.DeviceRegister;
import cc.iotkit.plugin.core.thing.actions.up.DeviceStateChange;
import cc.iotkit.plugin.core.thing.actions.up.PropertyReport;
import cc.iotkit.plugins.tcp.cilent.VertxTcpClient;
import cc.iotkit.plugins.tcp.conf.TcpServerConfig;
import cc.iotkit.plugins.tcp.parser.DataDecoder;
import cc.iotkit.plugins.tcp.parser.DataEncoder;
import cc.iotkit.plugins.tcp.parser.DataPackage;
import cc.iotkit.plugins.tcp.parser.DataReader;
import cc.iotkit.script.IScriptEngine;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import com.gitee.starblues.core.PluginInfo;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author huangwenl
 * @date 2022-10-13
 */
@Slf4j
@Service
public class TcpServerVerticle extends AbstractVerticle {

    @Getter
    @Setter
    private TcpServerConfig config;

    private final Map<String, VertxTcpClient> clientMap = new ConcurrentHashMap<>();

    private final Map<String, String> dnToPk = new HashMap<>();

    private final Map<String, Long> heartbeatDevice = new HashMap<>();

    @Setter
    private long keepAliveTimeout = Duration.ofSeconds(30).toMillis();

    private NetServer netServer;

    @Getter
    private IScriptEngine scriptEngine;

    @Autowired
    private PluginInfo pluginInfo;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IPluginScript pluginScript;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IThingService thingService;

    @Override
    public void stop() {
        if (netServer != null) {
            netServer.close(rst -> {
                log.info("tcp server close:{}", rst.succeeded());
            });
        }

        log.info("tcp server stopped");
    }

    /**
     * 创建配置文件
     */
    @PostConstruct
    public void initConfig() {
        log.info("initConfig:{}", pluginScript.getClass().getName());
        //获取脚本引擎
        scriptEngine = pluginScript.getScriptEngine(pluginInfo.getPluginId());
        if (scriptEngine == null) {
            throw new BizException("script engine is null");
        }
        Executors.newSingleThreadScheduledExecutor().schedule(this::initTcpServer, 3, TimeUnit.SECONDS);
    }


    /**
     * 初始TCP服务
     */
    private void initTcpServer() {
        netServer = vertx.createNetServer(
                new NetServerOptions().setHost(config.getHost())
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

    public void sendMsg(String addr, Buffer msg) {
        VertxTcpClient tcpClient = clientMap.get(addr);
        if (tcpClient != null) {
            tcpClient.sendMessage(msg);
        }
    }

    @Scheduled(fixedRate = 40, timeUnit = TimeUnit.SECONDS)
    private void offlineCheckTask() {
        log.info("keepClientTask");
        Set<String> clients = new HashSet<>(clientMap.keySet());
        for (String key : clients) {
            VertxTcpClient client = clientMap.get(key);
            if (!client.isOnline()) {
                client.shutdown();
            }
        }

        heartbeatDevice.keySet().iterator().forEachRemaining(addr -> {
            Long time = heartbeatDevice.get(addr);
            //心跳超时，判定为离线
            if (System.currentTimeMillis() - time > keepAliveTimeout * 2) {
                heartbeatDevice.remove(addr);
                //离线上报
                thingService.post(pluginInfo.getPluginId(), DeviceStateChange.builder()
                        .id(IdUtil.simpleUUID())
                        .productKey(dnToPk.get(addr))
                        .deviceName(addr)
                        .state(DeviceState.OFFLINE)
                        .time(System.currentTimeMillis())
                        .build());
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
                    String addr = data.getAddr();
                    int code = data.getCode();
                    if (code == DataPackage.CODE_REGISTER) {
                        clientMap.put(addr, client);
                        heartbeatDevice.remove(addr);
                        //设备注册
                        String pk = new String(data.getPayload());
                        dnToPk.put(addr, pk);
                        ActionResult rst = thingService.post(pluginInfo.getPluginId(), DeviceRegister.builder()
                                .id(IdUtil.simpleUUID())
                                .productKey(pk)
                                .deviceName(addr)
                                .time(System.currentTimeMillis())
                                .build());
                        if (rst.getCode() == 0) {
                            //回复注册成功给客户端
                            sendMsg(addr, DataEncoder.encode(
                                    DataPackage.builder()
                                            .addr(addr)
                                            .code(DataPackage.CODE_REGISTER_REPLY)
                                            .mid(data.getMid())
                                            .payload(Buffer.buffer().appendInt(0).getBytes())
                                            .build()
                            ));
                        }
                        return;
                    }

                    if (code == DataPackage.CODE_HEARTBEAT) {
                        //心跳
                        online(addr);
                        heartbeatDevice.put(addr, System.currentTimeMillis());
                        return;
                    }

                    if (code == DataPackage.CODE_DATA_UP) {
                        //设备数据上报
                        online(addr);
                        //数据上报也作为心跳
                        heartbeatDevice.put(addr, System.currentTimeMillis());
                        //这里可以直接解码，或调用脚本解码（用脚本解码不用修改程序重新打包）
                        Map<String, Object> rst = scriptEngine.invokeMethod(new TypeReference<>() {
                        }, "decode", data);
                        if (rst == null) {
                            return;
                        }
                        //属性上报
                        thingService.post(pluginInfo.getPluginId(), PropertyReport.builder()
                                .id(IdUtil.simpleUUID())
                                .productKey(dnToPk.get(addr))
                                .deviceName(addr)
                                .params(rst)
                                .time(System.currentTimeMillis())
                                .build());
                    }

                    //未注册断开连接
                    if (!clientMap.containsKey(data.getAddr())) {
                        socket.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            client.setParser(parser);
            log.debug("accept tcp client [{}] connection", socket.remoteAddress());
        } catch (Exception e) {
            e.printStackTrace();
            client.shutdown();
        }
    }

    private void online(String addr) {
        if (!heartbeatDevice.containsKey(addr)) {
            //第一次心跳，上线
            thingService.post(pluginInfo.getPluginId(), DeviceStateChange.builder()
                    .id(IdUtil.simpleUUID())
                    .productKey(dnToPk.get(addr))
                    .deviceName(addr)
                    .state(DeviceState.ONLINE)
                    .time(System.currentTimeMillis())
                    .build());
        }
    }

}
