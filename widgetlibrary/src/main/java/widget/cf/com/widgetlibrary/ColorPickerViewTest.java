package widget.cf.com.widgetlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import widget.cf.com.widgetlibrary.util.SPUtil;

public class ColorPickerViewTest extends View {

    public static final String KEY_CIRCLE_POINT = "circle_point";
    public static final String KEY_DEEPNESS = "deepness";

    private static final float PI = (float) Math.PI;

    private Paint paintCircle;
    private Paint paintCircleShadow;
    private Paint paintGray;
    private Paint paintLightShadow;
    private Paint paintPoint;
    private int[] arrColorGray;
    private final int[] arrColorCircle = new int[]{0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000};

    private boolean circleUpdate;
    private OnColorBackListener colorChangeListener;
    float density;
    private ColorProperty currentColor;
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
    private float deepness = 0.5f;

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

    public ColorProperty getCurrentColor() {
        return currentColor;
    }

    public void setOnColorBackListener(OnColorBackListener l) {
        this.colorChangeListener = l;
    }

    private void init() {
        baseColor = Color.parseColor("#FFFFFF");
        density = getContext().getResources().getDisplayMetrics().density;
        arrColorGray = new int[]{0xFFFFFFFF, baseColor, 0xFF000000};
        paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCircle.setShader(new SweepGradient(0, 0, arrColorCircle, null));
        paintCircle.setStyle(Paint.Style.FILL);

        paintCircleShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCircle.setStyle(Paint.Style.FILL);

        paintGray = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLightShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLightShadow.setColor(Color.BLACK);
        paintLightShadow.setStyle(Paint.Style.STROKE);
        paintLightShadow.setStrokeWidth(dp(1));

        paintPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPoint.setStyle(Paint.Style.FILL);
        paintPoint.setColor(Color.BLACK);
        circleUpdate = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(centerX, centerY);
        canvas.drawOval(new RectF(-radius, -radius, radius, radius), paintCircle);
//        canvas.drawOval(new RectF(-radius, -radius, radius, radius), paintCircleShadow);
        if (leftPoint != null) {
            canvas.drawCircle(leftPoint.first, leftPoint.second, dp(4), paintPoint);
        }
        canvas.restore();

        canvas.save();
        canvas.translate(rightViewLeft, 0);
        if (circleUpdate) {
            paintGray.setShader(new LinearGradient(0, 0, rightRectWidth, (float) getHeight(), arrColorGray, null, Shader.TileMode.CLAMP));
        }
        canvas.drawRect(new RectF(0, 0, rightRectWidth, getHeight()), paintLightShadow);
        canvas.drawRect(new RectF(1, 1, rightRectWidth - 1, getHeight() - 1), paintGray);
        float rightPointY = getHeight() * deepness;
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
        super.onMeasure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        paintCircleShadow.setShader(new RadialGradient(0, 0, radius, 0xffffffff, 0x00ffffff, Shader.TileMode.CLAMP));
        centerX = leftViewMargin + radius;
        centerY = radius;
        rightRectWidth = dp(15);
        rightViewLeft = getMeasuredWidth() - rightRectWidth - dp(40);
        deepness = SPUtil.get(getContext(), KEY_DEEPNESS, 0.5F, Float.class);
        String pointStr = SPUtil.get(getContext(), KEY_CIRCLE_POINT, "0f:0f", String.class);
        String[] point = pointStr.split(":");
        onCircleUpdate(Float.parseFloat(point[0]), Float.parseFloat(point[1]));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                int distance = getTouchCircleDistance(x, y);
                if (distance < radius) {
                    float circleX = x - centerX;
                    float circleY = y - centerY;
                    onCircleUpdate(circleX, circleY);
                } else if (isTouchRightView(x, y)) {
                    circleUpdate = false;
                    deepness = y / getHeight();
                    SPUtil.put(getContext(), KEY_DEEPNESS, deepness);
                    setCurrentColor();
                }
                break;
            }
        }
        return true;
    }

    private void onCircleUpdate(float circleX, float circleY) {
        circleUpdate = true;
        leftPoint = new Pair<>(circleX, circleY);
        float angle = (float) Math.atan2(circleY, circleX);
        float unit = angle / (2 * PI);
        if (unit < 0) {
            unit += 1;
        }
        baseColor = interpColor(arrColorCircle, unit);
        arrColorGray[1] = baseColor;
        SPUtil.put(getContext(), KEY_CIRCLE_POINT, circleX + ":" + circleY);
        setCurrentColor();
    }

    private ColorProperty setCurrentColor() {
        int c0, c1;
        float p;
        int center = getMeasuredHeight() / 2;
        float rightPointY = getMeasuredHeight() * deepness;
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
        currentColor = colorProperty;
        if (colorChangeListener != null) {
            colorChangeListener.onColorBack(colorProperty);
        }
        invalidate();
        return colorProperty;
    }

    private int getTouchCircleDistance(float x, float y) {
        return (int) Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
    }

    private boolean isTouchRightView(float x, float y) {
        return x > rightViewLeft && x < rightViewLeft + rightRectWidth && y > 0 && y < getHeight();
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

    public interface OnColorBackListener {
        void onColorBack(ColorProperty colorProperty);
    }

    public static class ColorProperty {
        public int a, r, g, b;

        public int getColor() {
            return Color.argb(a, r, g, b);
        }

        public String getColorStr() {
            return Integer.toHexString(getColor()).substring(2).toUpperCase();
        }
    }
}
