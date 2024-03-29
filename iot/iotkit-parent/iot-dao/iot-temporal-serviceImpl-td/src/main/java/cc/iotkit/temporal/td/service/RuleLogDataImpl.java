/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.td.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.model.rule.RuleLog;
import cc.iotkit.temporal.IRuleLogData;
import cc.iotkit.temporal.td.dao.TdTemplate;
import cc.iotkit.temporal.td.dm.TableManager;
import cc.iotkit.temporal.td.model.TbRuleLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RuleLogDataImpl implements IRuleLogData {

    @Autowired
    private TdTemplate tdTemplate;

    @Override
    public void deleteByRuleId(String ruleId) {
        tdTemplate.update("delete from rule_log where rule_id=? and time<=NOW()", ruleId);
    }

    @Override
    public Paging<RuleLog> findByRuleId(String ruleId, int page, int size) {
        String sql = "select time,state1,content,success,rule_id from rule_log where rule_id=? " +
                "order by time desc limit %d offset %d";
        sql = String.format(sql, size, (page - 1) * size);
        List<TbRuleLog> ruleLogs = tdTemplate.query(sql, new BeanPropertyRowMapper<>(TbRuleLog.class), ruleId);

        sql = "select count(*) from rule_log where rule_id=?";
        List<Long> counts = tdTemplate.queryForList(sql, Long.class, ruleId);

        return new Paging<>(!counts.isEmpty() ? counts.get(0) : 0, ruleLogs.stream().map(r ->
                        new RuleLog(r.getTime().toString(), ruleId, r.getState1(),
                                r.getContent(), r.getSuccess(), r.getTime()))
                .collect(Collectors.toList()));
    }

    @Override
    public void add(RuleLog log) {
        //使用ruleId作表名
        String sql = String.format("INSERT INTO %s (%s) USING %s TAGS ('%s') VALUES (%s);",
                "rule_log_" + TableManager.rightTbName(log.getRuleId()),
                "time,state1,content,success",
                "rule_log",
                log.getRuleId(),
                "?,?,?,?"
        );
        tdTemplate.update(sql, System.currentTimeMillis(), log.getState(), log.getContent(), log.getSuccess());
    }
}
