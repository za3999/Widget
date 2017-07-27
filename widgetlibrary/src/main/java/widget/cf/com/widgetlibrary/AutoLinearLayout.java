package widget.cf.com.widgetlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;


/**
 * 宽高按比例自动缩放的ImageView
 */
public class AutoLinearLayout extends LinearLayout {

    private float yScale, xScale;

    public AutoLinearLayout(Context context) {
        super(context);
    }

    public AutoLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        readTypeArray(context, attrs);
    }

    public AutoLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        readTypeArray(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        MeasureSpec.getMode(widthMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        if (yScale != 0f) {
            h = (int) (w * yScale);
        } else if (xScale != 0f) {
            w = (int) (h * xScale);
        }
        setMeasuredDimension(w, h);
        measureChildren(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec
                .makeMeasureSpec(h, MeasureSpec.EXACTLY));
    }

    private void readTypeArray(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoLinearLayout);
        yScale = a.getFloat(R.styleable.AutoLinearLayout_y_scale, 0f);
        xScale = a.getFloat(R.styleable.AutoLinearLayout_x_scale, 0f);
        a.recycle();
    }

}
