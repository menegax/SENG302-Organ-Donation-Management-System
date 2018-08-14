package utility;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class CachedThreadPool {

    private static CachedThreadPool cachedThreadPool = null;

    private ExecutorService service;

    private CachedThreadPool() {
        service = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true); //create daemon threads
            return t;
        }); //create once
    }

    public static CachedThreadPool getCachedThreadPool() {
        if (cachedThreadPool == null) {
            cachedThreadPool = new CachedThreadPool();
        }
        return cachedThreadPool;
    }


    public ExecutorService getThreadService() {
        return service;
    }


}
