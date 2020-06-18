package widget.cf.com.widgetlibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import widget.cf.com.widgetlibrary.util.LogUtils;

/**
 * 自定义一个可以滑动删除listView的item布局
 */

public class SwipeLayout extends FrameLayout {

    private View targetView;
    private View followView;
    private ViewDragHelper mViewDragHelper;
    private SwipeLayoutManager manager;
    private boolean isDisabled = false;
    private float mThreshold = 0.5f;
    private float downX, downY;
    private float offset;
    private VelocityTracker velocityTracker;
    private float velocity;
    private float minFlingVelocity;

    enum SwipeState {
        Open, Close
    }

    private SwipeState currentState = SwipeState.Close;

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setSwipeManager(SwipeLayoutManager manager) {
        this.manager = manager;
    }

    private void init() {
        minFlingVelocity = 400 * getContext().getResources().getDisplayMetrics().density;
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {

            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return followView.getWidth();
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                LogUtils.d("clampViewPositionHorizontal", left);
                if (child == targetView) {
                    if (left > 0) {
                        left = 0;
                    } else if (left < -followView.getWidth()) {
                        left = -followView.getWidth();
                    }
                }
                return left;
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                if (changedView == followView) {
                    if (dx > 0) {
                        offset += dx;
                        if (offset > 0) {
                            offset = 0;
                        }
                    } else {
                        offset = -followView.getWidth();
                    }

                } else {
                    offset = left;
                }
                requestLayout();
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                float changeOffset = followView.getWidth() * mThreshold;
                if (Math.abs(velocity) > minFlingVelocity) {
                    if (velocity > changeOffset) {
                        close();
                    } else if (velocity < -minFlingVelocity) {
                        open();
                    }
                } else {
                    if (offset < -changeOffset) {
                        open();
                    } else {
                        close();
                    }
                }
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        targetView = getChildAt(0);
        followView = getChildAt(1);
    }

    public void setOpen(boolean isOpen) {
        offset = isOpen ? -followView.getWidth() : 0;
        currentState = isOpen ? SwipeState.Open : SwipeState.Close;
    }

    public boolean isOpen() {
        return currentState == SwipeState.Open;
    }

    public void setDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    /**
     * @param threshold 0 - 1
     */
    public void setThreshold(float threshold) {
        if (threshold < 0 || threshold > 1) {
            return;
        }
        this.mThreshold = threshold;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        targetView.layout((int) offset, 0, targetView.getMeasuredWidth() + (int) offset, getMeasuredHeight());
        int rightViewStart = targetView.getWidth() + (int) offset;
        followView.layout(rightViewStart, 0, rightViewStart + followView.getMeasuredWidth(), getMeasuredHeight());
        if (followView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) followView;
            int count = viewGroup.getChildCount();
            float offsetScale = Math.abs(offset) / followView.getWidth();
            int start = 0;
            for (int i = 0; i < count; i++) {
                View view = viewGroup.getChildAt(i);
                int viewLayoutWith;
                if (i == count - 1) {
                    viewLayoutWith = view.getMeasuredWidth();
                } else {
                    viewLayoutWith = (int) (view.getMeasuredWidth() * offsetScale);
                }

                view.layout(start, 0, start + viewLayoutWith, getMeasuredHeight());
                start += viewLayoutWith;
            }
        }

        if (currentState == SwipeState.Open) {
            if (manager != null) {
                manager.setSwipeLayout(SwipeLayout.this);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (followView.getWidth() == 0 || followView.getVisibility() == GONE || followView.getVisibility() == INVISIBLE) {
            return super.dispatchTouchEvent(ev);
        }

        if (isDisabled) {
            return super.dispatchTouchEvent(ev);
        }

        if (currentState == SwipeState.Open) {
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (followView.getWidth() == 0 || followView.getVisibility() == GONE || followView.getVisibility() == INVISIBLE) {
            return super.onInterceptTouchEvent(ev);
        }

        if (isDisabled) {
            return super.onInterceptTouchEvent(ev);
        }

        boolean result = mViewDragHelper.shouldInterceptTouchEvent(ev);

        if (manager != null && !manager.isShouldSwipe(this)) {
            manager.closeCurrentLayout();
            result = true;
        }

        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (followView.getWidth() == 0 || followView.getVisibility() == GONE || followView.getVisibility() == INVISIBLE) {
            return super.onTouchEvent(event);
        }

        if (isDisabled) {
            return super.onTouchEvent(event);
        }

        if (event.getAction() != MotionEvent.ACTION_MOVE &&
                manager != null &&
                !manager.isShouldSwipe(this)) {
//            requestDisallowInterceptTouchEvent(true);
            return true;
        }

        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }

        velocityTracker.addMovement(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - downX) > Math.abs(event.getY() - downY)) {
                    if (currentState == SwipeState.Close && event.getX() > downX) {
                        break;
                    }

                    requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                velocityTracker.computeCurrentVelocity(1000);
                velocity = velocityTracker.getXVelocity();

                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                break;
        }

        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    protected void open() {
        currentState = SwipeState.Open;
        if (listener != null) {
            listener.onOpen(getTag());
        }
        if (manager != null) {
            manager.setSwipeLayout(SwipeLayout.this);
        }
        mViewDragHelper.smoothSlideViewTo(targetView, -followView.getWidth(), 0);
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    protected void close() {
        close(true);
    }

    protected void close(boolean smooth) {
        currentState = SwipeState.Close;
        if (listener != null) {
            listener.onClose(getTag());
        }
        if (manager != null) {
            manager.clearCurrentLayout();
        }
        if (smooth) {
            mViewDragHelper.smoothSlideViewTo(targetView, 0, 0);
            ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
        } else {
            offset = 0;
            requestLayout();
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private OnSwipeStateChangeListener listener;

    public void setOnSwipeStateChangeListener(OnSwipeStateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnSwipeStateChangeListener {

        void onOpen(Object tag);

        void onClose(Object tag);
    }
}
