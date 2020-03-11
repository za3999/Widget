package widget.cf.com.widgetlibrary.bubble;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.base.BaseCallBack;
import widget.cf.com.widgetlibrary.util.LogUtils;


public class DragBubbleFrameLayout extends FrameLayout {

    private DragBubbleView mBubbleView;
    boolean intercept = false;
    private View dragView;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public DragBubbleFrameLayout(@NonNull Context context) {
        super(context);
    }

    public DragBubbleFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DragBubbleFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!intercept) {
            return super.onInterceptTouchEvent(ev);
        } else {
            mBubbleView.touchDown(ev);
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!intercept) {
            return super.onTouchEvent(event);
        } else {
            mainHandler.removeCallbacksAndMessages(null);
            return mBubbleView.touchUpdate(event);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            setIntercept(false);
            if (mBubbleView != null) {
                mBubbleView.endBubble(true);
            }
        }
    }

    public boolean startDragBubbleView(View view, int yOffset, int color, BaseCallBack.CallBack1<Boolean> onResultListener) {
        if (intercept) {
            return false;
        }
        dragView = view;
        if (mBubbleView == null) {
            mBubbleView = findViewById(R.id.drag_bubble_view);
        }
        mBubbleView.initBubble(view, yOffset, color, isReset -> {
            LogUtils.d("drag", "DragBubbleFrameLayout drag result:" + isReset);
            setIntercept(false);
            BaseCallBack.onCallBack(onResultListener, isReset);
        });
        setIntercept(true);
        mainHandler.postDelayed(() -> forceStopDragBubble(view), 400);
        return true;
    }

    public void updateLocation(int yOffset) {
        if (mBubbleView != null && dragView != null) {
            if (dragView.isAttachedToWindow()) {
                mBubbleView.updateLocation(dragView, yOffset);
            } else {
                forceStopDragBubble(dragView);
            }
        }
    }

    public void forceStopDragBubble(View view) {
        if (dragView == null || dragView != view) {
            return;
        }
        if (mBubbleView != null) {
            mBubbleView.endBubble(true);
        }
        setIntercept(false);
    }

    public View getDragView() {
        return dragView;
    }

    private void setIntercept(boolean intercept) {
        this.intercept = intercept;
        if (!intercept) {
            dragView = null;
        }
    }
}
