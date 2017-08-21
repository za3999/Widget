package widget.cf.com.widgetlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 自动换行的Layout
 */
public class AutoLayout extends ViewGroup {
    int horizontalSpacing = 0;
    int verticalSpacing = 0;
    int paddingTop = 0;
    int maxLines = 50;

    public AutoLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        readStyleParameters(context, attrs);
    }

    public AutoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        readStyleParameters(context, attrs);
    }

    public AutoLayout(Context context) {
        super(context);
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    private void readStyleParameters(Context context, AttributeSet attributeSet) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.AutoLayout);
        try {
            horizontalSpacing = a.getDimensionPixelSize(R.styleable.AutoLayout_horizontalSpacing, 0);
            verticalSpacing = a.getDimensionPixelSize(R.styleable.AutoLayout_verticalSpacing, 0);
            paddingTop = a.getDimensionPixelSize(R.styleable.AutoLayout_paddingTop, 0);
            maxLines = a.getInt(R.styleable.AutoLayout_maxLines, maxLines);
        } finally {
            a.recycle();
        }
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
    }

    public void setVerticalSpacing(int verticalSpacing) {
        this.verticalSpacing = verticalSpacing;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        int width = 0;
        int lengthX = 0;
        int rowNumber = 1;
        for (int index = 0; index < getChildCount(); index++) {
            final View child = getChildAt(index);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            height = child.getMeasuredHeight();
            width = child.getMeasuredWidth();
            lengthX = lengthX + width;
            if (lengthX > sizeWidth) {
                lengthX = width;
                if (rowNumber < maxLines) {
                    rowNumber++;
                }
            } else {
                lengthX += horizontalSpacing;
            }
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                paddingTop + rowNumber * (height + verticalSpacing));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int row = 0;
        int lengthX = 0;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = this.getChildAt(i);
            if (row != maxLines) {
                child.setVisibility(View.VISIBLE);
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                lengthX += width;
                if (lengthX > getWidth()) {
                    lengthX = width;
                    row++;
                }
                int lengthY = row * (height + verticalSpacing) + height + paddingTop;
                child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
                lengthX += horizontalSpacing;
            } else {
                child.setVisibility(View.GONE);
            }
        }

    }

}
