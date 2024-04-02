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

import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.message.DevicePropertyCache;
import cc.iotkit.ruleengine.expression.Expression;
import lombok.Data;

import java.util.Map;

@Data
public class DeviceCondition {

    private String device;

    private String type;

    private String identifier;

    private Object value;

    private String comparator;

    private IDeviceInfoData deviceInfoData;

    @Override
    public DeviceCondition clone() {
        DeviceCondition con = new DeviceCondition();
        con.setDevice(device);
        con.setType(type);
        con.setIdentifier(identifier);
        con.setValue(value);
        con.setComparator(comparator);
        con.setDeviceInfoData(deviceInfoData);
        return con;
    }

    public boolean matches() {
        DeviceInfo deviceInfo;
        String[] pkDn = device.split("/");
        if (pkDn.length < 2) {
            //用deviceId取
            deviceInfo = deviceInfoData.findByDeviceId(device);
        } else {
            //用pk/dn取
            deviceInfo = deviceInfoData.findByDeviceName(pkDn[1]);
        }
        Object left = null;
        if ("property".equals(type)) {
            Map<String, ?> properties = deviceInfo.getProperty();
            DevicePropertyCache propertyCache = (DevicePropertyCache) properties.get(identifier);
            if (propertyCache == null) {
                return false;
            }
            left = propertyCache.getValue();
        } else if ("state".equals(type)) {
            DeviceInfo.State state = deviceInfo.getState();
            left = state != null && state.isOnline();
        } else if ("tag".equals(type)) {
            //取设备标签判断
            Map<String, DeviceInfo.Tag> tags = deviceInfo.getTag();
            if (tags != null) {
                DeviceInfo.Tag tag = tags.get(identifier);
                if (tag != null) {
                    //设备标签值
                    left = tag.getValue();
                }
            }
        }
        return Expression.eval(comparator, left, value);
    }
}
