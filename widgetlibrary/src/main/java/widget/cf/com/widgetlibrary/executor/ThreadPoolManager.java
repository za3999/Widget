package widget.cf.com.widgetlibrary.executor;

import android.text.TextUtils;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import widget.cf.com.widgetlibrary.util.LogUtils;

public class ThreadPoolManager {
    private static final String TAG = "ThreadPoolManager";
    public static final int defaultMaxThreadCount = 1;
    private static ThreadPoolManager mInstance = new ThreadPoolManager();
    private volatile ThreadPoolExecutor defaultExecutorService;
    private Map<String, ThreadPoolExecutor> executorServiceMap = new ConcurrentHashMap<>();

    private static ThreadPoolManager getInstance() {
        return mInstance.checkDefault();
    }

    private ThreadPoolManager checkDefault() {
        if (defaultExecutorService == null) {
            synchronized (this) {
                if (defaultExecutorService == null) {
                    defaultExecutorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 30, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactory("default"));
                }
            }
        }
        return this;
    }

    public static void createFixThreadPool(String tag, int maxThreadCount) {
        getInstance().getExecutorService(tag, maxThreadCount);
    }

    public static Future submit(String key, Runnable runnable) {
        return submit(key, defaultMaxThreadCount, runnable);
    }

    public static <T> Future<T> submit(String key, Callable<T> callable) {
        return submit(key, defaultMaxThreadCount, callable);
    }

    public static Future submit(String key, int maxThreadCount, Runnable runnable) {
        ThreadPoolExecutor executorService;
        if (TextUtils.isEmpty(key)) {
            executorService = getInstance().defaultExecutorService;
        } else {
            executorService = getInstance().getExecutorService(key, maxThreadCount);
        }
        return executorService.submit(runnable);
    }

    public static <T> Future<T> submit(String key, int maxThreadCount, Callable<T> callable) {
        ExecutorService executorService;
        if (TextUtils.isEmpty(key)) {
            executorService = getInstance().defaultExecutorService;
        } else {
            executorService = getInstance().getExecutorService(key, maxThreadCount);
        }
        return executorService.submit(callable);
    }

    public static void setMaxThreadCount(String key, int maxThreadCount) {
        ThreadPoolExecutor threadPoolExecutor = getInstance().getExecutorService(key, defaultMaxThreadCount);
        threadPoolExecutor.setCorePoolSize(maxThreadCount);
        threadPoolExecutor.setMaximumPoolSize(maxThreadCount);
    }

    public static void cancelAllTask() {
        if (mInstance.defaultExecutorService != null) {
            mInstance.defaultExecutorService.getQueue().clear();
        }
        synchronized (mInstance.executorServiceMap) {
            for (ThreadPoolExecutor threadPool : mInstance.executorServiceMap.values()) {
                threadPool.getQueue().clear();
            }
        }
    }

    public static void cancelTask(String key) {
        getExecutorService(key).getQueue().clear();
    }

    public static ThreadPoolExecutor getExecutorService(String key) {
        if (TextUtils.isEmpty(key)) {
            return getInstance().defaultExecutorService;
        }
        return getInstance().executorServiceMap.get(key);
    }

    private ThreadPoolExecutor getExecutorService(String key, int maxThreadCount) {
        ThreadPoolExecutor executorService = executorServiceMap.get(key);
        if (executorService == null) {
            synchronized (executorServiceMap) {
                if (executorService == null) {
                    executorServiceMap.put(key, createExecutorService(key, maxThreadCount, 30));
                    executorService = executorServiceMap.get(key);
                }
            }
        }
        return executorService;
    }

    private ThreadPoolExecutor createExecutorService(String key, int maximumPoolSize, long keepAliveTime) {
        if (maximumPoolSize > 5) {
            LogUtils.w(TAG, "request too many thread:" + key + "-" + maximumPoolSize);
        }
        LogUtils.d(TAG, "createExecutorService:" + key + "|" + maximumPoolSize);
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(maximumPoolSize, maximumPoolSize, keepAliveTime,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadFactory(key));
        executorService.allowCoreThreadTimeOut(true);
        return executorService;
    }

    private static class ThreadFactory implements java.util.concurrent.ThreadFactory {

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String key;

        ThreadFactory(String key) {
            this.key = key;
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        }

        public Thread newThread(Runnable r) {
            int i = threadNumber.getAndIncrement();
            Thread t = new Thread(group, r, "pool-" + key + "-thread-" + i, 0);
            LogUtils.d(TAG, "newThread:" + t.getName());
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
