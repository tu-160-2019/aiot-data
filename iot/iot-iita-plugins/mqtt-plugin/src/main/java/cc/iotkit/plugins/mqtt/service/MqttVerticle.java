/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.plugins.mqtt.service;

import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.utils.CodecUtil;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.common.utils.UniqueIdUtil;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugin.core.thing.actions.ActionResult;
import cc.iotkit.plugin.core.thing.actions.DeviceState;
import cc.iotkit.plugin.core.thing.actions.EventLevel;
import cc.iotkit.plugin.core.thing.actions.IDeviceAction;
import cc.iotkit.plugin.core.thing.actions.up.*;
import cc.iotkit.plugin.core.thing.model.ThingDevice;
import cc.iotkit.plugin.core.thing.model.ThingProduct;
import cc.iotkit.plugins.mqtt.conf.MqttConfig;
import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import com.gitee.starblues.core.PluginInfo;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttProperties;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.mqtt.*;
import io.vertx.mqtt.messages.codes.MqttSubAckReasonCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * mqtt官方协议文档：
 * http://iotkit-open-source.gitee.io/document/pages/device_protocol/mqtt/#%E7%BD%91%E5%85%B3%E8%BF%9E%E6%8E%A5%E5%92%8C%E6%B3%A8%E5%86%8C
 *
 * @author sjg
 */
@Slf4j
@Component
@Data
public class MqttVerticle extends AbstractVerticle implements Handler<MqttEndpoint> {

    private MqttServer mqttServer;
    private final Map<String, MqttEndpoint> endpointMap = new HashMap<>();
    /**
     * 增加一个客户端连接clientid-连接状态池，避免mqtt关闭的时候走异常断开和mqtt断开的handler，导致多次离线消息
     */
    private static final Map<String, Boolean> MQTT_CONNECT_POOL = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> DEVICE_ONLINE = new ConcurrentHashMap<>();

    private MqttConfig config;

    @Autowired
    @AutowiredType(AutowiredType.Type.MAIN_PLUGIN)
    private IThingService thingService;

    @Autowired
    private PluginInfo pluginInfo;

    @Override
    public void start() {
        Executors.newSingleThreadScheduledExecutor().schedule(this::initMqttServer, 3, TimeUnit.SECONDS);
    }

    private void initMqttServer() {
        MqttServerOptions options = new MqttServerOptions()
                .setPort(config.getPort());
        if (config.isSsl()) {
            options = options.setSsl(true)
                    .setKeyCertOptions(new PemKeyCertOptions()
                            .setKeyPath(config.getSslKey())
                            .setCertPath(config.getSslCert()));
        }
        options.setUseWebSocket(config.isUseWebSocket());
        options.setTcpKeepAlive(true);

        mqttServer = MqttServer.create(vertx, options);
        mqttServer.endpointHandler(this).listen(ar -> {
            if (ar.succeeded()) {
                log.info("MQTT server is listening on port " + ar.result().actualPort());
            } else {
                log.error("Error on starting the server", ar.cause());
            }
        });
    }

    @Override
    public void handle(MqttEndpoint endpoint) {
        log.info("MQTT client:{} request to connect, clean session = {}", endpoint.clientIdentifier(), endpoint.isCleanSession());

        MqttAuth auth = endpoint.auth();
        if (auth == null) {
            endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED);
            return;
        }
        //mqtt连接认证信息：
        /*
         * mqttClientId: productKey_deviceName_model
         * mqttUserName: deviceName
         * mqttPassword: md5(产品密钥,mqttClientId)
         */
        String clientId = endpoint.clientIdentifier();
        String[] parts = clientId.split("_");
        if (parts.length < 3) {
            log.error("clientId:{}不正确", clientId);
            endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_CLIENT_IDENTIFIER_NOT_VALID);
            return;
        }

        log.info("MQTT client auth,clientId:{},username:{},password:{}",
                clientId, auth.getUsername(), auth.getPassword());

        String productKey = parts[0];
        String deviceName = parts[1];
        String gwModel = parts[2];
        if (!auth.getUsername().equals(deviceName)) {
            log.error("username:{}不正确", deviceName);
            endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USERNAME_OR_PASSWORD);
            return;
        }

        ThingProduct product = thingService.getProduct(productKey);
        if (product == null) {
            log.error("获取产品信息失败,productKey:{}", productKey);
            endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USERNAME_OR_PASSWORD);
            return;
        }

        String validPasswd = CodecUtil.md5Str(product.getProductSecret() + clientId);
        if (!validPasswd.equalsIgnoreCase(auth.getPassword())) {
            log.error("密码验证失败,期望值:{}", validPasswd);
            endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USERNAME_OR_PASSWORD);
            return;
        }

        //网关设备注册
        ActionResult result = thingService.post(
                pluginInfo.getPluginId(),
                fillAction(
                        DeviceRegister.builder()
                                .productKey(productKey)
                                .deviceName(deviceName)
                                .model(gwModel)
                                .version("1.0")
                                .build()
                )
        );
        if (result.getCode() != 0) {
            log.error("设备注册失败:{}", result);
            endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED);
            return;
        }

        //保存设备与连接关系
        endpointMap.put(deviceName, endpoint);
        MQTT_CONNECT_POOL.put(clientId, true);

        log.info("MQTT client keep alive timeout = {} ", endpoint.keepAliveTimeSeconds());

        endpoint.accept(false);

        endpoint.closeHandler((v) -> {
            // 网络不好时也会出发,但是设备仍然可以发消息
            log.warn("client connection closed,clientId:{}", clientId);
            if (Boolean.FALSE.equals(MQTT_CONNECT_POOL.get(clientId))) {
                MQTT_CONNECT_POOL.remove(clientId);
                return;
            }
            //下线
            offline(productKey, deviceName);
            DEVICE_ONLINE.clear();
            //删除设备与连接关系
            endpointMap.remove(deviceName);
        }).disconnectMessageHandler(disconnectMessage -> {
            log.info("Received disconnect from client, reason code = {}", disconnectMessage.code());
            if (!MQTT_CONNECT_POOL.get(clientId)) {
                return;
            }
            //下线
            offline(productKey, deviceName);
            //删除设备与连接关系
            endpointMap.remove(deviceName);
            MQTT_CONNECT_POOL.put(clientId, false);
            DEVICE_ONLINE.clear();
        }).subscribeHandler(subscribe -> {
            List<MqttSubAckReasonCode> reasonCodes = new ArrayList<>();
            for (MqttTopicSubscription s : subscribe.topicSubscriptions()) {
                log.info("Subscription for {},with QoS {}", s.topicName(), s.qualityOfService());
                try {
                    String topic = s.topicName();
                    ThingDevice device = getDevice(topic);
                    //添加设备对应连接
                    endpointMap.put(device.getDeviceName(), endpoint);
                    online(device.getProductKey(), device.getDeviceName());
                    reasonCodes.add(MqttSubAckReasonCode.qosGranted(s.qualityOfService()));
                } catch (Throwable e) {
                    log.error("subscribe failed,topic:" + s.topicName(), e);
                    reasonCodes.add(MqttSubAckReasonCode.NOT_AUTHORIZED);
                }
            }
            // ack the subscriptions request
            endpoint.subscribeAcknowledge(subscribe.messageId(), reasonCodes, MqttProperties.NO_PROPERTIES);
        }).unsubscribeHandler(unsubscribe -> {
            for (String topic : unsubscribe.topics()) {
                ThingDevice device = getDevice(topic);
                //删除设备对应连接
                endpointMap.remove(device.getDeviceName());
                //下线
                offline(device.getProductKey(), device.getDeviceName());
                DEVICE_ONLINE.remove(device.getDeviceName());
            }

            // ack the subscriptions request
            endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
        }).publishHandler(message -> {
            String topic = message.topicName();
            JsonObject payload = message.payload().toJsonObject();
            log.info("Received message:topic={},payload={}, with QoS {}", topic, payload,
                    message.qosLevel());

            if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
                endpoint.publishAcknowledge(message.messageId());
            } else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
                endpoint.publishReceived(message.messageId());
            }
            if (payload.isEmpty()) {
                return;
            }

            ThingDevice device = getDevice(topic);
            if (device == null) {
                return;
            }

            //有消息上报-设备上线
            online(device.getProductKey(), device.getDeviceName());

            if (!MQTT_CONNECT_POOL.get(clientId)) {
                //保存设备与连接关系
                endpointMap.put(deviceName, endpoint);
                MQTT_CONNECT_POOL.put(clientId, true);
                log.info("mqtt client reconnect success,clientId:{}", clientId);
            }

            try {
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
                    ActionResult regResult = thingService.post(
                            pluginInfo.getPluginId(),
                            fillAction(
                                    SubDeviceRegister.builder()
                                            .productKey(productKey)
                                            .deviceName(deviceName)
                                            .model(gwModel)
                                            .version("1.0")
                                            .subs(List.of(
                                                    DeviceRegister.builder()
                                                            .productKey(subPk)
                                                            .deviceName(subDn)
                                                            .model(params.getString("model"))
                                                            .version("1.0")
                                                            .build()
                                            ))
                                            .build()
                            )
                    );
                    if (regResult.getCode() == 0) {
                        //注册成功
                        reply(endpoint, topic, payload);
                    } else {
                        //注册失败
                        reply(endpoint, topic, new JsonObject(), regResult.getCode());
                    }
                    return;
                }

                if ("thing.event.property.post".equalsIgnoreCase(method)) {
                    //属性上报
                    action = PropertyReport.builder()
                            .params(params.getMap())
                            .build();
                    reply(endpoint, topic, payload);
                } else if (method.startsWith("thing.event.")) {
                    //事件上报
                    action = EventReport.builder()
                            .name(method.replace("thing.event.", ""))
                            .level(EventLevel.INFO)
                            .params(params.getMap())
                            .build();
                    reply(endpoint, topic, payload);
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
            } catch (Throwable e) {
                log.error("handler message failed,topic:" + message.topicName(), e);
            }
        }).publishReleaseHandler(endpoint::publishComplete);

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

    /**
     * 回复设备
     */
    private void reply(MqttEndpoint endpoint, String topic, JsonObject payload) {
        reply(endpoint, topic, payload, 0);
    }

    /**
     * 回复设备
     */
    private void reply(MqttEndpoint endpoint, String topic, JsonObject payload, int code) {
        Map<String, Object> payloadReply = new HashMap<>();
        payloadReply.put("id", payload.getString("id"));
        payloadReply.put("method", payload.getString("method") + "_reply");
        payloadReply.put("code", code);
        payloadReply.put("data", payload.getJsonObject("params"));

        endpoint.publish(topic + "_reply", JsonObject.mapFrom(payloadReply).toBuffer(), MqttQoS.AT_LEAST_ONCE, false, false);
    }

    private IDeviceAction fillAction(IDeviceAction action) {
        action.setId(UniqueIdUtil.newRequestId());
        action.setTime(System.currentTimeMillis());
        return action;
    }

    @Override
    public void stop() {
        for (MqttEndpoint endpoint : endpointMap.values()) {
            String clientId = endpoint.clientIdentifier();
            String[] parts = clientId.split("_");
            if (parts.length < 3) {
                continue;
            }

            //下线
            offline(parts[0], parts[1]);
            DEVICE_ONLINE.clear();
        }
        if(mqttServer!=null) {
            mqttServer.close();
        }
    }

    private void offline(String productKey, String deviceName) {
        thingService.post(
                pluginInfo.getPluginId(),
                fillAction(
                        DeviceStateChange.builder()
                                .productKey(productKey)
                                .deviceName(deviceName)
                                .state(DeviceState.OFFLINE)
                                .build()
                )
        );
    }

    public void publish(String deviceName, String topic, String msg) {
        MqttEndpoint endpoint = endpointMap.get(deviceName);
        if (endpoint == null) {
            throw new BizException(ErrCode.SEND_DESTINATION_NOT_FOUND);
        }
        Future<Integer> result = endpoint.publish(topic, Buffer.buffer(msg),
                MqttQoS.AT_LEAST_ONCE, false, false);
        result.onFailure(e -> log.error("public topic failed", e));
        result.onSuccess(integer -> log.info("publish success,topic:{},payload:{}", topic, msg));
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

}
