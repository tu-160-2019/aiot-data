/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.script;


import cc.iotkit.common.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

/**
 * @author sjg
 */
@Slf4j
public class JavaScriptEngine implements IScriptEngine {

    private final Context context = Context.newBuilder("js").allowHostAccess(HostAccess.ALL).build();

    private Value jsScript;

    @Override
    public void setScript(String script) {
        jsScript = context.eval("js", String.format(
                "new (function () {\n%s; " +
                        "   this.invoke=function(f,args){" +
                        "       for(i in args){" +
                        "           args[i]=JSON.parse(args[i]);" +
                        "       }" +
                        "       return JSON.stringify(this[f].apply(this,args));" +
                        "   }; " +
                        "})()", script));
    }

    @Override
    public void putScriptEnv(String key, Object value) {
        context.getBindings("js").putMember(key, value);
    }

    @Override
    public void invokeMethod(String methodName, Object... args) {
        invokeMethod(new TypeReference<Void>() {
        }, methodName, args);
    }

    @Override
    public <T> T invokeMethod(TypeReference<T> type, String methodName, Object... args) {
        Value member = jsScript.getMember("invoke");
        StringBuilder sbArgs = formatArgs(args);
        //通过调用invoke方法将目标方法返回结果转成json
        Value rst = member.execute(methodName, args);
        String json = rst.asString();
        log.info("invoke script={}, args={}, result={}", methodName, sbArgs, json);

        //没有返回值
        if (json == null || "null".equals(json)) {
            return null;
        }
        return JsonUtils.parseObject(json, type);
    }

    @Override
    public String invokeMethod(String methodName, String args) {
        Value member = jsScript.getMember("invoke");
        //通过调用invoke方法将目标方法返回结果转成json
        Value rst = member.execute(methodName, JsonUtils.parseArray(args, Object.class));
        String json = rst.asString();
        log.info("invoke script={}, args={}, result={}", methodName, args, json);
        //没有返回值
        if (json == null || "null".equals(json)) {
            return null;
        }
        return json;
    }

    private static StringBuilder formatArgs(Object[] args) {
        StringBuilder sbArgs = new StringBuilder("[");
        //将入参转成json
        for (int i = 0; i < args.length; i++) {
            args[i] = JsonUtils.toJsonString(args[i]);
            sbArgs.append(args[i]).append(i != args.length - 1 ? "," : "");
        }
        sbArgs.append("]");
        return sbArgs;
    }

}