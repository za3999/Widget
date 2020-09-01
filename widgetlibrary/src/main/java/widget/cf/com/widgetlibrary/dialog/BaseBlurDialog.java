package widget.cf.com.widgetlibrary.dialog;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;
import widget.cf.com.widgetlibrary.util.BitmapUtil;


public abstract class BaseBlurDialog extends BlurDialog {

    private int targetLeftMargin;
    protected Rect mTargetRect = new Rect();
    protected View mMirrorView;
    protected View mContentView;

    public BaseBlurDialog(View targetView) {
        super(targetView);
    }

    public abstract View getContentView();

    public abstract RelativeLayout.LayoutParams getLayoutParams();

    public abstract RelativeLayout.LayoutParams getTargetLayoutParams();

    public abstract Pair<Integer, Integer> getAnimPoint();

    public int getMenuWidth() {
        return ApplicationUtil.getIntDimension(R.dimen.dp_250);
    }

    public int getTargetViewPadding() {
        return ApplicationUtil.getIntDimension(R.dimen.dp_8);
    }

    public int getTargetBgResource() {
        return R.drawable.white_radius_bg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fillTargetRect();
        mMirrorView = getMirrorView(BitmapUtil.getBitmap(mTargetView));
        mRootLayout.addView(mMirrorView, getTargetLayoutParams());
        mContentView = getContentView();
        mRootLayout.addView(mContentView, getLayoutParams());
        mRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                Pair<Integer, Integer> point = getAnimPoint();
                startAnim(mContentView, point.first, point.second);
            }
        });
    }

    private void fillTargetRect() {
        Rect tempRect = new Rect();
        int targetViewPadding = getTargetViewPadding();
        mTargetView.getGlobalVisibleRect(tempRect);
        mTargetRect.left = tempRect.left - targetViewPadding;
        mTargetRect.top = tempRect.top - targetViewPadding;
        mTargetRect.right = tempRect.left + mTargetView.getWidth() + targetViewPadding;
        mTargetRect.bottom = tempRect.top + mTargetView.getHeight() + targetViewPadding;
    }

    private ImageView getMirrorView(Bitmap mirrorBitmap) {
        ImageView mirrorView = new ImageView(getContext());
        int targetViewPadding = getTargetViewPadding();
        mirrorView.setPadding(targetViewPadding, targetViewPadding, targetViewPadding, targetViewPadding);
        mirrorView.setBackgroundResource(getTargetBgResource());
        mirrorView.setId(R.id.target_id);
        mirrorView.setImageBitmap(mirrorBitmap);
        return mirrorView;
    }

    protected int getTargetLeftMargin() {
        if (mTargetRect.left < 0) {
            targetLeftMargin = 0;
        } else {
            int rootWidth = mTargetView.getRootView().getWidth();
            int maxWidth = rootWidth - mTargetRect.left;
            if (maxWidth < mTargetRect.width()) {
                targetLeftMargin = rootWidth - mTargetRect.width();
            } else {
                targetLeftMargin = mTargetRect.left;
            }
        }
        return targetLeftMargin;
    }

    protected int getMenuLeftMargin() {
        int popLeft = targetLeftMargin - (getMenuWidth() - mTargetRect.width()) / 2;
        if (popLeft < 0) {
            return 0;
        } else {
            int rootWidth = mTargetView.getRootView().getWidth();
            int maxWidth = rootWidth - popLeft;
            if (maxWidth < getMenuWidth()) {
                return rootWidth - getMenuWidth();
            } else {
                return popLeft;
            }
        }
    }
}
