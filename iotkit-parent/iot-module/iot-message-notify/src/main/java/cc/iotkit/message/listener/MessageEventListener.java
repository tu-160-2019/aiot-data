package cc.iotkit.message.listener;

import cc.iotkit.message.event.MessageEvent;

/**
 * author: 石恒
 * date: 2023-05-08 15:08
 * description:
 **/
public interface MessageEventListener {

    void doEvent(MessageEvent event);

}
