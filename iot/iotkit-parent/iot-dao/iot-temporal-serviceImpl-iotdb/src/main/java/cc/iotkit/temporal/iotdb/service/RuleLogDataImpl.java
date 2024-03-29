/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.iotdb.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.model.rule.RuleLog;
import cc.iotkit.temporal.IRuleLogData;
import org.springframework.stereotype.Service;

@Service
public class RuleLogDataImpl implements IRuleLogData {

    @Override
    public void deleteByRuleId(String ruleId) {
    }

    @Override
    public Paging<RuleLog> findByRuleId(String ruleId, int page, int size) {
        return new Paging<>();
    }

    @Override
    public void add(RuleLog log) {
    }
}
