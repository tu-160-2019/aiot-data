/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.ruleengine.action.alert;

import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.ruleengine.action.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sjg
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AlertAction implements Action<AlertService> {

    public static final String TYPE = "alert";

    private String type;

    private List<AlertService> services;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<String> execute(ThingModelMessage msg) {
        List<String> results = new ArrayList<>();
        for (AlertService service : services) {
            results.add(service.execute(msg));
        }
        return results;
    }

}
