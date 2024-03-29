package cc.iotkit.common.websocket.listener;

import cc.iotkit.common.websocket.utils.WebSocketUtils;
import cn.hutool.core.collection.CollUtil;
import cc.iotkit.common.websocket.holder.WebSocketSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;

/**
 * WebSocket 主题订阅监听器
 *
 * @author zendwang
 */
@Slf4j
public class WebSocketTopicListener implements ApplicationRunner, Ordered {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        WebSocketUtils.subscribeMessage((message) -> {
            log.info("WebSocket主题订阅收到消息session keys={}  message={}！", message.getSessionKeys(), message.getMessage());
            if (CollUtil.isNotEmpty(message.getSessionKeys())) {
                message.getSessionKeys().forEach(key -> {
                    if (WebSocketSessionHolder.existSession(key)) {
                        WebSocketUtils.sendMessage(key, message.getMessage());
                    }
                });
            }
        });
        log.info("初始化WebSocket主题订阅监听器成功");
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
