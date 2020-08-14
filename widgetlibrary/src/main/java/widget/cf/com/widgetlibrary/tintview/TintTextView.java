package widget.cf.com.widgetlibrary.tintview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.emoji.EmojiAppCompatTextView;
import widget.cf.com.widgetlibrary.util.ViewUtil;


public class TintTextView extends EmojiAppCompatTextView implements TintColorChangeListener {

    private boolean backgroundTint;
    private boolean textTint;
    private boolean drawableLeftTint;

    public TintTextView(Context context) {
        this(context, null);
    }

    public TintTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TintTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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

    public void setBackgroundTint() {
        backgroundTint = true;
        onRefreshColor(TintColorManager.getColor());
    }

    public void setTextTint(boolean textTint) {
        this.textTint = textTint;
        onRefreshColor(TintColorManager.getColor());
    }

    public void init(AttributeSet attrs) {
        super.init(attrs);
        TypedArray array = null;
        try {
            array = getContext().obtainStyledAttributes(attrs, R.styleable.TintTextView);
            if (array != null) {
                backgroundTint = array.getBoolean(R.styleable.TintTextView_background_tint, false);
                textTint = array.getBoolean(R.styleable.TintTextView_text_tint, false);
                drawableLeftTint = array.getBoolean(R.styleable.TintTextView_drawableLeft_tint, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (array != null) {
                array.recycle();
            }
        }
    }

    @Override
    public void onRefreshColor(int color) {
        if (backgroundTint) {
            ViewUtil.setViewBackgroundTint(this, color);
        }
        if (textTint) {
            setTextColor(color);
        }

        if (drawableLeftTint) {
            Drawable[] drawables = getCompoundDrawables();
            if (drawables != null && drawables.length == 4) {
                Drawable drawableLeft = drawables[0];
                if (drawableLeft != null) {
                    drawableLeft.setTint(color);
                    setCompoundDrawablesRelativeWithIntrinsicBounds(drawableLeft, drawables[1], drawables[2], drawables[3]);
                }
            }
        }
    }
}
