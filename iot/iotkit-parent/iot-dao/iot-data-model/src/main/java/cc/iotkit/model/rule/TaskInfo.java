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

import cc.iotkit.model.Owned;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskInfo implements Owned<String> {

    public static String TYPE_TIMER = "timer";
    public static String TYPE_DELAY = "delay";

    public static String STATE_STOP = "stopped";
    public static String STATE_RUNNING = "running";
    public static String STATE_INVALID = "invalid";
    public static String STATE_FINISHED = "finished";

    private String id;

    private String name;

    /**
     * 任务类型
     */
    private String type;

    /**
     * 表达式
     * 定时器使用cron表达式
     * 延时器使用延时时长（秒）
     */
    private String expression;

    /**
     * 描述
     */
    private String desc;

    /**
     * 任务输出
     */
    private List<RuleAction> actions;

    /**
     * 任务状态
     */
    private String state;

    /**
     * 创建者
     */
    private String uid;

    private Long createAt;

    /**
     * 操作备注
     */
    private String reason;

    public Long delayTime() {
        if (!TYPE_DELAY.equals(type)) {
            return null;
        }
        if (expression == null) {
            return null;
        }
        return Long.parseLong(expression);
    }
}
