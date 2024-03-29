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


import cc.iotkit.test.mqtt.model.Request;
import cc.iotkit.test.mqtt.model.Response;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.messages.MqttPublishMessage;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Data
public class MessageHandler implements Handler<MqttPublishMessage> {

    private MqttClient client;
    private Gateway gateway;
    private Consumer<Device> deviceOnlineListener;

    public MessageHandler(MqttClient client, Gateway gateway, Consumer<Device> deviceOnlineListener) {
        this.client = client;
        this.gateway = gateway;
        this.deviceOnlineListener = deviceOnlineListener;
    }

    @SneakyThrows

    @Override
    public void handle(MqttPublishMessage msg) {
        try {
            String topic = msg.topicName();
            String payload = msg.payload().toString();

            log.info("received msg,topic:{},payload:{}", topic, payload);

            if (topic.endsWith("register_reply")) {
                Response response = Json.decodeValue(payload, Response.class);
                //子设备注册成功
                if (response.getCode() == 0) {
                    Map<String, Object> data = response.getData();
                    String productKey = data.get("productKey").toString();
                    String deviceName = data.get("deviceName").toString();
                    if (StringUtils.isBlank(productKey)) {
                        deviceOnlineListener.accept(new Device(productKey, "", deviceName, ""));
                        return;
                    }

                    //订阅子设备消息
                    String subTopic = String.format("/sys/%s/%s/c/#", productKey, deviceName);
                    log.info("subscribe topic:{}", subTopic);
                    client.subscribe(subTopic, 1, r -> {
                        if (deviceOnlineListener != null) {
                            deviceOnlineListener.accept(new Device(productKey, "", deviceName, ""));
                        }
                    });
                }
            }

            if (topic.endsWith("_reply")) {
                return;
            }
            Request request = Json.decodeValue(payload, Request.class);

            Response response = new Response(request.getId(), 0, request.getMethod(), new HashMap<>());
            client.publish(topic.replace("/c/", "/s/") + "_reply",
                    Buffer.buffer(Json.encode(response)), MqttQoS.AT_LEAST_ONCE, false, false);

            //属性设置后上报属性
            String setTopic = "/c/service/property/set";
            if (topic.endsWith(setTopic)) {
                request.setId(UUID.randomUUID().toString());
                client.publish(topic.replace(setTopic, "/s/event/property/post"),
                        Buffer.buffer(Json.encode(request)), MqttQoS.AT_LEAST_ONCE, false, false);
            }
        } catch (Throwable e) {
            log.info("receive msg error", e);
        }
    }


}
