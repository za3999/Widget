package widget.cf.com.widgetlibrary.touchmenu;

import android.app.Activity;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class TouchWidget extends FrameLayout {


    private ITouchPopMenu mMenuView;
    private Activity mActivity;
    private boolean isShowing = false;
    private boolean isTouchModel;
    private ViewGroup parent;
    private FrameLayout.LayoutParams lp;

    public TouchWidget(Activity activity) {
        super(activity);
        this.mActivity = activity;
        parent = (ViewGroup) activity.getWindow().getDecorView();
        lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setMenuView(int x, int y, ITouchPopMenu menuView) {
        mMenuView = menuView;
        mMenuView.setCloseHelper(() -> hide());
        removeAllViews();
        mMenuView.getRoot().setX(x);
        mMenuView.getRoot().setY(y);
        addView(mMenuView.getRoot());
    }

    public void show(boolean isTouchModel) {
        isShowing = true;
        ApplicationUtil.runOnMainThread(() -> {
            this.isTouchModel = isTouchModel;
            attachActivity();
            if (isTouchModel) {
                transferTouchEvent(mActivity);
            }
            animMenu();
        });
    }

    public boolean hide() {
        if (isShowing) {
            disAttachActivity();
            isShowing = false;
            return true;
        }
        return false;
    }

    private void animMenu() {
        if (mMenuView == null) {
            return;
        }
        mMenuView.getRoot().setPivotX(mMenuView.getMenuWith() / 2);
        mMenuView.getRoot().setPivotY(0f);
        mMenuView.getRoot().setScaleY(0f);
        mMenuView.getRoot().animate().scaleY(1)
                .setInterpolator(new LinearInterpolator())
                .setDuration(200).start();
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

    private void attachActivity() {
        if (isAttachedToWindow()) {
            parent.removeView(this);
        }
        parent.addView(this, lp);
    }

    private void disAttachActivity() {
        if (!isAttachedToWindow()) {
            return;
        }
        try {
            parent.removeView(this);
        } catch (Exception e) {
            //do nothing
        }
    }

    private void transferTouchEvent(final Activity activity) {
        postDelayed(() -> {
            long uptimeMillis = SystemClock.uptimeMillis();
            activity.dispatchTouchEvent(MotionEvent.obtain(uptimeMillis, uptimeMillis, MotionEvent.ACTION_UP, 0f, 0f, 0));
            activity.getWindow().getDecorView().dispatchTouchEvent(MotionEvent.obtain(uptimeMillis, uptimeMillis, MotionEvent.ACTION_DOWN, 0f, 0f, 0));
        }, 200);
    }
}
