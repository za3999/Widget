package widget.cf.com.widgetlibrary.animator;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.LinearInterpolator;


public class WaveHelper {

    View view;
    private RectF centerRect = new RectF();
    private Drawable currentDrawable;
    private int width;
    private int height;
    private int mWaveLength;
    private int mWaveLevel;
    private int mWaveAmplitude;
    private float mProgress;
    Paint mWavePaint;
    Path mPath = new Path();
    private int offset = 0;
    private int mWaveCount;
    private int mRealWaveLevel;
    boolean mIsPause;
    ValueAnimator mWaveAnimator;
    ValueAnimator mLevelAnimator;

    public WaveHelper(View view) {
        this.view = view;
        mWavePaint = new Paint();
        mWavePaint.setAntiAlias(true);
        mWavePaint.setStyle(Paint.Style.FILL);
        mWavePaint.setColor(Color.RED);
    }

    public void setProgressRect(int left, int top, int right, int bottom) {
        centerRect.set(left, top, right, bottom);
    }

    public void setWaveColor(int mWaveColor) {
        mWavePaint.setColor(mWaveColor);
    }

    public void draw(Canvas canvas) {
        drawWave(canvas);
        if (currentDrawable != null) {
            currentDrawable.setBounds((int) centerRect.left, (int) centerRect.top, (int) centerRect.right, (int) centerRect.bottom);
            currentDrawable.draw(canvas);
        }
    }

    public void setCenterDrawable(Drawable drawable) {
        currentDrawable = drawable;
        view.invalidate();
    }

    public void setProgress(float value) {
        if (value < 0 || value > 1) {
            return;
        }
        mProgress = value;
        checkAnimator();
    }

    private void checkAnimator() {
        if (mProgress > 0 && width > 0 && height > 0) {
            int currentLevel = (int) (height * mProgress);
            if (currentLevel != mRealWaveLevel) {
                mRealWaveLevel = currentLevel;
                if (mWaveLevel != mRealWaveLevel) {
                    startLevelAnimator();
                }
                startWaveAnimator();
            }
        }
    }

    public void onSizeChanged() {
        width = view.getWidth();
        mWaveLength = width;
        height = view.getHeight();
        mWaveAmplitude = height / 20;
        mWaveCount = (int) Math.round(width / mWaveLength + 1.5);
        checkAnimator();
    }

    public void setPause(boolean isPause) {
        mIsPause = isPause;
        if (mWaveAnimator != null) {
            if (isPause) {
                mWaveAnimator.pause();
            } else {
                mWaveAnimator.resume();
            }
        }
    }

    private void startLevelAnimator() {
        if (mLevelAnimator != null) {
            mLevelAnimator.cancel();
        }
        mLevelAnimator = ValueAnimator.ofInt(mWaveLevel, mRealWaveLevel);
        mLevelAnimator.setDuration(500);
        mLevelAnimator.setInterpolator(new LinearInterpolator());
        mLevelAnimator.addUpdateListener(animation -> {
            mWaveLevel = (Integer) animation.getAnimatedValue();
            view.invalidate();
        });
        mLevelAnimator.start();
    }

    private void startWaveAnimator() {
        if (mWaveAnimator == null) {
            mWaveAnimator = ValueAnimator.ofInt(0, mWaveLength);
            mWaveAnimator.setDuration(1000);
            mWaveAnimator.setInterpolator(new LinearInterpolator());
            mWaveAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mWaveAnimator.addUpdateListener(animation -> {
                offset = (int) animation.getAnimatedValue();
                view.invalidate();
            });
            mWaveAnimator.start();
        }
    }

    private void drawWave(Canvas canvas) {
        if (mProgress < 0 || mProgress >= 1) {
            return;
        }
        mPath.reset();
        int heightOffset = height - mWaveLevel;
        if (mIsPause) {
            mPath.moveTo(0, heightOffset);
            mPath.lineTo(width, heightOffset);
        } else {
            mPath.moveTo(-mWaveLength, heightOffset);
            for (int i = 0; i < mWaveCount; i++) {
                mPath.quadTo(-mWaveLength * 3 / 4 + i * mWaveLength + offset, heightOffset - mWaveAmplitude, -mWaveLength / 2 + i * mWaveLength + offset, heightOffset);
                mPath.quadTo(-mWaveLength / 4 + i * mWaveLength + offset, heightOffset + mWaveAmplitude, i * mWaveLength + offset, heightOffset);
            }
        }
        mPath.lineTo(width, height);
        mPath.lineTo(0, height);
        mPath.close();
        Path path = new Path();
        path.addCircle(width / 2, height / 2, width / 2, Path.Direction.CW);
        mPath.op(path, Path.Op.INTERSECT);
        canvas.drawPath(mPath, mWavePaint);
    }
}
