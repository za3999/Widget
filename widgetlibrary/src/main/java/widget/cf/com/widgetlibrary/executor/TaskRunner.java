package widget.cf.com.widgetlibrary.executor;

import android.text.TextUtils;

import java.util.concurrent.Future;

import widget.cf.com.widgetlibrary.util.ApplicationUtil;
import widget.cf.com.widgetlibrary.util.LogUtils;

public final class TaskRunner {

    private static final String TAG = "TaskRunner";

    public static boolean createFixThreadPool(String tag, int maxThreadCount) {
        ExecutorsManager.createFixThreadPool(tag, maxThreadCount);
        return true;
    }

    public static void setMaxThreadCount(String key, int maxThreadCount) {
        if (maxThreadCount <= 0) {
            LogUtils.e(TAG, "maxThreadCount must greater than 0");
            return;
        }
        ExecutorsManager.setMaxThreadCount(key, maxThreadCount);
    }

    public static void cancelAllTask() {
        ExecutorsManager.cancelAllTask();
    }

    public static void cancelTask(String tag) {
        LogUtils.v(TAG, "cancelFixThreadPendingTasks:" + tag);
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        ExecutorsManager.cancelTask(tag);
    }

    public static abstract class Runner {
        private Object inputData;
        private Object outputData;
        private Future mFuture;
        private String tag;
        private boolean isMainResult;

        public Runner(Object data) {
            inputData = data;
        }

        public Object getInputData() {
            return inputData;
        }

        public void cancel() {
            LogUtils.v(TAG, "cancel:" + this);
            if (mFuture != null) {
                mFuture.cancel(true);
            }
        }

        public boolean isCanceling() {
            return mFuture == null || mFuture.isCancelled();
        }

        public String getTag() {
            return tag;
        }

        public Runner setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public Runner setMainResult(boolean mainResult) {
            isMainResult = mainResult;
            return this;
        }

        public boolean isMainResult() {
            return isMainResult;
        }

        public Runner start() {
            mFuture = ExecutorsManager.submit(tag, () -> {
                try {
                    LogUtils.v(TAG, "doTask:" + Thread.currentThread().getName());
                    outputData = run(inputData);
                    if (!isCanceling()) {
                        if (isMainResult()) {
                            ApplicationUtil.getMainHandler().post(() -> onResult(outputData));
                        } else {
                            onResult(outputData);
                        }
                    }
                } catch (Exception e) {
                    onInterrupted(inputData);
                    e.printStackTrace();
                }
            });
            return this;
        }

        public abstract Object run(Object data) throws InterruptedException;

        public abstract void onInterrupted(Object data);

        public abstract void onResult(Object data);
    }


    public abstract static class RunnerWrapper<T, K> extends TaskRunner.Runner {

        public RunnerWrapper(T data) {
            super(data);
        }

        public abstract K runWrapper();

        public void onInterrupted() {

        }

        public void onResultWrapper(K resultData) {

        }

        @Override
        public final T getInputData() {
            return (T) super.getInputData();
        }

        @Override
        public final Object run(Object data) {
            return runWrapper();
        }

        @Override
        public final void onInterrupted(Object data) {
            onInterrupted();
        }

        @Override
        public final void onResult(Object data) {
            onResultWrapper((K) data);
        }
    }

    public abstract static class RunnerOnlyOutput<K> extends RunnerWrapper<Void, K> {

        public RunnerOnlyOutput() {
            super(null);
        }
    }

    public abstract static class RunnerOnlyInput<T> extends RunnerWrapper<T, Void> {

        public RunnerOnlyInput(T data) {
            super(data);
        }

        public abstract void run();

        public void onResult() {

        }

        @Override
        public final Void runWrapper() {
            run();
            return null;
        }

        @Override
        public final void onResultWrapper(Void resultData) {
            onResult();
        }
    }

    public abstract static class RunnerNoParam extends RunnerOnlyInput<Void> {

        public RunnerNoParam() {
            super(null);
        }

        public void onResult() {

        }

    }

    public abstract static class Runner2<T, M, K> extends RunnerWrapper<T, K> {

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
