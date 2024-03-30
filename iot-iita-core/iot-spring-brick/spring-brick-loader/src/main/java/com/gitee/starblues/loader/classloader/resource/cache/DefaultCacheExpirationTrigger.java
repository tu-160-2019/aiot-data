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

package com.gitee.starblues.loader.classloader.resource.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 默认缓存过期调度实现
 *
 * @author starBlues
 * @since 3.1.1
 * @version 3.1.1
 */
public class DefaultCacheExpirationTrigger implements CacheExpirationTrigger{

    private final Map<String, Cache<?,?>> cacheMap = new ConcurrentHashMap<>();

    private ScheduledExecutorService scheduledExecutor;

    private static volatile DefaultCacheExpirationTrigger TRIGGER;

    private final long delay;
    private final TimeUnit unit;

    private DefaultCacheExpirationTrigger(long delay, TimeUnit unit){
        this.delay = delay;
        this.unit = unit;
        start();
    }

    public static CacheExpirationTrigger getCacheExpirationTrigger(long delay, TimeUnit unit){
        if (TRIGGER == null){
            synchronized(DefaultCacheExpirationTrigger.class){
                TRIGGER = new DefaultCacheExpirationTrigger(delay, unit);
            }
        }
        return TRIGGER;
    }

    @Override
    public synchronized void addCache(String key, Cache<?, ?> cache) {
        if(cacheMap.containsKey(key)){
            throw new IllegalStateException(key + " already exists!");
        }
        cacheMap.put(key, cache);
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized <K, V> Cache<K, V> getCache(String key, Supplier<Cache<K, V>> cacheSupplier) {
        Cache<?, ?> cache = cacheMap.get(key);
        if(cache == null){
            Cache<K, V> supplier = cacheSupplier.get();
            if(supplier == null){
                return null;
            }
            cacheMap.put(key, supplier);
            return supplier;
        } else {
            return (Cache<K, V>) cache;
        }
    }

    @Override
    public synchronized void removeCache(String key) {
        cacheMap.remove(key);
    }

    @Override
    public synchronized void start() {
        scheduledExecutor  = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(this::cleanUp, delay, delay, unit);
    }

    @Override
    public synchronized void stop() {
        scheduledExecutor.shutdownNow();
    }

    private void cleanUp(){
        if(cacheMap.isEmpty()){
            return;
        }
        cacheMap.forEach((k,v)->{
            try {
                v.cleanExpired();
            } catch (Exception e){
                // log.warn("Checking clean cache:{} Exceptions. {}", k, e.getMessage());
            }
        });
    }

}
