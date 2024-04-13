/*
 * Copyright 2023 http://gcpaas.gccloud.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gccloud.common.utils;

import com.gccloud.common.exception.GlobalException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import groovy.transform.TimedInterrupt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.syntax.Types;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;
import org.kohsuke.groovy.sandbox.SandboxTransformer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 函数执行
 *
 * @author liuchengbiao
 * @date 2019-07-14 21:15
 */
@Slf4j
public class GroovyUtils {
    /**
     * 缓存编译好的类
     * key: 脚本
     * value: 编译好的class
     */
    static final Cache<String, Class> CACHE_CLASS = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();

    /**
     * 执行函数
     *
     * @param groovyScript 传入的脚本
     * @param params       传入的参数
     */
    public static Object run(String groovyScript, Map<String, Object> params) {
        Class clazz = buildClass(groovyScript);
        if (clazz == null) {
            return null;
        }
        Binding binding = new Binding();
        // 设置变量
        Map variables = binding.getVariables();
        if (params != null) {
            variables.putAll(params);
        }
        try {
            Script script = InvokerHelper.createScript(clazz, binding);
            Object result = script.run();
            return result;
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new GlobalException("脚本执行失败", e);
        }
    }

    /**
     * 脚本编译
     *
     * @param groovyScript
     * @return
     */
    public static Class buildClass(String groovyScript) {
        if (StringUtils.isBlank(groovyScript)) {
            return null;
        }
        Class clazz = CACHE_CLASS.get(groovyScript, (script) -> {
            try {
                return buildClassSafe(script);
            } catch (Exception e) {
                log.error("脚本 {} 编译失败:{}", script, e);
            }
            return null;
        });
        return clazz;
    }

    public static Class<?> buildClassSafe(String groovyScript) {
        // 自定义配置
        CompilerConfiguration config = new CompilerConfiguration();
        // 添加线程中断拦截器，可拦截循环体（while）、方法和闭包的首指令
        SecureASTCustomizer secure = new SecureASTCustomizer();
        // 禁止使用闭包
        secure.setClosuresAllowed(true);
        List<Integer> tokensBlacklist = new ArrayList<>();
        // 添加关键字黑名单 while和goto
        tokensBlacklist.add(Types.KEYWORD_WHILE);
        tokensBlacklist.add(Types.KEYWORD_GOTO);
        secure.setTokensBlacklist(tokensBlacklist);
        config.addCompilationCustomizers(secure);
        // statement 黑名单，不能使用while循环块
        List<Class<? extends Statement>> statementBlacklist = new ArrayList<>();
        statementBlacklist.add(WhileStatement.class);
        secure.setStatementsBlacklist(statementBlacklist);
        // 添加线程中断拦截器，可中断超时线程，当前定义超时时间为3s
        Map<String, Object> timeoutArgs = new HashMap<>();
        timeoutArgs.put("value", 3);
        config.addCompilationCustomizers(new ASTTransformationCustomizer(timeoutArgs, TimedInterrupt.class));
        // 沙盒环境
        config.addCompilationCustomizers(new SandboxTransformer());
        // 注册至当前线程
        new NoSystemExitSandbox().register();
        new NoRunTimeSandbox().register();
        new NoFileSandbox().register();

        ClassLoader parent = GroovyUtils.class.getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent, config);

        Class groovyClass = loader.parseClass(groovyScript);

        try {
            loader.close();
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return groovyClass;
    }


    static class NoSystemExitSandbox extends GroovyInterceptor {
        @Override
        public Object onStaticCall(Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
            if (receiver == System.class && method.equals("exit")) {
                throw new SecurityException("No call on System.exit() please");
            }
            return super.onStaticCall(invoker, receiver, method, args);
        }
    }

    static class NoRunTimeSandbox extends GroovyInterceptor {
        @Override
        public Object onStaticCall(Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
            if (receiver == Runtime.class) {
                throw new SecurityException("No call on RunTime please");
            }
            return super.onStaticCall(invoker, receiver, method, args);
        }
    }

    static class NoFileSandbox extends GroovyInterceptor {
        @Override
        public Object onStaticCall(Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
            if (receiver == File.class) {
                throw new SecurityException("No call on File please");
            }
            return super.onStaticCall(invoker, receiver, method, args);
        }
    }

    static class NoProcessSandbox extends GroovyInterceptor {
        @Override
        public Object onStaticCall(Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
            if (receiver == Process.class) {
                throw new SecurityException("No call on Process please");
            }
            return super.onStaticCall(invoker, receiver, method, args);
        }
    }


}