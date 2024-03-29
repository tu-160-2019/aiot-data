/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.es.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.model.rule.RuleLog;
import cc.iotkit.temporal.IRuleLogData;
import cc.iotkit.temporal.es.dao.RuleLogRepository;
import cc.iotkit.temporal.es.document.DocRuleLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class RuleLogDataImpl implements IRuleLogData {

    @Autowired
    private RuleLogRepository ruleLogRepository;

    @Override
    public void deleteByRuleId(String ruleId) {
        ruleLogRepository.deleteByRuleId(ruleId);
    }

    @Override
    public Paging<RuleLog> findByRuleId(String ruleId, int page, int size) {
        Page<DocRuleLog> paged = ruleLogRepository.findByRuleIdOrderByLogAtDesc(ruleId, Pageable.ofSize(size).withPage(page - 1));
        return new Paging<>(paged.getTotalElements(),
                paged.getContent().stream().map(o -> MapstructUtils.convert(o, RuleLog.class))
                        .collect(Collectors.toList()));
    }

    @Override
    public void add(RuleLog log) {
        ruleLogRepository.save(MapstructUtils.convert(log, DocRuleLog.class));
    }
}
