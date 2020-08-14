package widget.cf.com.widgetlibrary.tintview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class TintBgImageView extends AppCompatImageView implements TintColorChangeListener {

    public TintBgImageView(Context context) {
        super(context);
    }

    public TintBgImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TintBgImageView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        if (this.getBackground() != null) {
            this.getBackground().setTint(color);
        }
    }

}
