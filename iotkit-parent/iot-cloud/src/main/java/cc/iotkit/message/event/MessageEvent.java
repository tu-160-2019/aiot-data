package cc.iotkit.message.event;

import cc.iotkit.message.model.Message;
import org.springframework.context.ApplicationEvent;

/**
 * author: 石恒
 * date: 2023-05-11 14:14
 * description:
 **/

public class MessageEvent extends ApplicationEvent {
    private Message message;

    public MessageEvent(Message message) {
        super(message);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
