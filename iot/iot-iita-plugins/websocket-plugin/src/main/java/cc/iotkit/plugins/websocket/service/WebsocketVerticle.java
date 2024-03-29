/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.plugins.websocket.service;

import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.DeviceState;
import cc.iotkit.plugin.core.thing.actions.up.DeviceRegister;
import cc.iotkit.plugin.core.thing.actions.up.DeviceStateChange;
import cc.iotkit.plugins.websocket.conf.WebsocketConfig;
import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import com.gitee.starblues.core.PluginInfo;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.net.PemKeyCertOptions;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author tfd
 */
@Slf4j
@Component
@Data
public class WebsocketVerticle extends AbstractVerticle {

    private HttpServer httpServer;
    private final Map<String, ServerWebSocket> wsClients = new ConcurrentHashMap<>();

    private static final Map<String, Boolean> CONNECT_POOL = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> DEVICE_ONLINE = new ConcurrentHashMap<>();

    private Map<String, String> tokens=new HashMap<>();

    private WebsocketConfig config;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IThingService thingService;

    @Autowired
    private PluginInfo pluginInfo;

    @Override
    public void start() {
        Executors.newSingleThreadScheduledExecutor().schedule(this::initWsServer, 3, TimeUnit.SECONDS);
    }

    private void initWsServer() {
        HttpServerOptions options = new HttpServerOptions()
                .setPort(config.getPort());
        if (config.isSsl()) {
            options = options.setSsl(true)
                    .setKeyCertOptions(new PemKeyCertOptions()
                            .setKeyPath(config.getSslKey())
                            .setCertPath(config.getSslCert()));
        }

        httpServer = vertx.createHttpServer(options).webSocketHandler(wsClient -> {
            log.info("webSocket client connect sessionId:{},path={}", wsClient.textHandlerID(), wsClient.path());
            String deviceKey = wsClient.path().replace("/","");
            String[] strArr=deviceKey.split("_");
            if(StringUtils.isBlank(deviceKey)||strArr.length!=2){
                log.warn("陌生连接，拒绝");
                wsClient.reject();
                return;
            }
            wsClient.writeTextMessage("connect succes! please auth!");
            wsClient.textMessageHandler(message -> {
                HashMap<String,String> msg;
                try{
                    msg=JsonUtils.parseObject(message,HashMap.class);
                }catch (Exception e){
                    log.warn("数据格式异常");
                    wsClient.writeTextMessage("data err");
                    return;
                }
                if(wsClients.containsKey(deviceKey)){
                    if("ping".equals(msg.get("type"))){
                        msg.put("type","pong");
                        wsClient.writeTextMessage(JsonUtils.toJsonString(msg));
                        return;
                    }
                    if("register".equals(msg.get("type"))){
                        //设备注册
                        ActionResult result = thingService.post(
                                pluginInfo.getPluginId(),
                                DeviceRegister.builder()
                                        .productKey(strArr[1])
                                        .deviceName(deviceKey)
                                        .model("")
                                        .version("1.0")
                                        .build()
                        );
                        if(result.getCode()==0){
                            thingService.post(
                                    pluginInfo.getPluginId(),
                                    DeviceStateChange.builder()
                                            .productKey(strArr[1])
                                            .deviceName(deviceKey)
                                            .state(DeviceState.ONLINE)
                                            .build()
                            );
                        }else{
                            //注册失败
                            Map<String,String> ret=new HashMap<>();
                            ret.put("id",msg.get("id"));
                            ret.put("type",msg.get("type"));
                            ret.put("result","fail");
                            wsClient.writeTextMessage(JsonUtils.toJsonString(ret));
                            return;
                        }
                    }
                }else if(msg!=null&&"auth".equals(msg.get("type"))){
                    Set<String> tokenKey=tokens.keySet();
                    for(String key:tokenKey){
                        if(StringUtils.isNotBlank(msg.get(key))&&tokens.get(key).equals(msg.get(key))){
                            //保存设备与连接关系
                            log.info("认证通过");
                            wsClients.put(deviceKey, wsClient);
                            wsClient.writeTextMessage("auth succes");
                            return;
                        }
                    }
                    log.warn("认证失败，拒绝");
                    wsClient.writeTextMessage("auth fail");
                    return;
                }else{
                    log.warn("认证失败，拒绝");
                    wsClient.writeTextMessage("auth fail");
                    return;
                }

            });
            wsClient.closeHandler(c -> {
                log.warn("client connection closed,deviceKey:{}", deviceKey);
                if(wsClients.containsKey(deviceKey)){
                    wsClients.remove(deviceKey);
                    thingService.post(
                            pluginInfo.getPluginId(),
                            DeviceStateChange.builder()
                                    .productKey(strArr[1])
                                    .deviceName(deviceKey)
                                    .state(DeviceState.OFFLINE)
                                    .build()
                    );
                }
            });
            wsClient.exceptionHandler(ex -> {
                log.warn("webSocket client connection exception,deviceKey:{}", deviceKey);
                if(wsClients.containsKey(deviceKey)){
                    wsClients.remove(deviceKey);
                    thingService.post(
                            pluginInfo.getPluginId(),
                            DeviceStateChange.builder()
                                    .productKey(strArr[1])
                                    .deviceName(deviceKey)
                                    .state(DeviceState.OFFLINE)
                                    .build()
                    );
                }
            });
        }).listen(config.getPort(), server -> {
            if (server.succeeded()) {
                log.info("webSocket server is listening on port " + config.getPort());
                if(config.getTokenKey()!=null&&config.getAccessToken()!=null){
                        tokens.put(config.getTokenKey(),config.getAccessToken());
                }
            } else {
                log.error("webSocket server on starting the server", server.cause());
            }
        });
    }

    @Override
    public void stop() throws Exception {
        for (String deviceKey : wsClients.keySet()) {
            thingService.post(
                    pluginInfo.getPluginId(),
                    DeviceStateChange.builder()
                            .productKey(deviceKey.split("_")[1])
                            .deviceName(deviceKey)
                            .state(DeviceState.OFFLINE)
                            .build()
            );
        }
        tokens.clear();
        httpServer.close(voidAsyncResult -> log.info("close webocket server..."));
    }

    private String getDeviceKey(String productKey, String deviceName) {
        return String.format("%s_%s", productKey, deviceName);
    }

    public void send(String deviceName,String msg) {
        ServerWebSocket wsClient = wsClients.get(deviceName);
        String msgStr = JsonUtils.toJsonString(msg);
        log.info("send msg payload:{}", msgStr);
        Future<Void> result = wsClient.writeTextMessage(msgStr);
        result.onFailure(e -> log.error("webSocket server send msg failed", e));
    }

}
