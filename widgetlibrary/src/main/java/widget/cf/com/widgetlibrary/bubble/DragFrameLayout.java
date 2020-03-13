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


public class DragFrameLayout extends FrameLayout {

    private DragBubbleView mBubbleView;
    boolean bubbleIntercept = false;
    private View dragView;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public DragFrameLayout(@NonNull Context context) {
        super(context);
    }

    public DragFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DragFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (bubbleIntercept) {
            mBubbleView.touchDown(ev);
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (bubbleIntercept) {
            mainHandler.removeCallbacksAndMessages(null);
            return mBubbleView.touchUpdate(event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            setBubbleIntercept(false);
            if (mBubbleView != null) {
                mBubbleView.endBubble(true);
            }
        }
    }

    public boolean startDragBubbleView(View view, int yOffset, int color, BaseCallBack.CallBack1<Boolean> onResultListener) {
        if (bubbleIntercept) {
            return false;
        }
        dragView = view;
        if (mBubbleView == null) {
            mBubbleView = findViewById(R.id.drag_bubble_view);
        }
        mBubbleView.initBubble(view, yOffset, color, isReset -> {
            LogUtils.d("drag", "DragBubbleFrameLayout drag result:" + isReset);
            setBubbleIntercept(false);
            BaseCallBack.onCallBack(onResultListener, isReset);
        });
        setBubbleIntercept(true);
        view.setVisibility(View.INVISIBLE);
        mainHandler.postDelayed(() -> forceStopDragBubble(), 400);
        return true;
    }

    public void updateLocation(int yOffset) {
        if (mBubbleView != null && dragView != null) {
            if (dragView.isAttachedToWindow()) {
                mBubbleView.updateLocation(dragView, yOffset);
            } else {
                forceStopDragBubble();
            }
        }
    }

    public void forceStopDragBubble() {
        if (dragView == null) {
            return;
        }
        if (mBubbleView != null) {
            mBubbleView.endBubble(true);
        }
        setBubbleIntercept(false);
    }

    public View getDragView() {
        return dragView;
    }

    private void setBubbleIntercept(boolean bubbleIntercept) {
        this.bubbleIntercept = bubbleIntercept;
        if (!bubbleIntercept) {
            dragView = null;
        }
    }
}
