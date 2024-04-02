package cc.iotkit.manager.service;

import cc.iotkit.data.manager.IAlertConfigData;
import cc.iotkit.message.event.MessageEvent;
import cc.iotkit.message.listener.MessageEventListener;
import cc.iotkit.message.model.Message;
import cc.iotkit.model.alert.AlertConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author sjg
 */
@Slf4j
@Component
public class AlertMessageEventListener implements MessageEventListener {

    @Autowired
    private IAlertConfigData alertConfigData;

    @Autowired
    private AlertService alertService;

    @Override
    @EventListener(classes = MessageEvent.class)
    public void doEvent(MessageEvent event) {
        Message message = event.getMessage();
        AlertConfig alertConfig = alertConfigData.findById(message.getAlertConfigId());
        alertService.addAlert(alertConfig, message.getFormatContent());
    }
}
