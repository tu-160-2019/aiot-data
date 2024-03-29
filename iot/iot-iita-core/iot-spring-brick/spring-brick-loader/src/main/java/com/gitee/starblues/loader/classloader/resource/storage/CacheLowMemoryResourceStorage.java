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

package com.gitee.starblues.loader.classloader.resource.storage;

import com.gitee.starblues.loader.classloader.resource.Resource;
import com.gitee.starblues.loader.classloader.resource.cache.Cache;
import com.gitee.starblues.loader.classloader.resource.cache.DefaultCacheExpirationTrigger;
import com.gitee.starblues.loader.classloader.resource.cache.LRUMapCache;
import com.gitee.starblues.loader.utils.ObjectUtils;
import com.gitee.starblues.loader.utils.ResourceUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 直接可缓存的资源存储,
 * 优点: 释放前占用内存做了限制, 不会占用太高, 处于占用内存比较低, 且可根据LRU缓存机制进行缓存
 * 缺点: 如果缓存未命中, 速度比较慢
 *
 * @author starBlues
 * @since 3.1.1
 * @version 3.1.1
 * @deprecated 暂时遗弃
 */
@Deprecated
public class CacheLowMemoryResourceStorage extends AbstractResourceStorage {

    protected final Map<String, Cache<String, List<Resource>>> resourceStorage = new ConcurrentHashMap<>();

    public CacheLowMemoryResourceStorage(String key) {
    }

    @Override
    public void addBaseUrl(URL url) {
        super.addBaseUrl(url);
        String key = url.toString();
        if(!resourceStorage.containsKey(key)){
            Cache<String, List<Resource>> cache = DefaultCacheExpirationTrigger.getCacheExpirationTrigger(3, TimeUnit.MINUTES)
                    .getCache(key, () -> new LRUMapCache<String, List<Resource>>(10000, 180000));
            resourceStorage.put(key, cache);
        }
    }

    private Cache<String, List<Resource>> getCache(URL baseUrl){
        String key = baseUrl.toString();
        Cache<String, List<Resource>> cache = resourceStorage.get(key);
        if(cache != null){
            return cache;
        }
        cache = DefaultCacheExpirationTrigger.getCacheExpirationTrigger(3, TimeUnit.MINUTES)
                .getCache(key, () -> new LRUMapCache<String, List<Resource>>(1000, 180000));
        resourceStorage.put(key, cache);
        return cache;
    }

    @Override
    protected void addResource(Resource resource) throws Exception {
        String name = formatResourceName(resource.getName());
        List<Resource> resources = getCache(resource.getBaseUrl()).getOrDefault(name, ArrayList::new, true);
        resources.add(resource);
    }

    @Override
    public boolean exist(String name) {
        return get(name) != null;
    }

    @Override
    public Resource getFirst(String name) {
        if(ObjectUtils.isEmpty(name)){
            return null;
        }
        name = formatResourceName(name);
        for (Cache<String, List<Resource>> cache : resourceStorage.values()) {
            List<Resource> resources = cache.get(name);
            if(!ObjectUtils.isEmpty(resources)){
                return resources.get(0);
            }
        }
        return searchResource(name);
    }

    @Override
    public Enumeration<Resource> get(String name) {
        List<Resource> resources1 = new ArrayList<>();
        for (Cache<String, List<Resource>> cache : resourceStorage.values()) {
            List<Resource> resources = cache.get(name);
            if(!ObjectUtils.isEmpty(resources)){
                resources1.addAll(resources);
            }
        }
        if(!resources1.isEmpty()){
            return Collections.enumeration(resources1);
        }
        return searchResources(name);
    }

    @Override
    public InputStream getFirstInputStream(String name) {
        Resource resource = getFirst(name);
        if(resource == null){
            return null;
        }
        return openStream(resource);
    }

    @Override
    public Enumeration<InputStream> getInputStream(String name) {
        Enumeration<Resource> resources = searchResources(name);
        return openStream(resources);
    }

    @Override
    public synchronized void release() throws Exception {
        for (Cache<String, List<Resource>> value : resourceStorage.values()) {
            value.clear(ResourceUtils::release);
        }
        resourceStorage.clear();
    }

    @Override
    public void close() throws Exception {
        //resourceStorage.clear(ResourceUtils::release);
        super.close();
        release();
//        for (List<Resource> resources : resourceStorage.values()) {
//            ResourceUtils.release(resources);
//        }
    }


}
