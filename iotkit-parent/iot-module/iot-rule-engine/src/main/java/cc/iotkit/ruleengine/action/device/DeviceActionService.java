/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.ruleengine.action.device;

import cc.iotkit.common.thing.DeviceService;
import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.common.thing.ThingService;
import cc.iotkit.common.utils.UniqueIdUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DeviceActionService {

    @Autowired
    private DeviceService deviceService;

    public String invoke(Service service) {
        String[] pkDn = service.getDevice().split("/");
        ThingService<Map<String, Object>> thingService = new ThingService<>();
        thingService.setMid(UniqueIdUtil.newRequestId());
        thingService.setProductKey(pkDn[0]);
        thingService.setDeviceName(pkDn[1]);
        thingService.setType(service.getType());
        thingService.setIdentifier(service.getIdentifier());
        thingService.setParams(service.parseInputData());
        deviceService.invoke(thingService);
        return thingService.getMid();
    }

    @Data
    public static class Service {

        private String device;

        private String identifier;

        private String type;

        public String getType() {
            //identifier为set固定为属性设置，其它为服务调用
            if (ThingModelMessage.ID_PROPERTY_SET.equals(identifier) ||
                    ThingModelMessage.ID_PROPERTY_GET.equals(identifier)) {
                return ThingModelMessage.TYPE_PROPERTY;
            }
            return ThingModelMessage.TYPE_SERVICE;
        }

        private List<Parameter> inputData;

        public Map<String, Object> parseInputData() {
            Map<String, Object> data = new HashMap<>();
            for (Parameter p : inputData) {
                data.put(p.getIdentifier(), p.getValue());
            }
            return data;
        }

        @Data
        public static class Parameter {
            private String identifier;
            private Object value;
        }
    }

}
