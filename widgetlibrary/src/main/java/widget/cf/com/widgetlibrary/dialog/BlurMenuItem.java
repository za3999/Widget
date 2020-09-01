package widget.cf.com.widgetlibrary.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;


public class BlurMenuItem extends FrameLayout {

    private TextView mName;

    private ImageView mImageView;

    private int height = ApplicationUtil.getIntDimension(R.dimen.dp_42);

    public BlurMenuItem(Context context) {
        this(context, null);
    }

    public BlurMenuItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlurMenuItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BlurMenuItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(getContext()).inflate(R.layout.blur_menu_layout, this);
        mName = findViewById(R.id.name_tv);
        mImageView = findViewById(R.id.image_iv);
    }

    public BlurMenuItem bindData(int nameRes, int imageRes, OnClickListener onClickListener) {
        mName.setText(nameRes);
        mImageView.setImageResource(imageRes);
        setOnClickListener(onClickListener);
        return this;
    }

    public BlurMenuItem bindData(int nameRes, int imageRes, int color, OnClickListener onClickListener) {
        bindData(nameRes, imageRes, onClickListener);
        mName.setTextColor(color);
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }
}
