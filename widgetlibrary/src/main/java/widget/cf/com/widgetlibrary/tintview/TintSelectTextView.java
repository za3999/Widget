package widget.cf.com.widgetlibrary.tintview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import widget.cf.com.widgetlibrary.emoji.EmojiAppCompatTextView;


public class TintSelectTextView extends EmojiAppCompatTextView implements TintColorChangeListener {

    private ColorStateList originalTextColor;

    public TintSelectTextView(Context context) {
        this(context, null);
    }

    public TintSelectTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TintSelectTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        originalTextColor = getTextColors();
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        onRefreshColor(TintColorManager.getColor());
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
    public void setTextColor(int color) {
        super.setTextColor(color);
        originalTextColor = getTextColors();
    }

    @Override
    public void onRefreshColor(int color) {
        if (isSelected()) {
            super.setTextColor(color);
        } else {
            super.setTextColor(originalTextColor);
        }
    }

}
