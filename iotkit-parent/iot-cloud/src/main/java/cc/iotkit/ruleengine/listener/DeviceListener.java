/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.ruleengine.listener;

import cc.iotkit.common.thing.ThingModelMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class DeviceListener implements Listener<DeviceCondition> {

    public static final String TYPE = "device";

    private String type;

    private String pk;

    private String dn;

    private List<DeviceCondition> conditions;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public boolean execute(ThingModelMessage message) {
        String identifier = message.getIdentifier();
        Map<String, Object> mapData = message.dataToMap();
        String pk = message.getProductKey();
        String dn = message.getDeviceName();
        for (DeviceCondition condition : this.conditions) {
            String condPkDn = condition.getDevice();
            String[] pkAndDn = condPkDn.split("/");
            String condPk = pkAndDn[0];
            String condDn = pkAndDn[1];
            //判断产品是否匹配
            if (!pk.equals(condPk)) {
                continue;
            }
            //判断设备是否匹配
            if (!"#".equals(condDn) && !dn.equals(condDn)) {
                continue;
            }

            if (condition.matches(message.getType(), identifier, mapData)) {
                return true;
            }
        }

        return false;
    }

}
