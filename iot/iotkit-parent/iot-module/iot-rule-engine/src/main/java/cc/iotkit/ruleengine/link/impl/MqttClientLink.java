package cc.iotkit.ruleengine.link.impl;

import cc.iotkit.common.utils.FIUtil;
import cc.iotkit.ruleengine.link.BaseSinkLink;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author huangwenl
 * @date 2022-11-10
 */
@Slf4j
public class MqttClientLink implements BaseSinkLink {
    public static final String LINK_TYPE = "mqtt";
    public static final String TOPIC = "topic";
    public static final String PASSWORD = "password";
    public static final String USERNAME = "username";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String PAYLOAD = "payload";

    private MqttClient mqttClient;
    private Consumer<Void> closeHandler;
    private MqttClientOptions clientOptions;
    private String host;
    private int port;
    private boolean connecting;


    @Override
    public boolean open(Map<String, Object> config) {
        try {
            AtomicReference<Vertx> vertx = new AtomicReference<>();
            FIUtil.isTotF(Vertx.currentContext() == null).handler(
                    () -> vertx.set(Vertx.vertx()),
                    () -> vertx.set(Vertx.currentContext().owner())
            );
            clientOptions = new MqttClientOptions();
            clientOptions.setUsername((String) config.get(USERNAME));
            clientOptions.setPassword((String) config.get(PASSWORD));
            mqttClient = MqttClient.create(vertx.get(), clientOptions);
            host = (String) config.get(HOST);
            port = (int) config.get(PORT);
            mqttClient = MqttClient.create(vertx.get(), clientOptions);
            connecting = true;
            mqttClient.connect(port, host,
                    s -> {
                        connecting = false;
                        if (!s.succeeded()) {
                            closeHandler.accept(null);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            connecting = false;
            return false;
        }
        return true;
    }

    @Override
    public void send(Map<String, Object> msg, Consumer<String> consumer) {
        FIUtil.isTotF(mqttClient.isConnected()).handler(
                () -> {
                    Future<Integer> publish = mqttClient.publish((String) msg.get(TOPIC),
                            Buffer.buffer(msg.get(PAYLOAD).toString()),
                            MqttQoS.AT_MOST_ONCE, false, false);
                    try {
                        publish.toCompletionStage().toCompletableFuture().get();
                        FIUtil.isTotF(publish.succeeded()).handler(
                                () -> consumer.accept(String.format("mqtt,topic[%s],发送成功:%s", msg.get(TOPIC), msg.get(PAYLOAD).toString())),
                                () -> consumer.accept(String.format("mqtt,topic[%s],发送失败:%s", msg.get(TOPIC), msg.get(PAYLOAD).toString()))
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                        consumer.accept(String.format("mqtt,topic[%s],发送异常:%s", msg.get(TOPIC), msg.get(PAYLOAD).toString()));
                    }
                },
                () -> {
                    consumer.accept("mqtt,连接断开,发送失败");
                    if (!connecting) {
                        log.info("mqtt重连！");
                        connecting = true;
                        mqttClient.connect(port, host, s -> connecting = false);
                    }
                });
    }

    @Override
    public void close() {
        try {
            mqttClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeHandler.accept(null);
        }
    }

    @Override
    public void closeHandler(Consumer<Void> consumer) {
        this.closeHandler = consumer;
    }
}
