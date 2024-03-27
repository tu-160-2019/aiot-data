package net.srt.flink.function.context;

import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.Set;

/**
 * @author ZackYoung
 * @since 0.7.0
 */
public class UdfPathContextHolder {
    private static final ThreadLocal<Set<String>> UDF_PATH_CONTEXT = new ThreadLocal<>();

    public static void add(String path) {
        if (UDF_PATH_CONTEXT.get() == null) {
            UDF_PATH_CONTEXT.set(new ConcurrentHashSet<>());
        }
        UDF_PATH_CONTEXT.get().add(path);
    }

    public static Set<String> get() {
        return UDF_PATH_CONTEXT.get();
    }

    public static void clear() {
        UDF_PATH_CONTEXT.remove();
    }
}
