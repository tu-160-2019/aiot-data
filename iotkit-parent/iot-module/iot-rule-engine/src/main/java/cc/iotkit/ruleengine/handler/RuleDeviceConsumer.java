/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.ruleengine.handler;


import cc.iotkit.common.constant.Constants;
import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.common.utils.ThreadUtil;
import cc.iotkit.mq.ConsumerHandler;
import cc.iotkit.mq.MqConsumer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Slf4j
public class RuleDeviceConsumer implements ConsumerHandler<ThingModelMessage>, ApplicationContextAware {

    private final List<DeviceMessageHandler> handlers = new ArrayList<>();
    private ScheduledThreadPoolExecutor messageHandlerPool;

    @SneakyThrows
    public RuleDeviceConsumer(MqConsumer<ThingModelMessage> consumer) {
        consumer.consume(Constants.THING_MODEL_MESSAGE_TOPIC, this);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, DeviceMessageHandler> handlerMap = applicationContext.getBeansOfType(DeviceMessageHandler.class);
        messageHandlerPool = ThreadUtil.newScheduled(handlerMap.size() * 2, "messageHandler");
        this.handlers.addAll(handlerMap.values());
    }

    @SneakyThrows
    @Override
    public void handler(ThingModelMessage msg) {
        log.info("received thing model message:{}", msg);
        try {
            for (DeviceMessageHandler handler : this.handlers) {
                messageHandlerPool.submit(() -> {
                    try {
                        if (!(msg.getData() instanceof Map)) {
                            msg.setData(new HashMap<>());
                        }
                        handler.handle(msg);
                    } catch (Throwable e) {
                        log.error("handler message error", e);
                    }
                });
            }
        } catch (Throwable e) {
            log.error("rule device message process error", e);
        }
    }

}
