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

package com.gitee.starblues.core.launcher.plugin.involved;

import com.gitee.starblues.core.PluginInsideInfo;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.PluginExtensionInfo;
import com.gitee.starblues.core.descriptor.PluginLibInfo;
import com.gitee.starblues.loader.PluginResourceStorage;
import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.SpringPluginHook;
import com.gitee.starblues.spring.web.PluginStaticResourceResolver;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.SpringBeanCustomUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 默认的插件启动介入者
 * @author starBlues
 * @since 3.0.0
 * @version 3.1.2
 */
@Slf4j
public class DefaultPluginLaunchInvolved implements PluginLaunchInvolved{

    @Override
    public void before(PluginInsideInfo pluginInsideInfo, ClassLoader classLoader) throws Exception {
        InsidePluginDescriptor descriptor = pluginInsideInfo.getPluginDescriptor();
        Set<PluginLibInfo> pluginLibInfo = descriptor.getPluginLibInfo();
        List<String> libPath = null;
        if(pluginLibInfo != null){
            libPath = pluginLibInfo.stream().map(PluginLibInfo::getPath).collect(Collectors.toList());
        }
        PluginResourceStorage.addPlugin(descriptor.getPluginId(), descriptor.getPluginFileName(), libPath);
    }

    @Override
    public void after(PluginInsideInfo pluginInsideInfo, ClassLoader classLoader, SpringPluginHook pluginHook) throws Exception {
        InsidePluginDescriptor descriptor = pluginInsideInfo.getPluginDescriptor();
        PluginStaticResourceResolver.parse(descriptor, classLoader, pluginHook.getWebConfig());
        setExtensionInfoSupplier(pluginInsideInfo, pluginHook);
    }

    @Override
    public void close(PluginInsideInfo pluginInsideInfo, ClassLoader classLoader) throws Exception {
        InsidePluginDescriptor descriptor = pluginInsideInfo.getPluginDescriptor();
        String pluginId = descriptor.getPluginId();
        PluginStaticResourceResolver.remove(pluginId);
    }

    private void setExtensionInfoSupplier(PluginInsideInfo pluginInsideInfo, SpringPluginHook pluginHook){
        pluginInsideInfo.setExtensionInfoSupplier(()->{
            // 设置插件自主扩展信息
            ApplicationContext applicationContext = pluginHook.getApplicationContext();
            List<PluginExtensionInfo> beans = SpringBeanCustomUtils.getBeans(applicationContext,
                    PluginExtensionInfo.class);
            if(ObjectUtils.isEmpty(beans)){
                return new HashMap<>(0);
            }
            Map<String, Object> extensionInfos = new HashMap<>();
            for (PluginExtensionInfo extensionInfoBean : beans) {
                try {
                    Map<String, Object> extensionInfo = extensionInfoBean.extensionInfo();
                    if(!ObjectUtils.isEmpty(extensionInfo)){
                        extensionInfos.putAll(extensionInfo);
                    }
                } catch (Exception e){
                    log.error(e.getMessage(), e);
                }
            }
            return extensionInfos;
        });
    }

}
