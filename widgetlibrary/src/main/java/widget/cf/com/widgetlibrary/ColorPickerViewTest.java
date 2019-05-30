package widget.cf.com.widgetlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerViewTest extends View {

    private static final float PI = (float) Math.PI;

    private Paint paintCircle;
    private Paint paintGray;
    private Paint paintLightShadow;
    private Paint paintPoint;
    private int[] arrColorGray;
    private final int[] arrColorCircle = new int[]{0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000};

    private boolean mRedrawHSV;
    private OnColorBackListener l;
    float density;
    private int currentColor;
    private String strColor = "";

    private float leftViewArea = 0.8f;
    private int leftViewMargin;
    int leftViewWidth;
    private int centerX;
    private int centerY;
    int radius;
    Pair<Float, Float> leftPoint;

    private int rightRectWidth;
    int rightViewLeft;
    private float rightPointY;

    public ColorPickerViewTest(Context context) {
        super(context);
        init();
    }

    public ColorPickerViewTest(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorPickerViewTest(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        currentColor = Color.parseColor("#FFFFFF");
        density = getContext().getResources().getDisplayMetrics().density;
        arrColorGray = new int[]{0xFFFFFFFF, currentColor, 0xFF000000};
        paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCircle.setShader(new SweepGradient(0, 0, arrColorCircle, null));
        paintCircle.setStyle(Paint.Style.FILL);

        paintGray = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLightShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLightShadow.setColor(Color.BLACK);
        paintLightShadow.setStyle(Paint.Style.STROKE);
        paintLightShadow.setStrokeWidth(dp(1));

        paintPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPoint.setStyle(Paint.Style.FILL);
        paintPoint.setColor(Color.BLACK);
        mRedrawHSV = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(centerX, centerY);
        strColor = "#" + Integer.toHexString(currentColor).substring(2).toUpperCase();
        canvas.drawOval(new RectF(-radius, -radius, radius, radius), paintCircle);
        if (leftPoint != null) {
            canvas.drawCircle(leftPoint.first, leftPoint.second, dp(4), paintPoint);
        }
        canvas.restore();

        canvas.save();
        canvas.translate(rightViewLeft, 0);
        if (mRedrawHSV) {
            arrColorGray[1] = currentColor;
            paintGray.setShader(new LinearGradient(0, 0, rightRectWidth, (float) getHeight(), arrColorGray, null, Shader.TileMode.CLAMP));
        }
        canvas.drawRect(new RectF(0, 0, rightRectWidth, getHeight()), paintLightShadow);
        canvas.drawRect(new RectF(1, 1, rightRectWidth - 1, getHeight() - 1), paintGray);
        canvas.drawRect(new RectF(0, rightPointY - dp(1), rightRectWidth, rightPointY + dp(1)), paintLightShadow);
        canvas.restore();
        mRedrawHSV = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int w = MeasureSpec.getSize(widthMeasureSpec);
        leftViewWidth = (int) (w * leftViewArea);
        leftViewMargin = dp(15);
        radius = (leftViewWidth - leftViewMargin * 2) / 2;
        int height = radius * 2;
        centerX = leftViewMargin + radius;
        centerY = radius;
        rightRectWidth = dp(15);
        super.onMeasure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        int rightAreaWidth = getMeasuredWidth() - leftViewWidth;
        int rightOffset = (rightAreaWidth - rightRectWidth) / 2;
        rightViewLeft = getMeasuredWidth() - rightOffset;
        rightPointY = getMeasuredHeight() / 2;
    }

    public String getStrColor() {
        return strColor;
    }

    private int dp(float value) {
        return (int) Math.ceil(density * value);
    }

    private int ave(int s, int d, float p) {
        return s + Math.round(p * (d - s));
    }

    private int interpColor(int colors[], float unit) {

        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {
            return colors[colors.length - 1];
        }

        float p = unit * (colors.length - 1);
        int i = (int) p;
        p -= i;

        int c0 = colors[i];
        int c1 = colors[i + 1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);
        if (l != null) {
            l.onColorBack(a, r, g, b);
        }
        return Color.argb(a, r, g, b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                if (isTouchCircle(x, y)) {
                    float circleX = x - centerX;
                    float circleY = y - centerY;
                    float angle = (float) Math.atan2(circleY, circleX);
                    float unit = angle / (2 * PI);
                    if (unit < 0)
                        unit += 1;
                    currentColor = interpColor(arrColorCircle, unit);
                    leftPoint = new Pair<>(circleX, circleY);
                    invalidate();
                } else if (isTouchRightView(x)) {
                    rightPointY = y;
                    int a, r, g, b, c0, c1;
                    float p;
                    int center = getHeight() / 2;
                    if (rightPointY < center) {
                        c0 = arrColorGray[0];
                        c1 = arrColorGray[1];
                        p = rightPointY / center;
                    } else {
                        c0 = arrColorGray[1];
                        c1 = arrColorGray[2];
                        p = (rightPointY - center) / center;
                    }
                    a = ave(Color.alpha(c0), Color.alpha(c1), p);
                    r = ave(Color.red(c0), Color.red(c1), p);
                    g = ave(Color.green(c0), Color.green(c1), p);
                    b = ave(Color.blue(c0), Color.blue(c1), p);
                    currentColor = Color.argb(a, r, g, b);
                    mRedrawHSV = false;
                    if (l != null) {
                        l.onColorBack(a, r, g, b);
                    }
                    invalidate();
                }
                break;
            }
        }
        return true;
    }

    private boolean isTouchCircle(float x, float y) {
        int distance = (int) Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        boolean touchCircle = distance < radius;
        return touchCircle;
    }

    private boolean isTouchRightView(float x) {
        return x > rightViewLeft && x < rightViewLeft + rightRectWidth;
    }

    public void setOnColorBackListener(OnColorBackListener l) {
        this.l = l;
    }

    public interface OnColorBackListener {
        void onColorBack(int a, int r, int g, int b);
    }
}
