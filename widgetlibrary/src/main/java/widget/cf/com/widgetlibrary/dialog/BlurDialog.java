package widget.cf.com.widgetlibrary.dialog;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;
import widget.cf.com.widgetlibrary.util.BitmapUtil;


public class BlurDialog extends BaseAlertDialog {
    private Drawable mDrawable;
    protected View mTargetView;
    protected RelativeLayout mRootLayout;
    protected boolean isCancelAble;

    public BlurDialog(View targetView) {
        super(targetView.getContext(), R.style.ThemeTranslucent);
        this.mTargetView = targetView;
        mRootLayout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(mRootLayout, layoutParams);
        Bitmap blurBitmap = BitmapUtil.blurWallpaper(BitmapUtil.getBitmap(targetView.getRootView()), 20);
        blurBitmap = BitmapUtil.getCoverBitmap(blurBitmap, ContextCompat.getColor(getContext(), R.color.color_20_transparent));
        mDrawable = new BitmapDrawable(ApplicationUtil.getResources(), blurBitmap);
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
        isCancelAble = flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mRootLayout);
        getWindow().setBackgroundDrawable(mDrawable);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (isCancelAble) {
            mRootLayout.setOnClickListener(v -> dismiss());
        }
    }

    protected void startAnim(View view, int pivotX, int pivotY) {
        view.setPivotX(pivotX);
        view.setPivotY(pivotY);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.8f, 1.0f);
        animator.addUpdateListener(animation -> {
            if ((float) animation.getAnimatedValue() > 0.81 && view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }
        });
        animatorSet.playTogether(animator, ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.8f, 1.0f));
        animatorSet.setDuration(200).start();
    }
}
