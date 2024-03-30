/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.ruleengine.alert;

import java.util.Map;

/**
 * 报警器
 */
public interface Alerter {

    void setConfig(String config);

    void setTemplate(String template);

    String send(Map<String, Object> data);

}
