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

package com.gitee.starblues.core.classloader;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.loader.classloader.GeneralUrlClassLoader;
import com.gitee.starblues.loader.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 插件基本 url classLoader
 *
 * @author starBlues
 * @version 3.1.0
 * @since 3.0.4
 */
@Slf4j
public class PluginGeneralUrlClassLoader extends GeneralUrlClassLoader implements PluginResourceLoaderFactory{

    private final PluginResourceLoaderFactory proxy;

    public PluginGeneralUrlClassLoader(String name, GeneralUrlClassLoader parent) {
        super(name, parent);
        this.proxy = new PluginResourceLoaderFactoryProxy(this, parent);
    }

    @Override
    public void addResource(InsidePluginDescriptor descriptor) throws Exception {
        proxy.addResource(descriptor);
    }

    @Override
    public void close() throws IOException {
        super.close();
    }
}
