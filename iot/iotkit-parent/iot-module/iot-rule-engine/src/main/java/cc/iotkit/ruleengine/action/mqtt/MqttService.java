package cc.iotkit.ruleengine.action.mqtt;

import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.common.utils.FIUtil;
import cc.iotkit.ruleengine.action.ScriptService;
import cc.iotkit.ruleengine.link.LinkFactory;
import cc.iotkit.ruleengine.link.LinkService;
import cc.iotkit.ruleengine.link.impl.MqttClientLink;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author huangwenl
 * @date 2022-11-09
 */

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class MqttService extends ScriptService implements LinkService {

    private String username;
    private String password;
    private String host;
    private int port;

    public String execute(ThingModelMessage msg) {
        //执行转换脚本
        Map<String, Object> result = execScript(new TypeReference<>() {
        }, msg);
        if (result == null) {
            log.warn("execScript result is null");
            return "execScript result is null";
        }
        boolean initResult = LinkFactory.initLink(getKey(), MqttClientLink.LINK_TYPE, getLinkConf());

        AtomicReference<String> data = new AtomicReference<>("");
        FIUtil.isTotF(initResult).handler(
                () -> LinkFactory.sendMsg(getKey(), result, data::set),
                () -> data.set("创建连接失败！")
        );
        return data.get();
    }

    @Override
    public String getKey() {
        return String.format("mqtt_%s_%d", host, port);
    }

    @Override
    public String getLinkType() {
        return MqttClientLink.LINK_TYPE;
    }

    @Override
    public Map<String, Object> getLinkConf() {
        Map<String, Object> config = new HashMap<>();
        config.put(MqttClientLink.HOST, host);
        config.put(MqttClientLink.PORT, port);
        config.put(MqttClientLink.USERNAME, username);
        config.put(MqttClientLink.PASSWORD, password);
        return config;
    }
}
