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

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.utils.Assert;

import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 外部 PluginWrapperFace
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.1.1
 */
public class PluginInfoFace implements PluginInfo {

    private final PluginDescriptor pluginDescriptor;
    private final PluginState pluginState;
    private final boolean followSystem;
    private final Supplier<Map<String, Object>> extensionInfoSupplier;
    private final ClassLoader classLoader;

    private final Date startTime;
    private final Date stopTime;

    public PluginInfoFace(PluginInsideInfo pluginInsideInfo) {
        Assert.isNotNull(pluginInsideInfo, "参数 pluginWrapperInside 不能为空");
        this.pluginDescriptor = pluginInsideInfo.getPluginDescriptor().toPluginDescriptor();
        this.pluginState = pluginInsideInfo.getPluginState();
        this.followSystem = pluginInsideInfo.isFollowSystem();
        this.extensionInfoSupplier = pluginInsideInfo.getExtensionInfoSupplier();
        this.startTime = pluginInsideInfo.getStartTime();
        this.stopTime = pluginInsideInfo.getStopTime();
        this.classLoader = pluginInsideInfo.getClassLoader();
    }

    @Override
    public String getPluginId() {
        return pluginDescriptor.getPluginId();
    }

    @Override
    public PluginDescriptor getPluginDescriptor() {
        return pluginDescriptor;
    }

    @Override
    public String getPluginPath() {
        return pluginDescriptor.getPluginPath();
    }

    @Override
    public PluginState getPluginState() {
        return pluginState;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    @Override
    public Date getStopTime() {
        return stopTime;
    }

    @Override
    public boolean isFollowSystem() {
        return followSystem;
    }

    @Override
    public Map<String, Object> getExtensionInfo() {
        return extensionInfoSupplier.get();
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
