package cc.iotkit.message.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * author: 石恒
 * date: 2023-05-08 15:58
 * description:
 **/
@Data
@Builder
public class DingTalkMessage implements Serializable {

    /**
     * 消息文本类型 目前只支持文本
     */
    private String msgtype;

    /**
     * 文本消息
     */
    private MessageContent text;

    @Data
    @Builder
    public static class MessageContent {

        /**
         * 文本内容
         */
        private String content;

    }
}
