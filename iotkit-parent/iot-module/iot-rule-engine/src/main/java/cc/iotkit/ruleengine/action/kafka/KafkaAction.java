package cc.iotkit.ruleengine.action.kafka;

import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.ruleengine.action.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangwenl
 * @date 2022-11-11
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class KafkaAction implements Action<KafkaService> {

    public static final String TYPE = "kafka";


    private List<KafkaService> services;

    @Override
    public String getType() {
        return TYPE;
    }


    @Override
    public List<String> execute(ThingModelMessage msg) {
        List<String> results = new ArrayList<>();
        for (KafkaService service : services) {
            results.add(service.execute(msg));
        }
        return results;
    }
}
