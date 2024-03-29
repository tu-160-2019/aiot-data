package com.gitee.starblues.loader.classloader.resource.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 注释
 *
 * @author starBlues
 * @since 1.0.0
 * @version 1.0.0
 */
public class CacheLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    private final int size;
    private final RemoveListener<K, V> removeListener;

    public CacheLinkedHashMap(int size) {
        this(size, null);
    }

    public CacheLinkedHashMap(int size, RemoveListener<K, V> removeListener) {
        super(size + 1, 1.0f, true);
        this.size = size;
        this.removeListener = removeListener;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (size == 0) {
            return false;
        }
        int removeSize = size() - size;
        if(removeSize > 0){
            if(removeListener != null){
                try {
                    removeListener.remove(eldest);
                } catch (Exception e){
                    // 忽略
                }
            }
            return true;
        }
        return false;
    }

    @FunctionalInterface
    public interface RemoveListener<K, V>{

        void remove(Map.Entry<K, V> eldest);

    }

}