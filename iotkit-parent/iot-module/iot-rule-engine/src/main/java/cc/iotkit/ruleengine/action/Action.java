/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.ruleengine.action;


import cc.iotkit.common.thing.ThingModelMessage;

import java.util.List;

public interface Action<T> {

    String getType();

    List<T> getServices();

    /**
     * 执行动作返回执行动作内容
     */
    List<String> execute(ThingModelMessage msg);
}
