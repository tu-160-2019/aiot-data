/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.test.mqtt.service;


import cc.iotkit.test.mqtt.config.MqttConfig;
import cc.iotkit.test.mqtt.model.Request;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mqtt.messages.MqttConnAckMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class Gateway extends Device {

    private List<Device> subDevices = new ArrayList<>();

    private Consumer<Device> deviceOnlineListener;

    private MqttClient client;

    private boolean isConnecting;

    private boolean registered;

    private MqttConfig mqttConfig;

    public Gateway(MqttConfig mqttConfig,String productKey, String productSecret, String deviceName) {
        super(productKey, productSecret, deviceName, "GW01");
        this.mqttConfig=mqttConfig;
    }

    @SneakyThrows
    public void start() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(this::connect, 0, 3, TimeUnit.SECONDS);
    }

    private void connect() {
        if (client != null && client.isConnected()) {
            return;
        }

        if (isConnecting) {
            return;
        }

        String clientId = String.format("%s_%s_%s", productKey, deviceName, getModel());

        try {
            isConnecting = true;
            MqttClientOptions options = new MqttClientOptions();
            options.setUsername(this.deviceName);
            options.setPassword(DigestUtils.md5Hex(getProductSecret() + clientId));
            options.setCleanSession(true);
            options.setKeepAliveInterval(30);
            options.setClientId(clientId);
            options.setReconnectInterval(3000);
            options.setReconnectAttempts(100);

            client = MqttClient.create(Vertxs.getVertx(), options);

            CountDownLatch countDownLatch = new CountDownLatch(1);
            client.connect(mqttConfig.getPort(), mqttConfig.getHost(), s -> {
                if (s.succeeded()) {
                    log.info("mqtt connected,clientId:{}", clientId);
                    countDownLatch.countDown();
                    registered = true;
                } else {
                    log.info("mqtt connect failed,clientId:{}", clientId);
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            if(!registered){
                return;
            }

            // 订阅
            String topic = String.format("/sys/%s/%s/c/#", productKey, deviceName);
            log.info("subscribe topic:{}", topic);

            client.subscribe(topic, 1, r -> {
                //配置获取
//                String configGetTopic = String.format("/sys/%s/%s/s/config/get", productKey, deviceName);
//                Request configRequest = new Request();
//                configRequest.setId(UUID.randomUUID().toString());
//                String configPayload = JsonUtils.toJsonString(configRequest);
//                client.publish(configGetTopic, Buffer.buffer(configPayload), MqttQoS.AT_LEAST_ONCE, false, false);
//                log.info("publish message,topic:{},payload:{}", configGetTopic, configPayload);

                //注册子设备
                for (Device subDevice : subDevices) {
                    log.info("start register sub device,pk:{},dn:{}", subDevice.getProductKey(), subDevice.getDeviceName());
                    Request request = new Request();
                    request.setId(UUID.randomUUID().toString());
                    request.setParams(subDevice);
                    request.setMethod("thing.lifetime.register");
                    String registerTopic = String.format("/sys/%s/%s/s/register", productKey, deviceName);
                    String payload = Json.encode(request);
                    client.publish(registerTopic, Buffer.buffer(payload), MqttQoS.AT_LEAST_ONCE, false, false);
                    log.info("publish message,topic:{},payload:{}", registerTopic, payload);
                }
            });

            client.publishHandler(new MessageHandler(client, this, deviceOnlineListener));

            client.closeHandler((v) -> {
                log.info("{} closed,reconnecting...", deviceName);
                client.disconnect();
            });

        } catch (Throwable e) {
            log.error("connect mqtt-broker error", e);
        } finally {
            isConnecting = false;
        }
    }

    public void addSubDevice(String productKey, String productSecret, String deviceName, String model) {
        subDevices.add(new Device(productKey, productSecret, deviceName, model));
    }

    public void onDeviceOnline(Consumer<Device> listener) {
        this.deviceOnlineListener = listener;
    }


    public static class OnConnected implements Handler<AsyncResult<MqttConnAckMessage>> {

        @Override
        public void handle(AsyncResult<MqttConnAckMessage> mqttConnAckMessageAsyncResult) {

        }
    }

}
