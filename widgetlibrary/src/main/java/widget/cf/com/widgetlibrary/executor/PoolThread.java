package widget.cf.com.widgetlibrary.executor;

import android.os.Handler;

import java.util.concurrent.Future;

import widget.cf.com.widgetlibrary.base.BaseCallBack;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public abstract class PoolThread<I, R> {

    private I inputData;
    private R result;
    private Future mFuture;
    private String tag;
    private boolean isMainResult;
    private boolean isCancel;
    private Handler handler = ApplicationUtil.getMainHandler();

    public PoolThread(I data) {
        inputData = data;
    }

    public I getInputData() {
        return inputData;
    }

    public void cancel() {
        isCancel = true;
        if (mFuture != null) {
            mFuture.cancel(true);
        }
    }

    public boolean isCanceling() {
        return isCancel;
    }

    public String getTag() {
        return tag;
    }

    public PoolThread setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public PoolThread setMainResult(boolean mainResult) {
        isMainResult = mainResult;
        return this;
    }

    public boolean isMainResult() {
        return isMainResult;
    }

    public void start() {
        start(null);
    }

    public void start(BaseCallBack.CallBack1<PoolThread> onSubmit) {
        ApplicationUtil.runOnMainThread(() -> {
                    mFuture = ThreadPoolManager.submit(tag, () -> {
                        try {
                            if (isCanceling()) {
                                return;
                            }
                            result = PoolThread.this.run(inputData);
                            if (isCanceling()) {
                                return;
                            }
                            if (isMainResult()) {
                                handler.post(() -> onResult(result));
                            } else {
                                onResult(result);
                            }
                        } catch (Exception e) {
                            onException(e);
                        }
                    });
                    if (onSubmit != null) onSubmit.onCallBack(PoolThread.this);
                }
        );
    }

    public void restart() {
        if (isCanceling() || mFuture == null) {
            isCancel = false;
            start();
        }
    }

    public void delayStart(long delayMillis) {
        ApplicationUtil.getBgHandler().postDelayed(() -> start(), delayMillis);
    }

    public abstract R run(I data);

    public void onException(Exception e) {
    }

    public void onResult(R result) {
    }
}
