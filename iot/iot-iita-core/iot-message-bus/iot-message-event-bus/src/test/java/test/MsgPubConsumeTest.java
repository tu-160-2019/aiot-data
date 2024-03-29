package test;

import cc.iotkit.mq.ConsumerHandler;
import cc.iotkit.vertx.VertxMqConsumer;
import cc.iotkit.vertx.VertxMqProducer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class MsgPubConsumeTest {


    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        VertxMqConsumer.MqConsumerVerticle<Bean1> consumerVerticle = new VertxMqConsumer.MqConsumerVerticle<>(Bean1.class);
        vertx.deployVerticle(consumerVerticle, new Handler<AsyncResult<String>>() {
            @Override
            public void handle(AsyncResult<String> stringAsyncResult) {
                consumerVerticle.consume("aaa", new ConsumerHandler<Bean1>() {
                    @Override
                    public void handler(Bean1 msg) {
                        System.out.println("c1:" + msg.getName());
                    }
                });
                consumerVerticle.consume("aaa", new ConsumerHandler<Bean1>() {
                    @Override
                    public void handler(Bean1 msg) {
                        System.out.println("c2:" + msg.getName());
                    }
                });
            }
        });


        VertxMqProducer.MqProducerVerticle<Bean1> producerVerticle = new VertxMqProducer.MqProducerVerticle<>(Bean1.class);
        vertx.deployVerticle(producerVerticle, new Handler<AsyncResult<String>>() {
            @Override
            public void handle(AsyncResult<String> stringAsyncResult) {
                producerVerticle.publish("aaa", new Bean1("test", 1));
                System.out.println("publish");
            }
        });
    }

    public static class Bean1 {
        private String name;
        private int age;

        public Bean1() {
        }

        public Bean1(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

}
