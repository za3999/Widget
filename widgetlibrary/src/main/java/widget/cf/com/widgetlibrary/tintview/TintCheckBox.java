package widget.cf.com.widgetlibrary.tintview;


import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckBox;

import widget.cf.com.widgetlibrary.util.ViewUtil;


public class TintCheckBox extends AppCompatCheckBox implements TintColorChangeListener {

    public TintCheckBox(Context context) {
        super(context);
    }

    public TintCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TintCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        onRefreshColor(TintColorManager.getColor());
    }

    @Override
    public void onRefreshColor(int color) {
        ViewUtil.setCheckBoxTint(this, isChecked() ? color : ViewUtil.TINT_NON);
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
