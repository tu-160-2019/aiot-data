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
public class QyWechatMessage implements Serializable {

    private String msgtype;

    private MessageContent text;

    @Data
    @Builder
    public static class MessageContent{
        private String content;
    }
}
