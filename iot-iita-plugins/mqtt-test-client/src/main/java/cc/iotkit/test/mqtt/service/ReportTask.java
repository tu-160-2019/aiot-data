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
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.mqtt.MqttClient;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ReportTask {

    private final MqttClient client;
    private final Map<String, Callable<Request>> taskMap = new HashMap<>();
    private ScheduledExecutorService taskService = null;

    public ReportTask(MqttClient client) {
        this.client = client;
    }

    public void start(int interval) {
        if (taskService == null) {
            taskService = Executors.newScheduledThreadPool(1);
            taskService.scheduleWithFixedDelay(this::send, 3, interval, TimeUnit.SECONDS);
        }
    }

    private void send() {
        taskMap.forEach((topic, action) -> {
            try {
                Request request = action.call();
                if (request == null) {
                    return;
                }
                if (!client.isConnected()) {
                    return;
                }
                String msg = Json.encode(request);
                log.info("send msg,topic:{},payload:{}", topic, msg);
                client.publish(topic, Buffer.buffer(msg), MqttQoS.AT_LEAST_ONCE, false, false);

            } catch (Throwable e) {
                log.error("send error", e);
            }
        });
    }

    public void addTask(String topic, Callable<Request> callable) {
        taskMap.put(topic, callable);
    }
}
