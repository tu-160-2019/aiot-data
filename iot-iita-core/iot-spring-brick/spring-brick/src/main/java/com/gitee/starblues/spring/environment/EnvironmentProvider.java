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

package com.gitee.starblues.spring.environment;

import java.util.function.BiConsumer;

/**
 * 配置信息提供者接口
 *
 * @author starBlues
 * @version 3.0.3
 */
public interface EnvironmentProvider {

    /**
     * 根据名称获取配置值
     * @param name 配置名称
     * @return 配置值
     */
    Object getValue(String name);

    /**
     * 根据名称获取 String 类型配置值
     * @param name 配置名称
     * @return 配置值
     */
    String getString(String name);

    /**
     * 根据名称获取 Integer 类型配置值
     * @param name 配置名称
     * @return 配置值
     */
    Integer getInteger(String name);

    /**
     * 根据名称获取 Long 类型配置值
     * @param name 配置名称
     * @return 配置值
     */
    Long getLong(String name);

    /**
     * 根据名称获取 Double 类型配置值
     * @param name 配置名称
     * @return 配置值
     */
    Double getDouble(String name);

    /**
     * 根据名称获取 Float 类型配置值
     * @param name 配置名称
     * @return 配置值
     */
    Float getFloat(String name);

    /**
     * 根据名称获取 Boolean 类型配置值
     * @param name 配置名称
     * @return 配置值
     */
    Boolean getBoolean(String name);

    /**
     * 根据前缀名称批量获取配置值
     * @param prefix 前缀
     * @return 环境
     */
    EnvironmentProvider getByPrefix(String prefix);

    /**
     * 获取所有配置集合
     * @param action 每个条目执行的操作
     */
    void forEach(BiConsumer<String, Object> action);


}
