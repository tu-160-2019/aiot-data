/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.ruleengine.rule;

import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.ruleengine.handler.DeviceMessageHandler;
import cc.iotkit.ruleengine.listener.DeviceCondition;
import cc.iotkit.ruleengine.listener.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RuleMessageHandler implements DeviceMessageHandler {

    private final Map<String, List<Rule>> deviceRuleMap = new HashMap<>();

    @Autowired
    private RuleExecutor ruleExecutor;

    public synchronized void putRule(Rule rule) {
        //先删除已经存在的规则
        removeRule(rule.getId());

        //将同一个规则中不同device拆开存储
        for (Listener<?> listener : rule.getListeners()) {
            for (Object condition : listener.getConditions()) {
                if (!(condition instanceof DeviceCondition)) {
                    continue;
                }
                String device = ((DeviceCondition) condition).getDevice();
                deviceRuleMap.putIfAbsent(device, new ArrayList<>());
                List<Rule> rules = deviceRuleMap.get(device);
                rules.add(rule);
            }
        }
    }

    public synchronized void removeRule(String ruleId) {
        Iterator<Map.Entry<String, List<Rule>>> iterator = deviceRuleMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<Rule>> entry = iterator.next();
            String device = entry.getKey();
            //找出相同场景ID的规则删除
            entry.getValue().removeIf(s -> s != null && s.getId().equals(ruleId));
            //删除空的规则列表
            if (entry.getValue().isEmpty()) {
                iterator.remove();
                deviceRuleMap.remove(device);
            }
        }
    }

    @Override
    public void handle(ThingModelMessage message) {
        String pk = message.getProductKey();
        String dn = message.getDeviceName();
        List<Rule> rules = new ArrayList<>();
        //仅用PK匹配
        List<Rule> foundRules = deviceRuleMap.get(pk + "/#");
        if (foundRules != null) {
            rules.addAll(foundRules);
        }
        //用PK和DN匹配
        foundRules = deviceRuleMap.get(pk + "/" + dn);
        if (foundRules != null) {
            rules.addAll(foundRules);
        }
        //执行规则
        for (Rule rule : rules) {
            ruleExecutor.execute(message, rule);
        }
    }

}
