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

package com.gitee.starblues.loader.classloader.resource.loader;

import com.gitee.starblues.loader.classloader.resource.storage.ResourceStorage;

import java.net.URL;

/**
 * 资源加载者
 *
 * @author starBlues
 * @version 3.0.0
 */
public interface ResourceLoader extends AutoCloseable{

    /**
     * 获取资源基本 URL
     * @return URL
     */
    URL getBaseUrl();


    /**
     * 装载资源到ResourceStorage
     * @param resourceStorage 资源存储
     * @throws Exception 装载异常
     */
    void load(ResourceStorage resourceStorage) throws Exception;

}
