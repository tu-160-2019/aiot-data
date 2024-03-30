/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.ruleengine.listener;

import cc.iotkit.common.thing.ThingModelMessage;

import java.util.List;

public interface Listener<T> {

    String getType();

    List<T> getConditions();

    boolean execute(ThingModelMessage msg);
}
