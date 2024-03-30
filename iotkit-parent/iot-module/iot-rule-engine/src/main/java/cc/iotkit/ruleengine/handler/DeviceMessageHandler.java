package cc.iotkit.ruleengine.handler;


import cc.iotkit.common.thing.ThingModelMessage;

public interface DeviceMessageHandler {

    void handle(ThingModelMessage message);

}
