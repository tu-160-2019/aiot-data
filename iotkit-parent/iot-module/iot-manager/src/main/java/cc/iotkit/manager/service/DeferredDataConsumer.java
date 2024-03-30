/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.manager.service;

import cc.iotkit.common.constant.Constants;
import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.common.websocket.holder.WebSocketSessionHolder;
import cc.iotkit.common.websocket.utils.WebSocketUtils;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.space.SpaceDevice;
import cc.iotkit.mq.ConsumerHandler;
import cc.iotkit.mq.MqConsumer;
import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 长连接推送消息消费
 */
@Slf4j
@Component
public class DeferredDataConsumer implements ConsumerHandler<ThingModelMessage>, Runnable {

    private final Map<String, Set<String>> topicConsumers = new ConcurrentHashMap<>();
    private final Map<String, DeferredResultInfo> consumerDeferred = new ConcurrentHashMap<>();
    private final DelayQueue<DelayedPush> delayedPushes = new DelayQueue<>();

    @Autowired
    private ISpaceDeviceService deviceServiceImpl;

    @Autowired
    private MqConsumer<ThingModelMessage> thingModelMessageConsumer;

    @PostConstruct
    public void init() {
        thingModelMessageConsumer.consume(Constants.THING_MODEL_MESSAGE_TOPIC, this);
        Executors.newCachedThreadPool().submit(this);
    }

    public <T> DeferredResult<T> newConsumer(String consumerId, String topic) {
        topicConsumers.putIfAbsent(topic, new HashSet<>());
        Set<String> consumers = topicConsumers.get(topic);
        consumers.add(consumerId);

        String consumerKey = getConsumerKey(consumerId, topic);
        DeferredResult<T> result = new DeferredResult<>(10000L, new DeviceInfo());
        DeferredResultInfo resultInfo = new DeferredResultInfo(result, false);
        result.onCompletion(() -> resultInfo.setCompleted(true));
        result.onTimeout(() -> resultInfo.setCompleted(true));

        consumerDeferred.put(consumerKey, resultInfo);
        return result;
    }

    public <T> void publish(String topic, T msg, boolean republish) {
        Set<String> consumers = topicConsumers.get(topic);
        if (consumers == null) {
            return;
        }
        for (String consumer : consumers) {
            String consumerKey = getConsumerKey(consumer, topic);
            DeferredResultInfo result = consumerDeferred.get(consumerKey);
            if (result == null) {
                continue;
            }

            //如果已经推送完成了，等待1秒再尝试发送，让客户端有时间重连
            if (!republish && result.isCompleted() && !result.isExpired()) {
                delayedPushes.offer(new DelayedPush<>(topic, System.currentTimeMillis(), msg),
                        3, TimeUnit.SECONDS);
            } else {
                log.info("push {} to {},msg:{}", topic, consumer, JsonUtils.toJsonString(msg));
                result.getDeferredResult().setResult(msg);
            }
        }
    }

    public <T> void publish(String topic, T msg) {
        publish(topic, msg, false);
    }

    private String getConsumerKey(String consumerId, String topic) {
        return consumerId + topic;
    }

    @Override
    public void handler(ThingModelMessage msg) {
        String type = msg.getType();
        String identifier = msg.getIdentifier();
        //属性上报和上下线消息
        if ((ThingModelMessage.TYPE_PROPERTY.equals(type) && "report".equals(identifier)) ||
                ThingModelMessage.TYPE_STATE.equals(type)) {
            publish(Constants.HTTP_CONSUMER_DEVICE_INFO_TOPIC + msg.getDeviceId(),
                    msg);
            sendWebSocket(msg);
        }
    }

    public void sendWebSocket(ThingModelMessage msg){
        SpaceDevice deviceInfo=deviceServiceImpl.findByDeviceId(msg.getDeviceId());
        if(ObjectUtil.isNotNull(deviceInfo)&&ObjectUtil.isNotNull(deviceInfo.getCreateBy())&&WebSocketSessionHolder.existSession(deviceInfo.getCreateBy())){
            WebSocketUtils.sendMessage(deviceInfo.getCreateBy(),JsonUtils.toJsonString(msg));
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                DelayedPush<ThingModelMessage> delayedPush = delayedPushes.take();
                ThingModelMessage modelMessage = delayedPush.getMsg();
                publish(delayedPush.getTopic(), modelMessage, true);
            } catch (Throwable e) {
                log.error("delayed push error", e);
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeferredResultInfo {
        private DeferredResult deferredResult;
        private boolean completed;
        private long completedTime;

        public DeferredResultInfo(DeferredResult deferredResult, boolean completed) {
            this.deferredResult = deferredResult;
            this.completed = completed;
            this.completedTime = System.currentTimeMillis();
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
            this.completedTime = System.currentTimeMillis();
        }

        public boolean isExpired() {
            //完成超过3后视为过期，客户端可能已断开
            return completed && System.currentTimeMillis() - completedTime > 3 * 1000L;
        }
    }

    @Data
    public static class DelayedPush<T> implements Delayed {

        private String topic;

        private T msg;

        private long addTime;

        public DelayedPush(String topic, long addTime, T message) {
            this.topic = topic;
            this.addTime = addTime;
            this.msg = message;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(addTime - System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            long diff = o.getDelay(TimeUnit.NANOSECONDS) - getDelay(TimeUnit.NANOSECONDS);
            if (diff == 0) {
                return 0;
            }
            return diff > 0 ? 1 : -1;
        }
    }

}
