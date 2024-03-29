package cc.iotkit.message.service;

import cc.iotkit.message.event.MessageEvent;
import cc.iotkit.message.model.Message;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * author: 石恒
 * date: 2023-05-08 16:02
 * description:
 **/
@Service
public class MessageService {
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public void sendMessage(Message message) {
        applicationEventPublisher.publishEvent(new MessageEvent(message));
    }

}
