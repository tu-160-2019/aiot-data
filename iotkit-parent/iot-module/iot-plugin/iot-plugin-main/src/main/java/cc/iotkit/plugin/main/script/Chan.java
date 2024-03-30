package cc.iotkit.plugin.main.script;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

@Slf4j
public class Chan<T> {

    private static final long THREAD_WAIT_TIME = 1000_000L * 10_000;

    private volatile T data;

    private volatile Thread t;

    private Chan() {

    }

    public static Chan getInstance() {
        return ChanSingleton.INSTANCE;
    }

    public T get(Object blocker) {
        this.t = Thread.currentThread();
        LockSupport.parkNanos(blocker, THREAD_WAIT_TIME);
        this.t = null;
        return data;
    }

    public void put(T data) {
        this.data = data;
        if (t == null) {
            return;
        }
        LockSupport.unpark(t);
    }

    private static class ChanSingleton {
        private static final Chan<?> INSTANCE = new Chan<>();
    }
}