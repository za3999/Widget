package widget.cf.com.widgetlibrary.touchmenu;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Build;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class TouchWidget extends FrameLayout {

    private IMenu mMenuView;
    private Activity mActivity;
    private boolean isShowing = false;
    private boolean isTouchModel;

    public TouchWidget(Activity activity) {
        super(activity);
        this.mActivity = activity;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setMenuView(int x, int y, IMenu menuView) {
        mMenuView = menuView;
        mMenuView.setCloseListener(() -> hide());
        removeAllViews();
        mMenuView.getRoot().setX(x);
        mMenuView.getRoot().setY(y);
        addView(mMenuView.getRoot());
    }

    public void show(boolean isTouchModel) {
        ApplicationUtil.runOnMainThread(() -> {
            this.isTouchModel = isTouchModel;
            attachActivity(mActivity);
            if (isTouchModel) {
                transferTouchEvent(mActivity);
            }
            animMenu();
            isShowing = true;
        });
    }

    public void hide() {
        if (isShowing) {
            disAttachActivity(mActivity);
            isShowing = false;
        }
    }

    private void animMenu() {
        if (mMenuView == null) {
            return;
        }
        mMenuView.getRoot().setPivotX(0f);
        mMenuView.getRoot().setPivotY(0f);
        mMenuView.getRoot().setScaleX(0f);
        mMenuView.getRoot().setScaleY(0f);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1f, 1.2f, 1f);
        valueAnimator.setDuration(200);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            mMenuView.getRoot().setScaleX(value);
            mMenuView.getRoot().setScaleY(value);
        });
        valueAnimator.start();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isTouchModel;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isTouchModel) {
            if (!TouchUtils.isInView(mMenuView.getRoot(), event.getX(), event.getY())) {
                hide();
            }
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (TouchUtils.isInView(mMenuView.getRoot(), event.getX(), event.getY())) {
                        mMenuView.onTouchMove(event.getX(), event.getY());
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mMenuView.onTouchUp();
                    hide();
                    break;
            }
        }
        return true;
    }

    private void attachActivity(Activity activity) {
        ViewParent parent = getParent();
        if (parent != null && parent instanceof ViewGroup) {
            ViewGroup parentView = (ViewGroup) parent;
            parentView.removeView(this);
        }
        FrameLayout decor = (FrameLayout) activity.getWindow().getDecorView();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        decor.addView(this, lp);
    }

    private void disAttachActivity(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!isAttachedToWindow()) {
                return;
            }
        }
        try {
            FrameLayout decor = (FrameLayout) activity.getWindow().getDecorView();
            decor.removeView(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void transferTouchEvent(final Activity activity) {
        postDelayed(() -> {
            long uptimeMillis = SystemClock.uptimeMillis();
            activity.dispatchTouchEvent(MotionEvent.obtain(uptimeMillis, uptimeMillis,
                    MotionEvent.ACTION_UP, 0f, 0f, 0));
            activity.getWindow().getDecorView().dispatchTouchEvent(MotionEvent.obtain(uptimeMillis, uptimeMillis,
                    MotionEvent.ACTION_DOWN, 0f, 0f, 0));
        }, 200);
    }

}
