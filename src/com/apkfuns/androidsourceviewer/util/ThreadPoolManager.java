package com.apkfuns.androidsourceviewer.util;


import java.util.concurrent.*;

/**
 * Thread Pool Manager
 */
public class ThreadPoolManager {
    private static ThreadPoolManager singleton;
    private ScheduledExecutorService serviceDelayManager;
    private ExecutorService ioActionPoolService;

    public static ThreadPoolManager getInstance() {
        if (singleton == null) {
            synchronized (ThreadPoolManager.class) {
                if (singleton == null) {
                    singleton = new ThreadPoolManager();
                }
            }
        }
        return singleton;
    }

    private ThreadPoolManager() {
        int num = Runtime.getRuntime().availableProcessors();
        serviceDelayManager = Executors.newScheduledThreadPool(num <= 2 ? 1 : num / 2);
        ioActionPoolService =
                new ThreadPoolExecutor(num, num, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
    }

    public synchronized ScheduledFuture addTaskDelay(Runnable runnable, long delay) {
        return serviceDelayManager.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public void addTask(Runnable runnable) {
        ioActionPoolService.execute(runnable);
    }
}
