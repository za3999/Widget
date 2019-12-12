package widget.cf.com.widgetlibrary.executor;

import android.text.TextUtils;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import widget.cf.com.widgetlibrary.util.ApplicationUtil;
import widget.cf.com.widgetlibrary.util.LogUtils;

public final class TaskRunner {

    private static final String TAG = "TaskRunner";

    public static boolean createFixThreadPool(String tag, int maxThreadCount) {
        ExecutorsManager.createFixThreadPool(tag, maxThreadCount);
        return true;
    }

    public static void setMaxThreadCount(String key, int maxThreadCount) {
        ExecutorsManager.setMaxThreadCount(key, maxThreadCount);
    }

    public static void doTask(Runner runner) {
        doTask("", ExecutorsManager.defaultMaxThreadCount, false, runner);
    }

    public static void doTask(boolean isNeedPostMainThread, Runner runner) {
        doTask("", ExecutorsManager.defaultMaxThreadCount, isNeedPostMainThread, runner);
    }

    public static void doTask(String tag, Runner runner) {
        doTask(tag, ExecutorsManager.defaultMaxThreadCount, false, runner);
    }

    public static void doTask(String tag, boolean isNeedPostMainThread, Runner runner) {
        doTask(tag, ExecutorsManager.defaultMaxThreadCount, isNeedPostMainThread, runner);
    }

    public static void doTask(String tag, int maxThreadCount, boolean isNeedPostMainThread, Runner runner) {
        Future future = ExecutorsManager.submit(tag, maxThreadCount, () -> {
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
            }
        });
        runner.setFuture(future);
    }

    public static void cancelAllTask() {
        ExecutorsManager.cancelAllTask();
    }

    public static void cancelFixThreadPendingTasks(String tag) {
        LogUtils.v(TAG, "cancelFixThreadPendingTasks:" + tag);
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        ThreadPoolExecutor threadPool = ExecutorsManager.getExecutorService(tag);
        if (threadPool != null) {
            threadPool.getQueue().clear();
        }
    }

    public static abstract class Runner<T, K> {
        private T inputData;
        private K outputData;
        private Future mFuture;

        public Runner(T data) {
            inputData = data;
        }

        public T getInputData() {
            return inputData;
        }

        public void cancel() {
            LogUtils.v(TAG, "cancel:" + this);
            if (mFuture != null) {
                mFuture.cancel(true);
            }
        }

        public boolean isCanceling() {
            return mFuture == null ? true : mFuture.isCancelled();
        }

        private Runner setFuture(Future future) {
            this.mFuture = future;
            return this;
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
