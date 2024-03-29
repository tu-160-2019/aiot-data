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

package com.gitee.starblues.core.descriptor;

import com.gitee.starblues.core.checker.PluginBasicChecker;
import com.gitee.starblues.core.descriptor.decrypt.EmptyPluginDescriptorDecrypt;
import com.gitee.starblues.core.descriptor.decrypt.PluginDescriptorDecrypt;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.utils.SpringBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 组合插件描述加载者
 * @author starBlues
 * @since 3.0.0
 * @version 3.0.1
 */
@Slf4j
public class ComposeDescriptorLoader implements PluginDescriptorLoader{
    
    private final List<PluginDescriptorLoader> pluginDescriptorLoaders = new ArrayList<>();

    private final ApplicationContext applicationContext;
    private final PluginBasicChecker pluginChecker;


    public ComposeDescriptorLoader(ApplicationContext applicationContext, PluginBasicChecker pluginChecker) {
        this.applicationContext = applicationContext;
        this.pluginChecker = pluginChecker;
        addDefaultLoader();
    }

    protected void addDefaultLoader(){
        PluginDescriptorDecrypt pluginDescriptorDecrypt = getPluginDescriptorDecrypt(applicationContext);
        addLoader(new DevPluginDescriptorLoader(pluginDescriptorDecrypt));
        addLoader(new ProdPluginDescriptorLoader(pluginDescriptorDecrypt));
    }

    protected PluginDescriptorDecrypt getPluginDescriptorDecrypt(ApplicationContext applicationContext){
        PluginDescriptorDecrypt pluginDescriptorDecrypt =
                SpringBeanUtils.getExistBean(applicationContext, PluginDescriptorDecrypt.class);
        if(pluginDescriptorDecrypt != null){
            return pluginDescriptorDecrypt;
        } else {
            return new EmptyPluginDescriptorDecrypt();
        }
    }

    public void addLoader(PluginDescriptorLoader descriptorLoader){
        if(descriptorLoader != null){
            pluginDescriptorLoaders.add(descriptorLoader);
        }
    }
    
    
    @Override
    public InsidePluginDescriptor load(Path location) throws PluginException {
        for (PluginDescriptorLoader pluginDescriptorLoader : pluginDescriptorLoaders) {
            try {
                InsidePluginDescriptor pluginDescriptor = pluginDescriptorLoader.load(location);
                if(pluginDescriptor != null){
                    pluginChecker.checkDescriptor(pluginDescriptor);
                    return pluginDescriptor;
                }
            } catch (Exception e){
                log.debug("非法路径插件: {}", location);
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
