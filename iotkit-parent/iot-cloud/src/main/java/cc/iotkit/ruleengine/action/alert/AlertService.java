/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.ruleengine.action.alert;

import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.message.model.Message;
import cc.iotkit.message.service.MessageService;
import cc.iotkit.ruleengine.action.ScriptService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author sjg
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class AlertService extends ScriptService {

    private Message message;

    private MessageService messageService;

    @SneakyThrows
    public String execute(ThingModelMessage msg) {
        //执行转换脚本
        Map<String, Object> result = execScript(new TypeReference<>() {
        }, msg);
        if (result == null) {
            log.warn("execScript result is null");
            return "execScript result is null";
        }
        message.setParam(result);
        messageService.sendMessage(message);
        return "ok";
    }
}
