package widget.cf.com.widgetlibrary.executor;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import widget.cf.com.widgetlibrary.util.ApplicationUtil;
import widget.cf.com.widgetlibrary.util.LogUtils;

public final class TaskRunner {

    private static final String TAG = "TaskRunner";
    private static Map<String, Set<Runner>> futureMap = new ConcurrentHashMap<>();
    private static Handler mHandler;

    static {
        HandlerThread handlerThread = new HandlerThread("task_runner_bg_thread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }

    public static boolean createFixThreadPool(String tag, int maxThreadCount) {
        ExecutorsManager.createFixThreadPool(tag, maxThreadCount);
        return true;
    }

    public static void setMaxThreadCount(String key, int maxThreadCount) {
        ExecutorsManager.setMaxThreadCount(key, maxThreadCount);
    }

    public static void doTask(Runner runner) {
        doTask("", false, runner);
    }

    public static void doTask(boolean isNeedPostMainThread, Runner runner) {
        doTask("", isNeedPostMainThread, runner);
    }

    public static void doTask(String tag, Runner runner) {
        doTask(tag, false, runner);
    }

    public static void doTask(String tag, boolean isNeedPostMainThread, Runner runner) {
        if (!futureMap.containsKey(tag)) {
            futureMap.put(tag, new HashSet<>());
        }
        Future future = ExecutorsManager.submit(tag, () -> {
            try {
                LogUtils.v(TAG, "doTask:" + Thread.currentThread().getName());
                runner.outputData = runner.run();
                if (isNeedPostMainThread) {
                    ApplicationUtil.getMainHandler().post(() -> runner.onResult(runner.outputData));
                } else {
                    runner.onResult(runner.outputData);
                }
            } catch (Exception e) {
                runner.onException(e);
            } finally {
                mHandler.post(() -> futureMap.get(tag).remove(runner));
            }
        });
        runner.setFuture(future);
        runner.setRunnerSet(futureMap.get(tag));
        mHandler.post(() -> futureMap.get(tag).add(runner));
    }

    public static void cancelFixThreadPendingTasks(String tag) {
        LogUtils.v(TAG, "cancelFixThreadPendingTasks:" + tag);
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        Set<Runner> futureSet = futureMap.get(tag);
        mHandler.post(() -> {
            if (futureSet != null) {
                Iterator<Runner> iterator = futureSet.iterator();
                while (iterator.hasNext()) {
                    iterator.next().cancel();
                    iterator.remove();
                }
            }
        });
    }

    public static abstract class Runner<T, K> {
        private T inputData;
        private K outputData;
        private volatile boolean isCancelRequesting;
        private Future mFuture;
        private Set<Runner> runnerSet;

        public Runner(T data) {
            inputData = data;
        }

        public T getInputData() {
            return inputData;
        }

        public void cancel() {
            LogUtils.v(TAG, "cancel:" + this);
            isCancelRequesting = true;
            if (mFuture != null) {
                mFuture.cancel(true);
            }
            mHandler.post(() -> {
                if (runnerSet != null) {
                    runnerSet.remove(Runner.this);
                }
            });
        }

        public boolean isCanceling() {
            return isCancelRequesting;
        }

        private Runner setFuture(Future future) {
            this.mFuture = future;
            return this;
        }

        private void setRunnerSet(Set<Runner> runnerSet) {
            this.runnerSet = runnerSet;
        }

        public abstract K run() throws InterruptedException;

        public abstract void onException(Exception e);

        public abstract void onResult(K k);
    }


    public abstract static class RunnerWithOutIn<K> extends Runner<Void, K> {

        public RunnerWithOutIn() {
            super(null);
        }
    }

    public abstract static class RunnerNotOut<T> extends Runner<T, Void> {

        public RunnerNotOut(T data) {
            super(data);
        }
    }

    public abstract static class RunnerNoParam extends Runner<Void, Void> {

        public RunnerNoParam() {
            super(null);
        }
    }

    public abstract static class Runner2<T, M, K> extends Runner<T, K> {

        private M second;

        public Runner2(T data, M second) {
            super(data);
            this.second = second;
        }

        public M getSecond() {
            return second;
        }
    }

    public abstract static class Runner3<T, M, N, K> extends Runner2<T, M, K> {

        private N third;

        public Runner3(T data, M second, N third) {
            super(data, second);
            this.third = third;
        }

        public N getThird() {
            return third;
        }
    }

    public abstract static class Runner4<T, M, N, O, K> extends Runner3<T, M, N, K> {

        private O four;

        public Runner4(T data, M second, N third, O four) {
            super(data, second, third);
            this.four = four;
        }

        public O getFour() {
            return four;
        }
    }
}
