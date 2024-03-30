package cc.iotkit.ruleengine.link;

import java.util.Map;

/**
 * @author huangwenl
 * @date 2022-11-11
 */
public interface LinkService {

    default boolean initLink(String ruleId) {
        return LinkFactory.initLink(ruleId, getKey(), getLinkType(), getLinkConf());
    }

    String getKey();

    String getLinkType();

    Map<String, Object> getLinkConf();
}
