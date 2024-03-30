/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.ruleengine.expression;

import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.script.IScriptEngine;
import cc.iotkit.script.ScriptEngineFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sjg
 */
@Slf4j
public class Expression {

    private static final Map<String, String> COMPARATOR_MAP = new HashMap<>();

    private static IScriptEngine engine = ScriptEngineFactory.getScriptEngine("js");

    static {
        COMPARATOR_MAP.put(">", "gt");
        COMPARATOR_MAP.put(">=", "ge");
        COMPARATOR_MAP.put("==", "eq");
        COMPARATOR_MAP.put("<", "lt");
        COMPARATOR_MAP.put("<=", "le");
        COMPARATOR_MAP.put("!=", "neq");
        try {
            String script = IOUtils.resourceToString("script.js", Charset.defaultCharset(), Expression.class.getClassLoader());
            engine.setScript(script);
        } catch (IOException e) {
            log.error("read script.js error", e);
        }
    }

    public static boolean eval(String comparator, Object... args) {
        String name = COMPARATOR_MAP.get(comparator);
        if (name == null) {
            throw new BizException(ErrCode.DATA_BLANK);
        }

        return engine.invokeMethod(new TypeReference<>() {
        }, name, args);
    }

}
