package cc.iotkit.ruleengine.link;

import cc.iotkit.common.utils.FIUtil;
import cc.iotkit.ruleengine.link.impl.KafkaLink;
import cc.iotkit.ruleengine.link.impl.MqttClientLink;
import cc.iotkit.ruleengine.link.impl.TcpClientLink;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author huangwenl
 * @date 2022-11-10
 */
@Slf4j
public class LinkFactory {

    private static final Map<String, BaseSinkLink> linkMap = new ConcurrentHashMap<>();
    private static final Map<String, Set<String>> ruleLink = new ConcurrentHashMap<>();

    /**
     * 发送消息前，初始化连接
     *
     * @param key    连接标识
     * @param type   连接类型
     * @param config 连接配置
     * @return
     */
    public static boolean initLink(String key, String type, Map<String, Object> config) {
        AtomicBoolean exist = new AtomicBoolean(false);
        FIUtil.isTotF(linkMap.containsKey(key)).handler(
                () -> exist.set(true),
                () -> exist.set(buildLink(key, type, config))
        );
        return exist.get();
    }

    /**
     * 启动规则时，初始化连接
     *
     * @param ruleId
     * @param ruleId 规则ID
     * @param key    连接标识
     * @param type   连接类型
     * @param config 连接配置
     * @return
     */
    public static boolean initLink(String ruleId, String key, String type, Map<String, Object> config) {
        boolean result = initLink(key, type, config);
        if (result) {
            Set<String> linkKeys = Optional.ofNullable(ruleLink.get(ruleId)).orElse(new HashSet<>());
            linkKeys.add(key);
            ruleLink.put(ruleId, linkKeys);
        }
        return result;
    }

    /**
     * 注册连接
     *
     * @param key  连接标识
     * @param link 连接
     */
    public static void register(String key, BaseSinkLink link) {
        log.info("连接器{}注册连接", key);
        linkMap.put(key, link);

        link.closeHandler(Void -> {
            linkMap.remove(key);
            log.info("连接器{}断开连接", key);
        });
    }

    /**
     * 发送消息
     *
     * @param key      连接标识
     * @param msg      消息
     * @param consumer 发送回调
     */
    public static void sendMsg(String key, Map<String, Object> msg, Consumer<String> consumer) {
        try {
            BaseSinkLink sinkLink = linkMap.get(key);
            FIUtil.isTotF(sinkLink != null).handler(() -> sinkLink.send(msg, consumer),
                    () -> consumer.accept(String.format("key:%s, 没有连接！", key)));
        } catch (Exception e) {
            e.printStackTrace();
            consumer.accept(String.format("key:%s,发送异常:%s", key, e.getMessage()));
        }

    }

    /**
     * 停用规则
     *
     * @param ruleId
     */
    public static void ruleClose(String ruleId) {
        Set<String> linkKeys = ruleLink.remove(ruleId);
        // 排除其他规则也在用这个 link的
        if (linkKeys != null && !linkKeys.isEmpty()) {
            Set<String> existKey = new HashSet<>();
            ruleLink.forEach((key, value) -> existKey.addAll(value));
            linkKeys.removeAll(existKey);
            linkKeys.forEach(LinkFactory::close);
        }
    }

    /**
     * 关闭连接
     *
     * @param key
     */
    public static void close(String key) {
        BaseSinkLink link = linkMap.get(key);
        if (link != null) {
            link.close();
        }
    }

    private static boolean buildLink(String key, String type, Map<String, Object> conf) {
        boolean success = false;
        BaseSinkLink link = null;
        switch (type) {
            case MqttClientLink.LINK_TYPE:
                link = new MqttClientLink();
                break;
            case KafkaLink.LINK_TYPE:
                link = new KafkaLink();
                break;
            case TcpClientLink.LINK_TYPE:
                link = new TcpClientLink();
                break;
        }
        if (link != null) {
            success = link.open(conf);
        }
        if (success) {
            register(key, link);
        }
        return success;
    }

}
