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
 * 钉钉机器人告警器
 */
public class DingdRobotAlerter implements Alerter {
    @Override
    public void setConfig(String config) {

    }

    @Override
    public void setTemplate(String template) {

    }

    @Override
    public String send(Map<String, Object> data) {
        return "send dingding robot msg";
    }
}
