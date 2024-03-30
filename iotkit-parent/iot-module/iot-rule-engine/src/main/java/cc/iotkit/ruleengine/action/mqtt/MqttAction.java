package cc.iotkit.ruleengine.action.mqtt;

import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.ruleengine.action.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangwenl
 * @date 2022-11-10
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MqttAction implements Action<MqttService> {
    public static final String TYPE = "mqtt";


    private List<MqttService> services;

    @Override
    public String getType() {
        return TYPE;
    }


    @Override
    public List<String> execute(ThingModelMessage msg) {
        List<String> results = new ArrayList<>();
        for (MqttService service : services) {
            results.add(service.execute(msg));
        }
        return results;
    }
}
