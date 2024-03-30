/**
 * Copyright [2019-Present] [starBlues]
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

package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.SpringBeanFactory;
import com.gitee.starblues.spring.environment.EnvironmentProvider;

/**
 * 提供插件上下文的工具类
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.0.3
 */
public abstract class PluginContextHolder {

    private static volatile Boolean INITIALIZED = false;

    private static ProcessorContext processorContext;

    private static ClassLoader pluginClassLoader;
    private static InsidePluginDescriptor pluginDescriptor;

    private static IntegrationConfiguration configuration;
    private static Boolean mainIsWebEnv;
    private static SpringBeanFactory mainSpringBeanFactory;


    private PluginContextHolder(){}

    static void initialize(ProcessorContext processorContext){
        if(INITIALIZED){
            return;
        }
        PluginContextHolder.processorContext = processorContext;

        PluginContextHolder.pluginClassLoader = processorContext.getClassLoader();
        PluginContextHolder.pluginDescriptor = processorContext.getPluginDescriptor();
        PluginContextHolder.configuration = processorContext.getConfiguration();
        PluginContextHolder.mainIsWebEnv = processorContext.getMainApplicationContext().isWebEnvironment();
        PluginContextHolder.mainSpringBeanFactory = processorContext.getMainBeanFactory();
        INITIALIZED = true;
    }

    /**
     * 获取主程序环境中配置文件内容提供者
     * @return EnvironmentProvider
     */
    public static EnvironmentProvider getEnvironmentProvider(){
        check();
        return processorContext.getMainApplicationContext().getEnvironmentProvider();
    }

    /**
     * 获取主程序针对本框架的配置内容
     * @return IntegrationConfiguration
     */
    public static IntegrationConfiguration getConfiguration() {
        check();
        return configuration;
    }

    /**
     * 获取主程序的 SpringBeanFactory . 通过它可获取主程序中的Bean
     * @return SpringBeanFactory
     */
    public static SpringBeanFactory getMainSpringBeanFactory() {
        check();
        return mainSpringBeanFactory;
    }

    /**
     * 判断主程序是否为web环境
     * @return Boolean
     */
    public static Boolean getMainIsWebEnv() {
        check();
        return mainIsWebEnv;
    }

    /**
     * 获取插件的 classloader
     * @return ClassLoader
     */
    public static ClassLoader getPluginClassLoader() {
        check();
        return pluginClassLoader;
    }

    /**
     * 获取插件信息
     * @return InsidePluginDescriptor
     */
    public static InsidePluginDescriptor getPluginDescriptor() {
        check();
        return pluginDescriptor;
    }

    private static void check(){
        if(!INITIALIZED){
            throw new IllegalStateException("PluginContextHolder 未初始化");
        }
    }

}
