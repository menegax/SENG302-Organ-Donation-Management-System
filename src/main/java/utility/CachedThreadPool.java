package utility;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedThreadPool {

    private static CachedThreadPool cachedThreadPool = null;

    private ExecutorService service;

    private CachedThreadPool() {
        service = Executors.newCachedThreadPool(); //create once
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
