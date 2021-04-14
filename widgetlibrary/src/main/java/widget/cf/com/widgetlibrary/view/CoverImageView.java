package widget.cf.com.widgetlibrary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class CoverImageView extends AppCompatImageView {

    private Drawable mCoverDrawable;
    private int mSize;
    private Path mPath = new Path();
    private Paint mPaint = new Paint();

    public CoverImageView(Context context) {
        this(context, null);
    }

    public CoverImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoverImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCover(@DrawableRes int drawableRes, int size) {
        setCover(ContextCompat.getDrawable(getContext(), drawableRes), size);
    }

    public void setCover(Drawable drawable, int size) {
        mCoverDrawable = drawable;
        this.mSize = size;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int radius = ApplicationUtil.getIntDimension(R.dimen.dp_2);
        mPath.addRoundRect(new RectF(0, 0, w, h), radius, radius, Path.Direction.CW);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.white_final));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.clipPath(mPath);
        canvas.drawPath(mPath, mPaint);
        super.onDraw(canvas);
        canvas.restore();
        if (mCoverDrawable != null) {
            mCoverDrawable.setBounds(0, 0, mSize, mSize);
            canvas.save();
            canvas.translate((getWidth() - mSize) / 2, (getHeight() - mSize) / 2);
            mCoverDrawable.draw(canvas);
            canvas.restore();
        }
    }

}
