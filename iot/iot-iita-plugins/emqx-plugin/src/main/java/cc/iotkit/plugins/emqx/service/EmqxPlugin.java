package cc.iotkit.plugins.emqx.service;

import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.common.utils.ThreadUtil;
import cc.iotkit.common.utils.UniqueIdUtil;
import cc.iotkit.plugin.core.IPlugin;
import cc.iotkit.plugin.core.IPluginConfig;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.DeviceState;
import cc.iotkit.plugin.core.thing.actions.EventLevel;
import cc.iotkit.plugin.core.thing.actions.IDeviceAction;
import cc.iotkit.plugin.core.thing.actions.up.*;
import cc.iotkit.plugin.core.thing.model.ThingDevice;
import cc.iotkit.plugins.emqx.conf.MqttConfig;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.IdUtil;
import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import com.gitee.starblues.bootstrap.realize.PluginCloseListener;
import com.gitee.starblues.core.PluginCloseType;
import com.gitee.starblues.core.PluginInfo;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author sjg
 */
@Slf4j
@Service
public class EmqxPlugin implements PluginCloseListener, IPlugin, Runnable {

    @Autowired
    private PluginInfo pluginInfo;
    @Autowired
    private MqttConfig mqttConfig;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IPluginConfig pluginConfig;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IThingService thingService;

    @Autowired
    private AuthVerticle authVerticle;

    @Autowired
    private MqttDevice mqttDevice;

    private final ScheduledThreadPoolExecutor emqxConnectTask = ThreadUtil.newScheduled(1, "emqx_connect");

    private Vertx vertx;
    private String deployedId;

    private MqttClient client;

    private boolean mqttConnected = false;

    private boolean authServerStarted = false;

    private static final Map<String, Boolean> DEVICE_ONLINE = new ConcurrentHashMap<>();

    public static final Map<String, Set<String>> CLIENT_DEVICE_MAP = new HashMap<>();

    @PostConstruct
    public void init() {
        vertx = Vertx.vertx();
        try {
            //获取插件最新配置替换当前配置
            Map<String, Object> config = pluginConfig.getConfig(pluginInfo.getPluginId());
            BeanUtil.copyProperties(config, mqttConfig, CopyOptions.create().ignoreNullValue());

            String serverPassword = IdUtil.fastSimpleUUID();
            MqttClientOptions options = new MqttClientOptions()
                    .setClientId("server")
                    .setUsername("server")
                    .setPassword(serverPassword)
                    .setCleanSession(true)
                    .setMaxInflightQueue(100)
                    .setKeepAliveInterval(60);

            if (mqttConfig.isSsl()) {
                options.setSsl(true)
                        .setTrustAll(true);
            }
            client = MqttClient.create(vertx, options);
            mqttDevice.setClient(client);

            authVerticle.setPort(mqttConfig.getAuthPort());
            authVerticle.setServerPassword(serverPassword);

            emqxConnectTask.scheduleWithFixedDelay(this, 3, 3, TimeUnit.SECONDS);
        } catch (Throwable e) {
            log.error("mqtt plugin startup error", e);
        }
    }

    @Override
    public void run() {
        if (!authServerStarted) {
            try {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                Future<String> future = vertx.deployVerticle(authVerticle);
                future.onSuccess((s -> {
                    deployedId = s;
                    countDownLatch.countDown();
                    authServerStarted = true;
                    log.info("start emqx auth plugin success");
                }));
                future.onFailure(e -> {
                    countDownLatch.countDown();
                    authServerStarted = false;
                    log.error("start emqx auth plugin failed", e);
                });
                countDownLatch.await();
            } catch (Exception e) {
                authServerStarted = false;
                log.error("start emqx auth server failed", e);
            }
        }

        if (mqttConnected) {
            return;
        }

        try {
            String[] topics = mqttConfig.getTopics().split(",");
            Map<String, Integer> subscribes = new HashMap<>(topics.length);
            for (String topic : topics) {
                subscribes.put(topic, 1);
            }

            client.connect(mqttConfig.getPort(), mqttConfig.getHost(), s -> {
                if (s.succeeded()) {
                    log.info("client connect success.");
                    mqttConnected = true;
                    client.subscribe(subscribes, e -> {
                        if (e.succeeded()) {
                            log.info("===>subscribe success: {}", e.result());
                        } else {
                            log.error("===>subscribe fail: ", e.cause());
                        }
                    });

                } else {
                    mqttConnected = false;
                    log.error("client connect fail: ", s.cause());
                }
            }).publishHandler(msg -> {
                String topic = msg.topicName();
                if (topic.contains("/c/")) {
                    return;
                }

                JsonObject payload = msg.payload().toJsonObject();
                log.info("Client received message on [{}] payload [{}] with QoS [{}]", topic, payload, msg.qosLevel());

                try {
                    //客户端连接断开
                    if (topic.equals("/sys/client/disconnected")) {
                        offline(payload.getString("clientid"));
                        return;
                    }

                    ThingDevice device = getDevice(topic);
                    if (device == null) {
                        return;
                    }

                    //有消息上报-设备上线
                    online(device.getProductKey(), device.getDeviceName());

                    JsonObject defParams = JsonObject.mapFrom(new HashMap<>(0));
                    IDeviceAction action = null;

                    String method = payload.getString("method", "");
                    if (StringUtils.isBlank(method)) {
                        return;
                    }
                    JsonObject params = payload.getJsonObject("params", defParams);

                    if ("thing.lifetime.register".equalsIgnoreCase(method)) {
                        //子设备注册
                        String subPk = params.getString("productKey");
                        String subDn = params.getString("deviceName");
                        String subModel = params.getString("model");
                        ActionResult regResult = thingService.post(
                                pluginInfo.getPluginId(),
                                fillAction(
                                        SubDeviceRegister.builder()
                                                .productKey(device.getProductKey())
                                                .deviceName(device.getDeviceName())
                                                .version("1.0")
                                                .subs(List.of(
                                                        DeviceRegister.builder()
                                                                .productKey(subPk)
                                                                .deviceName(subDn)
                                                                .model(subModel)
                                                                .build()
                                                ))
                                                .build()
                                )
                        );
                        if (regResult.getCode() == 0) {
                            //注册成功
                            reply(topic, payload, 0);
                            Set<String> devices = CLIENT_DEVICE_MAP.get(device.getProductKey() + device.getDeviceName());
                            devices.add(subPk + "," + subDn);
                        } else {
                            //注册失败
                            reply(topic, new JsonObject(), regResult.getCode());
                        }
                        return;
                    }

                    if ("thing.event.property.post".equalsIgnoreCase(method)) {
                        //属性上报
                        action = PropertyReport.builder()
                                .params(params.getMap())
                                .build();
                        reply(topic, payload, 0);
                    } else if (method.startsWith("thing.event.")) {
                        //事件上报
                        action = EventReport.builder()
                                .name(method.replace("thing.event.", ""))
                                .level(EventLevel.INFO)
                                .params(params.getMap())
                                .build();
                        reply(topic, payload, 0);
                    } else if (method.startsWith("thing.service.") && method.endsWith("_reply")) {
                        //服务回复
                        action = ServiceReply.builder()
                                .name(method.replaceAll("thing\\.service\\.(.*)_reply", "$1"))
                                .code(payload.getInteger("code", 0))
                                .params(params.getMap())
                                .build();
                    }

                    if (action == null) {
                        return;
                    }
                    action.setId(payload.getString("id"));
                    action.setProductKey(device.getProductKey());
                    action.setDeviceName(device.getDeviceName());
                    action.setTime(System.currentTimeMillis());
                    thingService.post(pluginInfo.getPluginId(), action);

                } catch (Exception e) {
                    log.error("message is illegal.", e);
                }
            }).closeHandler(e -> {
                mqttConnected = false;
                log.info("client closed");
            }).exceptionHandler(event -> log.error("client fail", event));
        } catch (Exception e) {
            log.error("start emqx client failed", e);
        }

    }

    public ThingDevice getDevice(String topic) {
        String[] topicParts = topic.split("/");
        if (topicParts.length < 5) {
            return null;
        }
        return ThingDevice.builder()
                .productKey(topicParts[2])
                .deviceName(topicParts[3])
                .build();
    }

    public void online(String pk, String dn) {
        if (Boolean.TRUE.equals(DEVICE_ONLINE.get(dn))) {
            return;
        }

        //上线
        thingService.post(
                pluginInfo.getPluginId(),
                fillAction(DeviceStateChange.builder()
                        .productKey(pk)
                        .deviceName(dn)
                        .state(DeviceState.ONLINE)
                        .build()
                )
        );
        DEVICE_ONLINE.put(dn, true);
    }

    public void offline(String clientId) {
        String[] parts = clientId.split("_");
        Set<String> devices = CLIENT_DEVICE_MAP.get(parts[0] + parts[1]);
        for (String device : devices) {
            String[] pkDn = device.split(",");
            //下线
            thingService.post(
                    pluginInfo.getPluginId(),
                    fillAction(DeviceStateChange.builder()
                            .productKey(pkDn[0])
                            .deviceName(pkDn[1])
                            .state(DeviceState.OFFLINE)
                            .build()
                    )
            );
            DEVICE_ONLINE.remove(pkDn[1]);
        }
    }

    private IDeviceAction fillAction(IDeviceAction action) {
        action.setId(UniqueIdUtil.newRequestId());
        action.setTime(System.currentTimeMillis());
        return action;
    }

    /**
     * 回复设备
     */
    private void reply(String topic, JsonObject payload, int code) {
        Map<String, Object> payloadReply = new HashMap<>();
        payloadReply.put("id", payload.getString("id"));
        payloadReply.put("method", payload.getString("method") + "_reply");
        payloadReply.put("code", code);
        payloadReply.put("data", payload.getJsonObject("params"));
        topic = topic.replace("/s/", "/c/") + "_reply";

        String finalTopic = topic;
        client.publish(topic, JsonObject.mapFrom(payloadReply).toBuffer(), MqttQoS.AT_LEAST_ONCE, false, false)
                .onSuccess(h -> {
                    log.info("publish {} success", finalTopic);
                });
    }

    @Override
    public void close(GenericApplicationContext applicationContext, PluginInfo pluginInfo, PluginCloseType closeType) {
        try {
            log.info("plugin close,type:{},pluginId:{}", closeType, pluginInfo.getPluginId());
            if (deployedId != null) {
                CountDownLatch wait = new CountDownLatch(1);
                Future<Void> future = vertx.undeploy(deployedId);
                future.onSuccess(unused -> {
                    log.info("emqx plugin stopped success");
                    wait.countDown();
                });
                future.onFailure(h -> {
                    log.error("emqx plugin stopped failed", h);
                    wait.countDown();
                });
                wait.await(5, TimeUnit.SECONDS);
            }

            client.disconnect()
                    .onSuccess(unused -> {
                        mqttConnected = false;
                        log.info("stop emqx connect success");
                    })
                    .onFailure(unused -> log.error("stop emqx connect failure"));

            emqxConnectTask.shutdown();

        } catch (Throwable e) {
            log.error("emqx plugin stop error", e);
        }
    }

    @Override
    public Map<String, Object> getLinkInfo(String pk, String dn) {
        return null;
    }
}
