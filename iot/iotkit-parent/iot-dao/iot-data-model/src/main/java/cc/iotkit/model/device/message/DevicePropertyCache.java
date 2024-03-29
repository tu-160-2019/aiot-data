/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.model.device.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevicePropertyCache {


    /**
     * 属性值
     */
    private Object value;

    /**
     * 属性值时间: 设备上报时间
     */
    private Long occurred;


    public Map<String, Object> toMap() {
        return Map.of("value", value, "occurred", occurred);
    }

}
