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
    private int baseColor;
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
        currentColor = baseColor = Color.parseColor("#FFFFFF");
        density = getContext().getResources().getDisplayMetrics().density;
        arrColorGray = new int[]{0xFFFFFFFF, baseColor, 0xFF000000};
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
        canvas.drawOval(new RectF(-radius, -radius, radius, radius), paintCircle);
        if (leftPoint != null) {
            canvas.drawCircle(leftPoint.first, leftPoint.second, dp(4), paintPoint);
        }
        canvas.restore();

        canvas.save();
        canvas.translate(rightViewLeft, 0);
        if (mRedrawHSV) {
            arrColorGray[1] = baseColor;
            paintGray.setShader(new LinearGradient(0, 0, rightRectWidth, (float) getHeight(), arrColorGray, null, Shader.TileMode.CLAMP));
        }
        canvas.drawRect(new RectF(0, 0, rightRectWidth, getHeight()), paintLightShadow);
        canvas.drawRect(new RectF(1, 1, rightRectWidth - 1, getHeight() - 1), paintGray);
        canvas.drawRoundRect(new RectF(dp(-3), rightPointY - dp(3), rightRectWidth + dp(3), rightPointY + dp(3)), 0f, 0f, paintPoint);
        canvas.restore();
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
        rightViewLeft = getMeasuredWidth() - rightRectWidth - dp(40);
        rightPointY = getMeasuredHeight() / 2;
    }

    public String getStrColor() {
        return "#" + Integer.toHexString(currentColor).substring(2).toUpperCase();
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
                    mRedrawHSV = true;
                    float circleX = x - centerX;
                    float circleY = y - centerY;
                    leftPoint = new Pair<>(circleX, circleY);
                    float angle = (float) Math.atan2(circleY, circleX);
                    float unit = angle / (2 * PI);
                    if (unit < 0) {
                        unit += 1;
                    }
                    baseColor = interpColor(arrColorCircle, unit);
                    setCurrentColor();
                } else if (isTouchRightView(x, y)) {
                    mRedrawHSV = false;
                    rightPointY = y;
                    setCurrentColor();
                }
                break;
            }
        }
        return true;
    }

    private ColorProperty setCurrentColor() {
        int c0, c1;
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
        ColorProperty colorProperty = new ColorProperty();
        colorProperty.a = ave(Color.alpha(c0), Color.alpha(c1), p);
        colorProperty.r = ave(Color.red(c0), Color.red(c1), p);
        colorProperty.g = ave(Color.green(c0), Color.green(c1), p);
        colorProperty.b = ave(Color.blue(c0), Color.blue(c1), p);
        currentColor = Color.argb(colorProperty.a, colorProperty.r, colorProperty.g, colorProperty.b);
        if (l != null) {
            l.onColorBack(colorProperty.a, colorProperty.r, colorProperty.g, colorProperty.b);
        }
        invalidate();
        return colorProperty;
    }

    private boolean isTouchCircle(float x, float y) {
        int distance = (int) Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        boolean touchCircle = distance < radius;
        return touchCircle;
    }

    private boolean isTouchRightView(float x, float y) {
        return x > rightViewLeft && x < rightViewLeft + rightRectWidth && y > 0 && y < getHeight();
    }

    public void setOnColorBackListener(OnColorBackListener l) {
        this.l = l;
    }

    public interface OnColorBackListener {
        void onColorBack(int a, int r, int g, int b);
    }

    private class ColorProperty {
        int a, r, g, b;
    }
}
