package cc.iotkit.ruleengine.link;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author huangwenl
 * @date 2022-11-10
 */
public interface BaseSinkLink {

    /**
     * 建立连接
     * @param config  连接配置信息
     */
    boolean open(Map<String, Object> config);

    /**
     * 发送消息
     * @param msg 消息内容
     * @param consumer  发送回调
     */
    void send(Map<String, Object> msg, Consumer<String> consumer);

    /**
     * 关闭连接
     */
    void close();

    /**
     * 连接监听
     * @param closeHandler
     */
    void closeHandler(Consumer<Void> closeHandler);
}
