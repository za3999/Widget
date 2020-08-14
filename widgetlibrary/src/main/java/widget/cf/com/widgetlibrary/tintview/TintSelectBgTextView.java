package widget.cf.com.widgetlibrary.tintview;

import android.content.Context;
import android.util.AttributeSet;

import widget.cf.com.widgetlibrary.emoji.EmojiAppCompatTextView;
import widget.cf.com.widgetlibrary.util.ViewUtil;


public class TintSelectBgTextView extends EmojiAppCompatTextView implements TintColorChangeListener {

    public TintSelectBgTextView(Context context) {
        this(context, null);
    }

    public TintSelectBgTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TintSelectBgTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
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
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        onRefreshColor(TintColorManager.getColor());
    }

    @Override
    public void onRefreshColor(int color) {
        ViewUtil.setViewBackgroundTint(this, isSelected() ? color : ViewUtil.TINT_NON);
    }
}
