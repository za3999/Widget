package widget.cf.com.widgetlibrary;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;


import widget.cf.com.widgetlibrary.base.BaseCallBack;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class PullRecyclerLayout extends FrameLayout {

    private static final long ANIM_TIME = 300;
    private static final int CHANGE_OFFSET = ApplicationUtil.getIntDimension(R.dimen.dp_50);
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
    private BaseCallBack.CallBack recoverListener;

    public PullRecyclerLayout(Context context) {
        this(context, null);
    }

    public PullRecyclerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRecyclerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        initChildView();
        super.onFinishInflate();
    }

    private void initChildView() {
        if (getChildCount() == 1) {
            View view = getChildAt(0);
//            recyclerView = view.findViewById(R.id.recycler_view);
            if (recyclerView != null) {
                childView = view;
            }
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
        childView.layout(getLeft(), getTop() + offset, getRight(), getBottom() + offset);
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
                if (scrollY > 10) {
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
                    offset = (int) (scrollY * OFFSET_RADIO);
                    childView.layout(originalRect.left, originalRect.top + offset, originalRect.right, originalRect.bottom + offset);
                    isMoved = true;
                } else {
                    isMoved = false;
                    childView.layout(originalRect.left, originalRect.top, originalRect.right, originalRect.bottom);
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

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setRecoverListener(BaseCallBack.CallBack recoverListener) {
        this.recoverListener = recoverListener;
    }

    public void setPullEnable(boolean pullEnable) {
        this.pullEnable = pullEnable;
    }

    private void recoverLayout() {
        if (!isMoved) {
            return;
        }
        TranslateAnimation anim = new TranslateAnimation(0, 0, childView.getTop() - originalRect.top, 0);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(ANIM_TIME);
        childView.startAnimation(anim);
        childView.layout(originalRect.left, originalRect.top, originalRect.right, originalRect.bottom);
        isMoved = false;
        if (recoverListener != null && offset > CHANGE_OFFSET) {
            recoverListener.onCallBack();
        }
        offset = 0;
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
}


