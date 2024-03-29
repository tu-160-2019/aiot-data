/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.ts.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.model.rule.RuleLog;
import cc.iotkit.temporal.IRuleLogData;
import cc.iotkit.temporal.ts.dao.TsTemplate;
import cc.iotkit.temporal.ts.dm.TableManager;
import cc.iotkit.temporal.ts.model.TsRuleLog;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

//import cc.iotkit.temporal.ts.dm.TableManager;

@Service
public class RuleLogDataImpl implements IRuleLogData {


    @Autowired
    private TsTemplate tsTemplate;

    @Override
    public void deleteByRuleId(String ruleId) {

        tsTemplate.update("delete from rule_log where rule_id=? and time<=NOW()", ruleId);
    }

    @Override
    public Paging<RuleLog> findByRuleId(String ruleId, int page, int size) {

        SelectForUpdateStep<Record5<Object, Object, Object, Object, Object>> sqlStep = TableManager.getSqlBuilder()
                .select(field("time"), field("state1"), field("content"), field("success"),
                        field("rule_id"))
                .from(table("rule_log"))
                .where(field("rule_id").eq(ruleId))
                .orderBy(field("time").desc())
                .limit(size)
                .offset((page - 1) * size);
        List<TsRuleLog> ruleLogs = tsTemplate.query(sqlStep.getSQL(ParamType.INLINED), new BeanPropertyRowMapper<>(TsRuleLog.class), ruleId);

        SelectConditionStep<Record1<Integer>> where = TableManager.getSqlBuilder().selectCount().from(table("rule_log"))
                .where(field("rule_id").eq(ruleId));
        Long count = tsTemplate.queryForObject(where.getSQL(ParamType.INLINED), Long.class);

        return new Paging<>(count, ruleLogs.stream().map(r ->
                new RuleLog(r.getTime().toString(), ruleId, r.getState1(),
                        r.getContent(), r.getSuccess(), r.getTime().getTime()))
                .collect(Collectors.toList()));
    }

    @Override
    public void add(RuleLog log) {
        //使用

        InsertValuesStep5<Record, Object, Object, Object, Object, Object> sqlStep = TableManager.getSqlBuilder().insertInto(table("rule_log"),
                field("time"),
                field("rule_id"),
                field("state1"),
                field("content"), field("success")).values(new Date(),
                log.getRuleId(), log.getState(), log.getContent(), log.getSuccess());

        tsTemplate.update(sqlStep.getSQL(ParamType.INLINED));
    }
}
