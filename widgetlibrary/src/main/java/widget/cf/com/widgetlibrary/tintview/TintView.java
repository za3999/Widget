package widget.cf.com.widgetlibrary.tintview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import widget.cf.com.widgetlibrary.util.ViewUtil;


public class TintView extends View implements TintColorChangeListener {

    public TintView(Context context) {
        this(context, null);
    }

    public TintView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    @Override
    public void onRefreshColor(int color) {
        ViewUtil.setViewBackgroundTint(this, color);
    }
}
