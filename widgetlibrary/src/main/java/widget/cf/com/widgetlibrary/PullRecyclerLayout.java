package widget.cf.com.widgetlibrary;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PullRecyclerLayout extends FrameLayout {

    private static final long ANIM_TIME = 300;
    private static final float OFFSET_RADIO = 0.5f;
    private View childView;
    private RecyclerView recyclerView;
    private Rect originalRect = new Rect();
    private boolean isMoved = false;
    private float startY;
    private int scrollY;
    private int offset;
    private boolean isInit;
    private boolean pullEnable = true;
    private PullListener pullListener;

    public PullRecyclerLayout(Context context) {
        this(context, null);
    }

    public PullRecyclerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRecyclerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPullListener(PullListener pullListener) {
        this.pullListener = pullListener;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    protected void onFinishInflate() {
        initChildView();
        super.onFinishInflate();
    }

    private void initChildView() {
        if (getChildCount() == 1) {
            childView = getChildAt(0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (childView == null) {
            return;
        }
        if (!isInit) {
            isInit = true;
            originalRect.set(childView.getLeft(), childView.getTop(), childView.getRight(), childView.getBottom());
        }
        if (offset != 0 && isMoved) {
            if (pullListener != null) {
                pullListener.onPull(offset);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (childView == null || !pullEnable) {
            return super.onInterceptTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
            case MotionEvent.ACTION_MOVE:
                float nowY = ev.getY();
                scrollY = (int) (nowY - startY);
                if (scrollY > 0) {
                    return isCanPullDown();
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (childView == null || !pullEnable) {
            return super.onTouchEvent(ev);
        }

        boolean isTouchOutOfScrollView = ev.getY() >= originalRect.bottom || ev.getY() <= originalRect.top;
        if (isTouchOutOfScrollView) {
            if (isMoved) {
                recoverLayout();
            }
            return true;
        }

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                float nowY = ev.getY();
                scrollY = (int) (nowY - startY);
                if (isCanPullDown() && scrollY > 0) {
                    setOffset((int) (scrollY * OFFSET_RADIO));
                    isMoved = true;
                    if (pullListener != null) {
                        pullListener.onPull(offset);
                    }
                } else {
                    if (pullListener != null) {
                        pullListener.onPull(0);
                    }
                    isMoved = false;
                    setOffset(0);
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isMoved) {
                    recoverLayout();
                }
                return super.onTouchEvent(ev);
            default:
                return true;
        }
    }


    public void setPullEnable(boolean pullEnable) {
        this.pullEnable = pullEnable;
    }

    private void recoverLayout() {
        if (!isMoved) {
            return;
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(offset, 0);
        valueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            setOffset(value);
        });
        valueAnimator.setDuration(ANIM_TIME).setInterpolator(new DecelerateInterpolator());
        valueAnimator.start();
        isMoved = false;
        if (pullListener != null) {
            pullListener.onRecover();
        }
    }

    private boolean isCanPullDown() {
        if (recyclerView.getVisibility() != View.VISIBLE) {
            return true;
        }
        final RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (null == adapter) {
            return true;
        }
        final int firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        if (firstVisiblePosition != 0 && adapter.getItemCount() != 0) {
            return false;
        }
        int mostTop = (recyclerView.getChildCount() > 0) ? recyclerView.getChildAt(0).getTop() : 0;
        return mostTop >= 0;
    }

    private void setOffset(int offset) {
        this.offset = offset;
        MarginLayoutParams layoutParams1 = (MarginLayoutParams) childView.getLayoutParams();
        layoutParams1.topMargin = offset;
        childView.setLayoutParams(layoutParams1);
    }

    public interface PullListener {

        void onRecover();

        void onPull(int offset);
    }
}


