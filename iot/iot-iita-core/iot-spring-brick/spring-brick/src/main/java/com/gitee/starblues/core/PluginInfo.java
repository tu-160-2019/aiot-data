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

package com.gitee.starblues.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.gitee.starblues.core.descriptor.PluginDescriptor;

import java.util.Date;
import java.util.Map;

/**
 * 插件信息
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.1.1
 */
public interface PluginInfo {

    /**
     * 得到插件id
     * @return String
     */
    String getPluginId();

    /**
     * 得到插件描述
     * @return PluginDescriptor
     */
    PluginDescriptor getPluginDescriptor();

    /**
     * 得到插件路径
     * @return Path
     */
    String getPluginPath();

    /**
     * 得到插件状态
     * @return PluginState
     */
    PluginState getPluginState();

    /**
     * 启动时间. 只有启动状态 {@link PluginState#STARTED} 才有值。
     * @return Date
     */
    Date getStartTime();

    /**
     * 停止时间. 只有停止状态 {@link PluginState#STOPPED} 才有值。
     * @return Date
     */
    Date getStopTime();

    /**
     * 是否跟随系统启动而加载的插件
     * @return true: 是, false: 否
     */
    boolean isFollowSystem();

    /**
     * 获取插件自主扩展信息
     * @return 扩展信息Map
     */
    Map<String, Object> getExtensionInfo();

    /**
     * 获取插件的Classloader
     * @return ClassLoader
     */
    @JsonIgnore
    ClassLoader getClassLoader();

}
