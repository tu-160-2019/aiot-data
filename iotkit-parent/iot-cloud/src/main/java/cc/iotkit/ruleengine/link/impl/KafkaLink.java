package cc.iotkit.ruleengine.link.impl;

import cc.iotkit.common.utils.FIUtil;
import cc.iotkit.ruleengine.link.BaseSinkLink;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * kafka 连接器
 * 支持自定义topic 和 分区, ack
 * k-v 只支持String
 *
 * @author huangwenl
 * @date 2022-11-11
 */
@Slf4j
public class KafkaLink implements BaseSinkLink {
    public static final String LINK_TYPE = "kafka";
    public static final String TOPIC = "topic";
    public static final String PAYLOAD = "payload";
    public static final String PARTITION = "partition";

    public static final String SERVERS = "servers";
    public static final String ACK = "ack";

    private KafkaProducer<String, String> producer;
    private Consumer<Void> closeHandler;

    @Override
    public boolean open(Map<String, Object> config) {
        try {
            AtomicReference<Vertx> vertx = new AtomicReference<>();
            FIUtil.isTotF(Vertx.currentContext() == null).handler(
                    () -> vertx.set(Vertx.vertx()),
                    () -> vertx.set(Vertx.currentContext().owner())
            );
            Map<String, String> kafkaConfig = new HashMap<>();
            kafkaConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, (String) config.get(SERVERS));
            kafkaConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            kafkaConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            kafkaConfig.put(ProducerConfig.ACKS_CONFIG, (String) config.get(ACK));
            producer = KafkaProducer.create(vertx.get(), kafkaConfig);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void send(Map<String, Object> msg, Consumer<String> consumer) {
        AtomicReference<KafkaProducerRecord<String, String>> record = new AtomicReference<>();
        FIUtil.isTotF(msg.containsKey(PARTITION)).handler(
                () -> record.set(KafkaProducerRecord.create((String) msg.get(TOPIC), "", msg.get(PAYLOAD).toString(), (Integer) msg.get(PARTITION))),
                () -> record.set(KafkaProducerRecord.create((String) msg.get(TOPIC), msg.get(PAYLOAD).toString())));
        // 同步等待结果
        try {
            Future<Void> write = producer.write(record.get());
            write.toCompletionStage().toCompletableFuture().get();
            FIUtil.isTotF(write.succeeded()).handler(
                    () -> consumer.accept(String.format("kafka,topic[%s],发送成功:%s", msg.get(TOPIC), msg.get(PAYLOAD).toString())),
                    () -> consumer.accept(String.format("kafka,topic[%s],发送失败:%s", msg.get(TOPIC), msg.get(PAYLOAD).toString()))
            );
        } catch (Exception e) {
            e.printStackTrace();
            log.error("kafka,topic[{}],发送异常:{}", msg.get(TOPIC), msg.get(PAYLOAD).toString(), e);
            consumer.accept(String.format("kafka,topic[%s],发送异常:%s", msg.get(TOPIC), msg.get(PAYLOAD).toString()));
        }

    }

    @Override
    public void close() {
        try {
            producer.close();
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
