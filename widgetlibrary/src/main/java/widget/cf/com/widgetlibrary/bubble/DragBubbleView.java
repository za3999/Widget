package widget.cf.com.widgetlibrary.bubble;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PointFEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.base.BaseCallBack;
import widget.cf.com.widgetlibrary.util.BitmapUtil;
import widget.cf.com.widgetlibrary.util.LogUtils;


public class DragBubbleView extends View {

    private final int BUBBLE_STATE_STATIC = 0;
    private final int BUBBLE_STATE_DISMISS = 1;
    private final int BUBBLE_STATE_CONNECTION = 2;
    private final int BUBBLE_STATE_APART = 3;

    private int mBubbleState = BUBBLE_STATE_STATIC;
    private Path bezierPath;
    private Paint bezierPaint;

    private Paint bPaint;
    private Paint textPaint;
    private Paint bombPaint;
    private int bubbleColor;

    private Rect textRect;
    private Rect bombRect;

    private float bubbleRadius = 20;
    private float bubbleStillRadius;
    private String bubbleText;

    private PointF stillBubbleCenter;
    private PointF moveBubbleCenter;

    private float dist;
    private float maxDist;
    private float moveOffSize;
    private BaseCallBack.CallBack1<Boolean> onResultListener;
    private boolean startDrag;

    private int[] BOOM_ARRAY = {R.drawable.burst_1, R.drawable.burst_2
            , R.drawable.burst_3, R.drawable.burst_4, R.drawable.burst_5,
            R.drawable.burst_6, R.drawable.burst_7, R.drawable.burst_8,
            R.drawable.burst_9, R.drawable.burst_10, R.drawable.burst_11,
            R.drawable.burst_12, R.drawable.burst_13};
    private Bitmap[] bomb_bitmaps;
    private int bombDrawableIndex = 0;
    private boolean isBombAnimStarting = false;
    private View dragView;
    private Bitmap viewBitmap;
    private boolean useCircle = false;

    public DragBubbleView(Context context) {
        this(context, null);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DragBubbleView, defStyleAttr, defStyleRes);
        try {
            bubbleText = typedArray.getString(R.styleable.DragBubbleView_bubble_text);
            setRadius(typedArray.getDimension(R.styleable.DragBubbleView_bubble_radius, bubbleRadius));
            bubbleColor = typedArray.getColor(R.styleable.DragBubbleView_bubble_color, Color.RED);
            setBubbleColor(bubbleColor);

            float bubbleTextSize = typedArray.getDimension(R.styleable.DragBubbleView_bubble_textSize, 12);
            int bubbleTextColor = typedArray.getColor(R.styleable.DragBubbleView_bubble_textColor, Color.WHITE);
            setTextPaint(bubbleTextColor, bubbleTextSize);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            typedArray.recycle();
        }

        bombPaint = new Paint();
        bombPaint.setAntiAlias(true);
        bombPaint.setFilterBitmap(true);
        bombRect = new Rect();
        bomb_bitmaps = new Bitmap[BOOM_ARRAY.length];
        for (int i = 0; i < BOOM_ARRAY.length; i++) {
            bomb_bitmaps[i] = BitmapFactory.decodeResource(getResources(), BOOM_ARRAY[i]);
        }
    }

    private void setTextPaint(int textColor, float textSize) {
        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textRect = new Rect();
    }

    private void setBubbleColor(int bubbleColor) {
        bezierPath = new Path();
        bezierPaint = new Paint();
        bezierPaint.setAntiAlias(true);
        bezierPaint.setStyle(Paint.Style.FILL);
        bezierPaint.setColor(bubbleColor);

        bPaint = new Paint();
        bPaint.setStyle(Paint.Style.FILL);
        bPaint.setAntiAlias(true);
        bPaint.setColor(bubbleColor);
    }

    public void initBubble(View view, int yOffset, int bubbleColor, BaseCallBack.CallBack1<Boolean> onResultListener) {
        this.onResultListener = onResultListener;
        this.dragView = view;
        int location[] = new int[2];
        view.getLocationInWindow(location);
        int x = location[0] + view.getWidth() / 2;
        int y = location[1] - yOffset + view.getHeight() / 2;
        stillBubbleCenter = new PointF(x, y);
        moveBubbleCenter = new PointF(x, y);
        setRadius(Math.min(view.getWidth(), view.getHeight()) / 2);

        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            setTextPaint(textView.getCurrentTextColor(), textView.getTextSize());
            bubbleText = textView.getText().toString();
        } else {
            useCircle = false;
        }
        this.bubbleColor = bubbleColor;
        setBubbleColor(bubbleColor);
        viewBitmap = BitmapUtil.getBitmap(dragView);
        setBubbleState(BUBBLE_STATE_STATIC);
        startDrag = true;
        invalidate();
    }

    public void updateLocation(View view, int yOffset) {
        int location[] = new int[2];
        view.getLocationInWindow(location);
        int x = location[0] + view.getWidth() / 2;
        int y = location[1] - yOffset + view.getHeight() / 2;
        stillBubbleCenter = new PointF(x, y);
        updateDist();
        invalidate();
    }

    private void setRadius(float radius) {
        bubbleRadius = radius;
        maxDist = bubbleRadius * 10;
        moveOffSize = maxDist / 5;
        bubbleStillRadius = bubbleRadius / 2;
    }

    private void setBubbleState(int state) {
        mBubbleState = state;
    }

    public void touchDown(MotionEvent event) {
        dist = (float) Math.hypot(event.getX() - stillBubbleCenter.x, event.getY() - stillBubbleCenter.y);
        if (dist <= bubbleRadius + moveOffSize) {
            setBubbleState(BUBBLE_STATE_CONNECTION);
        } else {
            setBubbleState(BUBBLE_STATE_STATIC);
        }
        invalidate();
    }

    public boolean touchUpdate(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (mBubbleState != BUBBLE_STATE_STATIC) {
                moveBubbleCenter.x = event.getX();
                moveBubbleCenter.y = event.getY();
                updateDist();
            }
            invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            endBubble(false);
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!startDrag) {
            return;
        }
        LogUtils.d("drag", "onDraw:" + stillBubbleCenter.x + "," + stillBubbleCenter.y + "|" + moveBubbleCenter.x + "," + moveBubbleCenter.y);
        if (mBubbleState == BUBBLE_STATE_CONNECTION) {
            canvas.drawCircle(stillBubbleCenter.x, stillBubbleCenter.y, bubbleStillRadius, bPaint);
            int anchorx = (int) (stillBubbleCenter.x + moveBubbleCenter.x) / 2;
            int anchorY = (int) (stillBubbleCenter.y + moveBubbleCenter.y) / 2;

            float sinTheta = (moveBubbleCenter.y - stillBubbleCenter.y) / dist;
            float cosTheta = (moveBubbleCenter.x - stillBubbleCenter.x) / dist;

            float aX = stillBubbleCenter.x - sinTheta * bubbleStillRadius;
            float aY = stillBubbleCenter.y + cosTheta * bubbleStillRadius;
            float bX = stillBubbleCenter.x + sinTheta * bubbleStillRadius;
            float bY = stillBubbleCenter.y - cosTheta * bubbleStillRadius;

            float cX = moveBubbleCenter.x - sinTheta * bubbleRadius;
            float cY = moveBubbleCenter.y + cosTheta * bubbleRadius;
            float dX = moveBubbleCenter.x + sinTheta * bubbleRadius;
            float dY = moveBubbleCenter.y - cosTheta * bubbleRadius;

            bezierPath.reset();
            bezierPath.moveTo(aX, aY);
            bezierPath.quadTo(anchorx, anchorY, cX, cY);
            bezierPath.lineTo(dX, dY);
            bezierPath.quadTo(anchorx, anchorY, bX, bY);
            bezierPath.close();
            canvas.drawPath(bezierPath, bezierPaint);
        }

        if (mBubbleState != BUBBLE_STATE_DISMISS) {
            if (useCircle) {
                canvas.drawCircle(moveBubbleCenter.x, moveBubbleCenter.y, bubbleRadius, bPaint);
                textPaint.getTextBounds(bubbleText, 0, bubbleText.length(), textRect);
                canvas.drawText(bubbleText, moveBubbleCenter.x - textRect.width() / 2, moveBubbleCenter.y + textRect.height() / 2, textPaint);
            } else {
                canvas.drawBitmap(viewBitmap, moveBubbleCenter.x - dragView.getWidth() / 2, moveBubbleCenter.y - dragView.getHeight() / 2, bPaint);
            }
        }

        if (isBombAnimStarting) {
            int bomOffset = Math.max(dragView.getWidth() / 2, dragView.getHeight() / 2);
            bombRect.set((int) (moveBubbleCenter.x - bomOffset), (int) (moveBubbleCenter.y - bomOffset),
                    (int) (moveBubbleCenter.x + bomOffset), (int) (moveBubbleCenter.y + bomOffset));
            canvas.drawBitmap(BitmapUtil.changeColor(bomb_bitmaps[bombDrawableIndex], bubbleColor), null, bombRect, bombPaint);
        }
    }

    public void endBubble(boolean forceStop) {
        if (forceStop) {
            startDrag = false;
            invalidate();
            BaseCallBack.onCallBack(onResultListener, true);
        } else {
            if (mBubbleState == BUBBLE_STATE_CONNECTION || dist <= bubbleRadius + moveOffSize) {
                bubbleRestAnim();
            } else if (mBubbleState == BUBBLE_STATE_APART) {
                bubbleBombAnim();
            }
        }
    }

    private void bubbleBombAnim() {
        setBubbleState(BUBBLE_STATE_DISMISS);
        isBombAnimStarting = true;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, BOOM_ARRAY.length);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            bombDrawableIndex = (int) animation.getAnimatedValue();
            invalidate();

        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                bomb();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                bomb();
            }

            private void bomb() {
                isBombAnimStarting = false;
                startDrag = false;
                BaseCallBack.onCallBack(onResultListener, false);
            }

        });
        valueAnimator.start();
    }


    private void bubbleRestAnim() {
        ValueAnimator vAnimator = ValueAnimator.ofObject(new PointFEvaluator(), new PointF(moveBubbleCenter.x, moveBubbleCenter.y), new PointF(stillBubbleCenter.x, stillBubbleCenter.y));
        vAnimator.setDuration(200);
        vAnimator.setInterpolator(new OvershootInterpolator(5f));
        vAnimator.addUpdateListener(animation -> {
            moveBubbleCenter = (PointF) animation.getAnimatedValue();
            updateDist();
            invalidate();
        });
        vAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                reset();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                reset();
            }

            private void reset() {
                setBubbleState(BUBBLE_STATE_STATIC);
                startDrag = false;
                BaseCallBack.onCallBack(onResultListener, true);
            }
        });
        vAnimator.start();
    }

    private void updateDist() {
        dist = (float) Math.hypot(moveBubbleCenter.x - stillBubbleCenter.x, moveBubbleCenter.y - stillBubbleCenter.y);
        bubbleStillRadius = bubbleRadius - dist / 12;
        if (dist < maxDist - moveOffSize) {
            setBubbleState(BUBBLE_STATE_CONNECTION);
        } else {
            setBubbleState(BUBBLE_STATE_APART);
        }
    }

}
