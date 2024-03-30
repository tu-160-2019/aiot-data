package cc.iotkit.ruleengine.action.tcp;

import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.common.utils.FIUtil;
import cc.iotkit.ruleengine.action.ScriptService;
import cc.iotkit.ruleengine.link.LinkFactory;
import cc.iotkit.ruleengine.link.LinkService;
import cc.iotkit.ruleengine.link.impl.TcpClientLink;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author huangwenl
 * @date 2022-12-14
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class TcpService extends ScriptService implements LinkService {
    private String host;
    private int port;

    public String execute(ThingModelMessage msg) {
        //执行转换脚本
        Map<String, Object> result = execScript(new TypeReference<>() {
        },msg);
        if (result == null) {
            log.warn("execScript result is null");
            return "execScript result is null";
        }
        boolean initResult = LinkFactory.initLink(getKey(), TcpClientLink.LINK_TYPE, getLinkConf());

        AtomicReference<String> data = new AtomicReference<>("");
        FIUtil.isTotF(initResult).handler(
                () -> LinkFactory.sendMsg(getKey(), result, data::set),
                () -> data.set("创建连接失败！")
        );
        return data.get();
    }

    @Override
    public String getKey() {
        return String.format("tcp_%s_%d", host, port);
    }

    @Override
    public String getLinkType() {
        return TcpClientLink.LINK_TYPE;
    }

    @Override
    public Map<String, Object> getLinkConf() {
        Map<String, Object> config = new HashMap<>();
        config.put(TcpClientLink.HOST, host);
        config.put(TcpClientLink.PORT, port);
        return config;
    }
}
