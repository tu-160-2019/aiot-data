/**
 * Copyright [2019-Present] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.spring;

import com.gitee.starblues.spring.environment.EnvironmentProvider;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

/**
 * 主程序 ApplicationContext 接口
 * @author starBlues
 * @version 3.0.1
 */
public interface MainApplicationContext extends ApplicationContext {

    /**
     * 得到主程序所有配置的 env
     *
     * @return 主程序配置的 env 集合
     */
    Map<String, Map<String, Object>> getConfigurableEnvironment();

    /**
     * 得到主程序配置的 Provider
     * @return EnvironmentProvider
     */
    EnvironmentProvider getEnvironmentProvider();

    /**
     * 返回主程序配置的Profile配置
     * @return String 数组
     */
    String[] getActiveProfiles();

    /**
     * 返回主程序默认的Profile配置
     * @return String 数组
     */
    String[] getDefaultProfiles();

    /**
     * 从主程序获取依赖
     *
     * @param requestingBeanName 依赖Bean名称
     * @param dependencyType 依赖类型
     * @return boolean
     */
    Object resolveDependency(String requestingBeanName, Class<?> dependencyType);

    /**
     * 是否为web环境
     * @return boolean
     */
    boolean isWebEnvironment();

    /**
     * 得到原始的 ApplicationContext
     * @return Object
     */
    Object getSourceApplicationContext();

    /**
     * 是否能注册Controller
     * @return boolean
     */
    boolean isRegisterController();

    /**
     * 获取主程序的 RequestMappingHandlerMapping
     * @return RequestMappingHandlerMapping
     */
    RequestMappingHandlerMapping getRequestMappingHandlerMapping();

    /**
     * 获取主程序的 RequestMappingHandlerAdapter
     * @return RequestMappingHandlerAdapter
     */
    RequestMappingHandlerAdapter getRequestMappingHandlerAdapter();


}
