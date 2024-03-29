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

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.utils.Assert;
import lombok.Setter;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 默认的内部PluginWrapperInside实现
 * @author starBlues
 *
 * @since 3.0.0
 * @version 3.1.1
 */
public class DefaultPluginInsideInfo implements PluginInsideInfo {

    private final String pluginId;
    private final InsidePluginDescriptor pluginDescriptor;
    private PluginState pluginState;
    private boolean isFollowInitial = false;

    private Date startTime;
    private Date stopTime;

    private Supplier<Map<String, Object>> extensionInfoSupplier = Collections::emptyMap;

    private ClassLoader classLoader = null;

    public DefaultPluginInsideInfo(InsidePluginDescriptor pluginDescriptor) {
        this.pluginId = pluginDescriptor.getPluginId();
        this.pluginDescriptor = pluginDescriptor;
    }

    @Override
    public void setPluginState(PluginState pluginState) {
        this.pluginState = Assert.isNotNull(pluginState, "pluginState 不能为空");
        resolveTime(pluginState);
    }

    @Override
    public void setFollowSystem() {
        isFollowInitial = true;
    }

    @Override
    public void setExtensionInfoSupplier(Supplier<Map<String, Object>> supplier) {
        this.extensionInfoSupplier = supplier;
    }

    @Override
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Supplier<Map<String, Object>> getExtensionInfoSupplier() {
        return extensionInfoSupplier;
    }

    @Override
    public String getPluginId() {
        return pluginId;
    }

    @Override
    public InsidePluginDescriptor getPluginDescriptor() {
        return pluginDescriptor;
    }

    @Override
    public PluginInfo toPluginInfo() {
        return new PluginInfoFace(this);
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
        return isFollowInitial;
    }

    @Override
    public Map<String, Object> getExtensionInfo() {
        return extensionInfoSupplier.get();
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    private void resolveTime(PluginState pluginState){
        if(pluginState == PluginState.STARTED || pluginState == PluginState.STARTED_FAILURE){
            startTime = new Date();
            stopTime = null;
        } else if(pluginState == PluginState.STOPPED || pluginState == PluginState.STOPPED_FAILURE){
            stopTime = new Date();
            startTime = null;
        } else {
            startTime = null;
            stopTime = null;
        }
    }

}
