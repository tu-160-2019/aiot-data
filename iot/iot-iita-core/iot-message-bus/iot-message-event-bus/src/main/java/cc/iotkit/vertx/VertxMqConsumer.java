/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.vertx;

import cc.iotkit.mq.ConsumerHandler;
import cc.iotkit.mq.MqConsumer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class VertxMqConsumer<T> implements MqConsumer<T> {

    private final MqConsumerVerticle<T> consumerVerticle;

    private final CountDownLatch countDownLatch = new CountDownLatch(4);

    @SneakyThrows
    public VertxMqConsumer(Class<T> cls) {
        consumerVerticle = new MqConsumerVerticle<>(cls);
        for (int i = 0; i < 4; i++) {
            VertxManager.getVertx().deployVerticle(consumerVerticle,new DeploymentOptions().setWorker(true), stringAsyncResult -> countDownLatch.countDown());
        }
        //等待初始化穿完成
        countDownLatch.await();
    }

    @Override
    public void consume(String topic, ConsumerHandler<T> handler) {
        consumerVerticle.consume(topic, handler);
    }

    public static class MqConsumerVerticle<T> extends AbstractVerticle {

        private final Class<T> cls;
        private EventBus eventBus;

        public MqConsumerVerticle(Class<T> cls) {
            this.cls = cls;
        }

        @Override
        public void start() {
            eventBus = vertx.eventBus();
            eventBus.registerCodec(new BeanCodec<>(cls));
        }

        public void consume(String topic, ConsumerHandler<T> handler) {
            eventBus.consumer(topic, (Handler<Message<T>>) msg -> handler.handler(msg.body()));
        }
    }

}
