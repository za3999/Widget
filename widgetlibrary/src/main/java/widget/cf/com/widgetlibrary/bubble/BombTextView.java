package widget.cf.com.widgetlibrary.bubble;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import widget.cf.com.widgetlibrary.base.BaseCallBack;
import widget.cf.com.widgetlibrary.util.BitmapUtil;

public class BombTextView extends androidx.appcompat.widget.AppCompatTextView {

    private int bombColor;
    private int bombDrawableIndex = 0;
    private Bitmap bombBitmap;
    private Paint mPaint;
    private boolean isBombing;
    private Rect bombRect;


    public BombTextView(Context context) {
        this(context, null);
    }

    public BombTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public BombTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
    }

    public void setBombColor(int bombColor) {
        this.bombColor = bombColor;
    }

    @Override
    public void draw(Canvas canvas) {
        if (isBombing) {
            drawBomb(canvas);
        } else {
            super.draw(canvas);
        }
    }

    private void drawBomb(Canvas canvas) {
        bombBitmap = BitmapUtil.changeColor(DragBubbleHelper.getBombBitmaps()[bombDrawableIndex], bombColor);
        int bubbleRadius = Math.min(getWidth(), getHeight());
        bombRect = new Rect((getWidth() - bubbleRadius) / 2, 0, (getWidth() + bubbleRadius) / 2, getHeight());
        canvas.drawBitmap(bombBitmap, null, bombRect, mPaint);
    }

    public void startBomb(BaseCallBack.CallBack callBack) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, DragBubbleHelper.getBombBitmaps().length);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            bombDrawableIndex = (int) animation.getAnimatedValue();
            invalidate();
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isBombing = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isBombing = false;
                invalidate();
                BaseCallBack.onCallBack(callBack);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isBombing = false;
                invalidate();
                BaseCallBack.onCallBack(callBack);
            }

        });
        valueAnimator.start();
    }
}
