/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.ruleengine.filter;

import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.data.manager.IDeviceInfoData;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class DeviceFilter implements Filter<DeviceCondition> {

    public static String TYPE = "device";

    private String type;

    private String pk;

    private String dn;

    private List<DeviceCondition> conditions;

    private IDeviceInfoData deviceInfoData;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void init() {
    }

    @Override
    public boolean execute(ThingModelMessage msg) {
        for (DeviceCondition condition : getConditions()) {
            DeviceCondition con = condition.clone();
            //未指定device，使用消息中的deviceId
            if (StringUtils.isBlank(con.getDevice())) {
                con.setDevice(msg.getDeviceId());
            }

            con.setDeviceInfoData(deviceInfoData);
            if (!con.matches()) {
                return false;
            }
        }
        return true;
    }

}
