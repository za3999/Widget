package widget.cf.com.widgetlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
 * 宽高按比例自动缩放的ImageView
 */
public class AutoImageView extends android.support.v7.widget.AppCompatImageView {

    private float yScale, xScale;

    public AutoImageView(Context context) {
        super(context);
    }

    public AutoImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        readTypeArray(context, attrs);
    }

    public AutoImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        readTypeArray(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int w = MeasureSpec.getSize(widthMeasureSpec);
        final int h = MeasureSpec.getSize(heightMeasureSpec);
        if (yScale != 0f) {
            setMeasuredDimension(w, (int) (w * yScale));
        } else if (xScale != 0f) {
            setMeasuredDimension((int) (h * xScale), h);
        } else {
            setMeasuredDimension(w, h);
        }
    }

    private void readTypeArray(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoImageView);
        yScale = a.getFloat(R.styleable.AutoImageView_yScale, 0f);
        xScale = a.getFloat(R.styleable.AutoImageView_xScale, 0f);
        a.recycle();
    }

}
