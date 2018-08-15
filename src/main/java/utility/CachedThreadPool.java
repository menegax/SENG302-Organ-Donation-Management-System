package utility;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedThreadPool {

    private static CachedThreadPool cachedThreadPool = null;

    private ExecutorService service;

    /**
     * Private constructor, adds in custom thread factory
     */
    private CachedThreadPool() {
        service = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true); //create daemon threads
            return t;
        }); //create once
    }

    /**
     * Get the single instance of the thread pool
     * @return - CachedThreadPool
     */
    public static CachedThreadPool getCachedThreadPool() {
        if (cachedThreadPool == null) {
            cachedThreadPool = new CachedThreadPool();
        }
        return cachedThreadPool;
    }


    /**
     * Get the executor thread service
     * @return - return Executor thread service
     */
    public ExecutorService getThreadService() {
        return service;
    }


}
