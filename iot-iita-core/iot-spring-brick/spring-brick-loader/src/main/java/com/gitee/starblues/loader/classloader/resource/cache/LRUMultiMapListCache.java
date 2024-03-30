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

import com.gitee.starblues.loader.utils.ObjectUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * LRU MultiMap List 缓存实现。list 集合中的元素单个过期
 *
 * @author starBlues
 * @since 3.1.1
 * @version 3.1.1
 */
@Deprecated
public class LRUMultiMapListCache<K, V> implements MultiCache<K, V>{

    private final Map<K, List<Entity<V>>> cacheMap;

    private final StampedLock lock = new StampedLock();

    private final int maxSize;
    private final long timeout;

    private final AtomicInteger size = new AtomicInteger(0);

    public LRUMultiMapListCache(int maxSize, long timeout){
        this.maxSize = maxSize;
        this.timeout = timeout;
        this.cacheMap = new CacheLinkedHashMap<K, List<Entity<V>>>(maxSize, entry->{
            List<Entity<V>> value = entry.getValue();
            if(value != null){
                size.addAndGet(-value.size());
            }
        });
    }

    @Override
    public void put(K key, Collection<V> value) {
        if(ObjectUtils.isEmpty(value)){
            return;
        }
        long stamp = lock.writeLock();
        try {
            List<Entity<V>> entityAddedList = value.stream()
                    .map(v-> new Entity<>(v, timeout))
                    .collect(Collectors.toList());
            if (isFull(key)) {
                cleanExpired(false);
            }
            List<Entity<V>> entityList = cacheMap.computeIfAbsent(key, k -> new ArrayList<>());
            entityList.addAll(entityAddedList);
            size.addAndGet(value.size());
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void putSingle(K key, V value) {
        put(key, ObjectUtils.toList(value));
    }

    @Override
    public V getFirst(K key) {
        long stamp = lock.tryOptimisticRead();
        List<Entity<V>> entityList = cacheMap.get(key);
        if(!lock.validate(stamp)){
            stamp = lock.readLock();
            try {
                entityList = cacheMap.get(key);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        if(ObjectUtils.isEmpty(entityList)){
            return null;
        }
        Iterator<Entity<V>> iterator = entityList.iterator();
        while (iterator.hasNext()){
            Entity<V> entity = iterator.next();
            if(entity.isExpired()){
                iterator.remove();
                size.addAndGet(-1);
            } else {
                return entity.getValue();
            }
        }
        return null;
    }

    @Override
    public int size() {
        long stamp = lock.tryOptimisticRead();
        int s = size.get();
        if(!lock.validate(stamp)){
            stamp = lock.readLock();
            try {
                s = size.get();
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return s;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public Collection<V> get(K key) {
        long stamp = lock.tryOptimisticRead();
        List<Entity<V>> entityList = cacheMap.get(key);
        if(!lock.validate(stamp)){
            stamp = lock.readLock();
            try {
                entityList = cacheMap.get(key);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        if(ObjectUtils.isEmpty(entityList)){
            return null;
        }
        Iterator<Entity<V>> iterator = entityList.iterator();
        List<V> result = new ArrayList<>(entityList.size());
        while (iterator.hasNext()){
            Entity<V> entity = iterator.next();
            if(entity.isExpired()){
                iterator.remove();
                size.addAndGet(-1);
            } else {
                result.add(entity.getValue());
            }
        }
        return result;
    }

    @Override
    public Collection<V> getOrDefault(K key, Supplier<Collection<V>> supplier, boolean defaultAdded) {
        Collection<V> collection = get(key);
        if(!ObjectUtils.isEmpty(collection)){
            return collection;
        }
        Collection<V> addCollection = supplier.get();
        if(ObjectUtils.isEmpty(addCollection)){
            return null;
        }
        if(defaultAdded){
            put(key, addCollection);
            return addCollection;
        }
        return null;
    }



    @Override
    public Collection<V> remove(K key) {
        long stamp = lock.writeLock();
        try {
            List<Entity<V>> cacheValue = cacheMap.remove(key);
            if (cacheValue == null) {
                return null;
            }
            size.addAndGet(-cacheValue.size());
            return cacheValue.stream()
                    .map(Entity::getValue)
                    .collect(Collectors.toList());
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public void clear() {
        clear(null);
    }

    @Override
    public void clear(Consumer<Collection<V>> consumer) {
        long stamp = lock.writeLock();
        try {
            if(consumer == null){
                cacheMap.clear();
                return;
            }
            Iterator<Map.Entry<K, List<Entity<V>>>> iterator = cacheMap.entrySet().iterator();
            while (iterator.hasNext()){
                try {
                    Map.Entry<K, List<Entity<V>>> entityMap = iterator.next();
                    List<Entity<V>> entityList = entityMap.getValue();
                    if(entityList == null){
                        iterator.remove();
                        continue;
                    }
                    List<V> values = entityList.stream().map(Entity::getValue).collect(Collectors.toList());
                    consumer.accept(values);
                    iterator.remove();
                } catch (Exception e){
                    // 忽略
                }
            }
        } finally {
            size.set(0);
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public int cleanExpired() {
        return cleanExpired(true);
    }

    private boolean isFull(K key) {
        if (maxSize == 0) {
            return false;
        }
        if(size.get() < maxSize){
            return false;
        }
        return !cacheMap.containsKey(key);
    }

    public int cleanExpired(boolean isLock) {
        if(!isLock){
            return actualCleanExpired();
        }
        long stamp = lock.writeLock();
        try {
            return actualCleanExpired();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    private int actualCleanExpired(){
        Iterator<Map.Entry<K, List<Entity<V>>>> cacheMapIterator = cacheMap.entrySet().iterator();
        int removeCount = 0;
        while (cacheMapIterator.hasNext()){
            Map.Entry<K, List<Entity<V>>> entityMap = cacheMapIterator.next();
            List<Entity<V>> value = entityMap.getValue();
            if(ObjectUtils.isEmpty(value)){
                cacheMapIterator.remove();
                continue;
            }
            int valueSize = value.size();
            Iterator<Entity<V>> entityIterator = value.iterator();
            int removeSize = 0;
            while (entityIterator.hasNext()){
                Entity<V> entity = entityIterator.next();
                if(entity == null || entity.isExpired()){
                    entityIterator.remove();
                    removeSize++;
                }
            }
            if(valueSize == removeSize){
                cacheMapIterator.remove();
            }
            size.addAndGet(-removeSize);
            removeCount = removeCount + removeSize;
        }
        return removeCount;
    }

}
