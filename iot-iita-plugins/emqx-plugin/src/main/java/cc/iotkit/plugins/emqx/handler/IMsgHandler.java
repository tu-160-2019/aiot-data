package cc.iotkit.plugins.emqx.handler;

import io.vertx.core.json.JsonObject;

/**
 * @author sjg
 */
public interface IMsgHandler {

    void handle(String topic, JsonObject payload);

}
