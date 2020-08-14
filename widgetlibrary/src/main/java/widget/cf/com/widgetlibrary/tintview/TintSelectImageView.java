package widget.cf.com.widgetlibrary.tintview;


import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import widget.cf.com.widgetlibrary.util.ViewUtil;

public class TintSelectImageView extends AppCompatImageView implements TintColorChangeListener {

    public TintSelectImageView(Context context) {
        super(context);
    }

    public TintSelectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TintSelectImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        onRefreshColor(TintColorManager.getColor());
    }

    @Override
    public void onRefreshColor(int color) {
        ViewUtil.setImageTint(this, isSelected() ? color : ViewUtil.TINT_NON);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        TintColorManager.addRefreshColorThemeListener(this, true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        TintColorManager.removeRefreshColorThemeListener(this);
    }
}
