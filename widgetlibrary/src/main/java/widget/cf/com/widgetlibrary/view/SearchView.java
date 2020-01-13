package widget.cf.com.widgetlibrary.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SearchView extends View {
    // 这个视图拥有的状态
    @IntDef({State.NONE, State.STARTING, State.SEARCHING, State.ENDING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
        int NONE = 0;
        int STARTING = 1;
        int SEARCHING = 2;
        int ENDING = 3;
    }

    // 画笔
    private Paint mPaint;
    // View 宽高
    private int mViewWidth;
    private int mViewHeight;

    // 当前的状态(非常重要)
    private int mCurrentState = State.NONE;

    // 放大镜与外部圆环
    private Path pathSearch;
    private Path pathCircle;

    // 测量Path 并截取部分的工具
    private PathMeasure mMeasure;

    // 默认的动效周期 2s
    private int defaultDuration = 2000;

    // 控制各个过程的动画
    private ValueAnimator mStartingAnimator;
    private ValueAnimator mSearchingAnimator;
    private ValueAnimator mEndingAnimator;

    // 动画数值(用于控制动画状态,因为同一时间内只允许有一种状态出现,具体数值处理取决于当前状态)
    private float mAnimatorValue = 0;

    // 动效过程监听器
    private ValueAnimator.AnimatorUpdateListener mUpdateListener = animation -> {
        mAnimatorValue = (float) animation.getAnimatedValue();
        invalidate();
    };
    private Animator.AnimatorListener mAnimatorListener = new AnimatorListenerAdapter() {

        @Override
        public void onAnimationEnd(Animator animation) {
            goToNextStatus();
        }
    };

    // 判断是否已经搜索结束
    private boolean isOver = false;

    private int count = 0;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAll();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSearch(canvas);
    }

    public void initAll() {
        initPaint();
        initPath();
        initAnimator();
        // 进入开始动画
        mCurrentState = State.STARTING;
        mStartingAnimator.start();
        setOnClickListener(v -> {
            mCurrentState = State.STARTING;
            mStartingAnimator.start();
        });
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(15);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
    }

    private void initPath() {
        pathSearch = new Path();
        pathCircle = new Path();

        mMeasure = new PathMeasure();
        // 注意,不要到360度,否则内部会自动优化,测量不能取到需要的数值
        RectF oval1 = new RectF(-50, -50, 50, 50);          // 放大镜圆环
        pathSearch.addArc(oval1, 45, 359.9f);
        RectF oval2 = new RectF(-100, -100, 100, 100);      // 外部圆环
        pathCircle.addArc(oval2, 45, -359.9f);
        float[] pos = new float[2];
        mMeasure.setPath(pathCircle, false);               // 放大镜把手的位置
        mMeasure.getPosTan(0, pos, null);
        pathSearch.lineTo(pos[0], pos[1]);                 // 放大镜把手
    }
    private void initAnimator() {
        mStartingAnimator = ValueAnimator.ofFloat(0, 1).setDuration(defaultDuration);
        mSearchingAnimator = ValueAnimator.ofFloat(0, 1).setDuration(defaultDuration);
        mEndingAnimator = ValueAnimator.ofFloat(1, 0).setDuration(defaultDuration);

        mStartingAnimator.addUpdateListener(mUpdateListener);
        mSearchingAnimator.addUpdateListener(mUpdateListener);
        mEndingAnimator.addUpdateListener(mUpdateListener);

        mStartingAnimator.addListener(mAnimatorListener);
        mSearchingAnimator.addListener(mAnimatorListener);
        mEndingAnimator.addListener(mAnimatorListener);
    }
    // 用于控制动画状态转换

    private void goToNextStatus() {
        switch (mCurrentState) {
            case State.STARTING:
                // 从开始动画转换好搜索动画
                isOver = false;
                mCurrentState = State.SEARCHING;
                mSearchingAnimator.start();
                break;
            case State.SEARCHING:
                if (!isOver) {  // 如果搜索未结束 则继续执行搜索动画
                    mSearchingAnimator.start();
                    count++;
                    if (count > 2) {       // count大于2则进入结束状态
                        isOver = true;
                    }
                } else {        // 如果搜索已经结束 则进入结束动画
                    mCurrentState = State.ENDING;
                    mEndingAnimator.start();
                }
                break;
            case State.ENDING:
                // 从结束动画转变为无状态
                mCurrentState = State.NONE;
                break;
        }
    }

    private void drawSearch(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        canvas.drawColor(Color.parseColor("#0082D7"));
        switch (mCurrentState) {
            case State.NONE:
                canvas.drawPath(pathSearch, mPaint);
                break;
            case State.STARTING:
                mMeasure.setPath(pathSearch, false);
                Path dst = new Path();
                mMeasure.getSegment(mMeasure.getLength() * mAnimatorValue, mMeasure.getLength(), dst, true);
                canvas.drawPath(dst, mPaint);
                break;
            case State.SEARCHING:
                mMeasure.setPath(pathCircle, false);
                Path dst2 = new Path();
                float stop = mMeasure.getLength() * mAnimatorValue;
                float start = (float) (stop - ((0.5 - Math.abs(mAnimatorValue - 0.5)) * 200f));
                mMeasure.getSegment(start, stop, dst2, true);
                canvas.drawPath(dst2, mPaint);
                break;
            case State.ENDING:
                mMeasure.setPath(pathSearch, false);
                Path dst3 = new Path();
                mMeasure.getSegment(mMeasure.getLength() * mAnimatorValue, mMeasure.getLength(), dst3, true);
                canvas.drawPath(dst3, mPaint);
                break;
        }
    }
}