/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.model.device;

import cc.iotkit.model.Owned;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 虚拟设备
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VirtualDevice implements Owned<String> {

    public static final String STATE_STOPPED = "stopped";
    public static final String STATE_RUNNING = "running";

    /**
     * 虚拟类型-基于物模型模拟
     */
    public static final String TYPE_THING_MODEL = "thingModel";
    /**
     * 虚拟类型-基于设备协议模拟
     */
    public static final String TYPE_PROTOCOL = "protocol";

    /**
     * 触发执行-无（手动）
     */
    public static final String TRIGGER_NONE = "none";
    /**
     * 触发执行-定时执行
     */
    public static final String TRIGGER_CRON = "cron";
    /**
     * 触发执行-随机执行
     */
    public static final String TRIGGER_RANDOM = "random";

    private String id;

    /**
     * 所属用户
     */
    private String uid;

    /**
     * 虚拟设备名称
     */
    private String name;

    /**
     * 产品key
     */
    private String productKey;

    /**
     * 虚拟的目标设备列表
     */
    private List<String> devices = new ArrayList<>();

    /**
     * 虚拟类型
     */
    private String type;

    /**
     * 设备行为脚本
     */
    private String script;

    /**
     * 触发方式执行方式
     */
    private String trigger;

    /**
     * 触发表达式
     */
    private String triggerExpression;

    /**
     * 运行状态
     */
    private String state;

    /**
     * 创建时间
     */
    private Long createAt;

}
