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

import java.util.List;

/**
 * 场景过滤器
 */
public interface Filter<T> {

    String getType();

    List<T> getConditions();

    void init();

    boolean execute(ThingModelMessage msg);
}
