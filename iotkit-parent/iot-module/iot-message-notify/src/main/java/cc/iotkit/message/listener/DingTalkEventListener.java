package cc.iotkit.message.listener;

import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.message.config.VertxManager;
import cc.iotkit.message.event.MessageEvent;
import cc.iotkit.message.model.DingTalkConfig;
import cc.iotkit.message.model.DingTalkMessage;
import cc.iotkit.message.model.Message;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * author: 石恒
 * date: 2023-05-08 15:09
 * description:
 **/
@Slf4j
@Component
public class DingTalkEventListener implements MessageEventListener {
    WebClient client = WebClient.create(VertxManager.INSTANCE.getVertx());

    @Override
    @EventListener(classes = MessageEvent.class, condition = "#event.message.channel=='DingTalk'")
    public void doEvent(MessageEvent event) {
        Message message = event.getMessage();
        String channelConfig = message.getChannelConfig();
        DingTalkConfig dingTalkConfig = JsonUtils.parse(channelConfig, DingTalkConfig.class);

        DingTalkMessage qyWechatMessage = DingTalkMessage.builder()
                .msgtype("text")
                .text(DingTalkMessage.MessageContent.builder().content(message.getFormatContent()).build())
                .build();

        client.postAbs(dingTalkConfig.getDingTalkWebhook()).sendJson(qyWechatMessage)
                .onSuccess(response -> log.info("Received response with status code" + response.statusCode()))
                .onFailure(err -> log.error("Something went wrong " + err.getMessage()));
    }

}
