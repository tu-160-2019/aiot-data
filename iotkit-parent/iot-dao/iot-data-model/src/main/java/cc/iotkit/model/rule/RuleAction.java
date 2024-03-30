/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.model.rule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 规则引擎输出动作
 *
 * @author sjg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleAction {
    /**
     * 设备控制
     */
    public static final String TYPE_DEVICE = "device";

    /**
     * 告警消息
     */
    public static final String TYPE_ALARM = "alarm";

    /**
     * 场景控制
     */
    public static final String TYPE_SCENE = "scene";

    /**
     * http推送
     */
    public static final String TYPE_HTTP = "http";

    /**
     * mqtt推送
     */
    public static final String TYPE_MQTT = "mqtt";

    /**
     * kafka推送
     */
    public static final String TYPE_KAFKA = "kafka";

    /**
     * tcp推送
     */
    public static final String TYPE_TCP = "tcp";

    /**
     * 动作类型
     */
    protected String type;

    /**
     * 动作配置
     */
    protected String config;

}
