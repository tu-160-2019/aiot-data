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
import com.gitee.starblues.loader.classloader.resource.loader.ResourceLoaderFactory;

/**
 * 插件资源工程
 *
 * @author starBlues
 * @version 3.1.0
 * @since 3.0.4
 */
public interface PluginResourceLoaderFactory extends ResourceLoaderFactory {

    /**
     * 加载插件资源
     * @param descriptor 插件资源描述
     * @throws Exception 添加插件资源异常
     * @since 3.0.4
     */
    void addResource(InsidePluginDescriptor descriptor) throws Exception;


}
