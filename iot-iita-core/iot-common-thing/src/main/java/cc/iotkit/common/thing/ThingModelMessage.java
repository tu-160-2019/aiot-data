/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.common.thing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 物模型消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThingModelMessage {

    public static final String TYPE_LIFETIME = "lifetime";
    public static final String TYPE_STATE = "state";
    public static final String TYPE_PROPERTY = "property";

    public static final String TYPE_EVENT = "event";
    public static final String TYPE_SERVICE = "service";

    public static final String TYPE_OTA = "ota";
    public static final String TYPE_CONFIG = "config";
    public static final String ID_PROPERTY_GET = "get";
    public static final String ID_PROPERTY_SET = "set";
    public static final String ID_PROPERTY_REPORT = "report";
    public static final String ID_CONFIG_GET = "get";
    public static final String ID_CONFIG_SET = "set";
    public static final String ID_DEREGISTER = "deregister";

    private String id;

    private String mid;

    private String deviceId;

    private String productKey;

    private String deviceName;

    /**
     * 所属用户ID
     */
    private String uid;

    /**
     * 消息类型
     * lifetime:生命周期
     * state:状态
     * property:属性
     * event:事件
     * service:服务
     */
    private String type;

    private String identifier;

    /**
     * 消息状态码
     */
    private int code;

    private Object data;

    /**
     * 时间戳，设备上的事件或数据产生的本地时间
     */
    private Long occurred;

    /**
     * 消息上报时间
     */
    private Long time;

    public Map<String, Object> dataToMap() {
        Map<String, Object> mapData = new HashMap<>();
        if (data instanceof Map) {
            ((Map<?, ?>) data).forEach((key, value) -> mapData.put(key.toString(), value));
        }
        return mapData;
    }
}
