package cc.iotkit.temporal;

import cc.iotkit.common.api.Paging;
import cc.iotkit.model.rule.RuleLog;

public interface IRuleLogData {

    void deleteByRuleId(String ruleId);

    Paging<RuleLog> findByRuleId(String ruleId, int page, int size);

    void add(RuleLog log);

}
