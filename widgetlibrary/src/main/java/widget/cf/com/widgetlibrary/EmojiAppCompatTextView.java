package widget.cf.com.widgetlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import widget.cf.com.widgetlibrary.emoji.EmojiHelper;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class EmojiAppCompatTextView extends AppCompatTextView {

    private boolean needCheckTextLength;
    private Integer originalMinWidth;
    private Integer originalMaxWidth;

    public EmojiAppCompatTextView(Context context) {
        super(context);
    }

    public EmojiAppCompatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmojiAppCompatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TransformationMethod tm = getTransformationMethod();
        if (tm != null && !(tm instanceof PasswordTransformationMethod)) {
            setTransformationMethod(EmojiHelper.wrapTransformationMethod(tm));
        }
    }

    @Override
    public void setFilters(InputFilter[] filters) {
        super.setFilters(EmojiHelper.getFilters(filters, this));
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!EmojiHelper.isUseSysEmoji() && getEllipsize() != null && !TextUtils.isEmpty(text)) {
            if (!needCheckTextLength) {
                resetLineWidth();
            }
            needCheckTextLength = true;
            text = TextUtils.ellipsize(text, getPaint(), ApplicationUtil.getScreenWidth() * getMaxLines(), getEllipsize());
        }
        super.setText(text, type);
    }

    public void setTextIsSelectable(boolean selectable) {
        if (getText() == null ||
                getText().length() <= 0) {
            super.setTextIsSelectable(selectable);
            return;
        }
        setClickable(selectable);
        setLongClickable(selectable);
        setMovementMethod(selectable ? ArrowKeyMovementMethod.getInstance() : null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needCheckTextLength) {
            int maxLines = getMaxLines();
            int lineWidth = getWidth();
            int textWidth = getTextWidth(lineWidth);
            CharSequence text = TextUtils.ellipsize(getText(), getPaint(), maxLines * textWidth, getEllipsize());
            if (getText() instanceof Spanned && getText().length() != text.length()) {
                if (EmojiHelper.isExistEmojiSpan((Spanned) getText(), text.length() - 6, text.length() + 6)) {
                    if (maxLines > 1) {
                        text = TextUtils.ellipsize(getText(), getPaint(), maxLines * textWidth - getTextSize(), getEllipsize());
                    }
                    setLineWidth(lineWidth);
                    setText(text);
                    needCheckTextLength = false;
                }
            }
        }
        super.onDraw(canvas);
    }

    private int getTextWidth(int lineWidth) {
        Drawable[] compoundDrawables = getCompoundDrawables();
        if (compoundDrawables[0] != null) {
            lineWidth = lineWidth - compoundDrawables[0].getIntrinsicWidth();
        }
        if (compoundDrawables[2] != null) {
            lineWidth = lineWidth - compoundDrawables[2].getIntrinsicWidth();
        }
        return lineWidth - getPaddingLeft() - getPaddingRight();
    }

    @Override
    public void setWidth(int pixels) {
        super.setWidth(pixels);
        originalMinWidth = pixels;
        originalMaxWidth = pixels;
    }


    @Override
    public void setMinWidth(int minPixels) {
        super.setMinWidth(minPixels);
        originalMinWidth = minPixels;
    }

    @Override
    public void setMaxWidth(int maxPixels) {
        super.setMaxWidth(maxPixels);
        originalMaxWidth = maxPixels;
    }

    private void setLineWidth(int lineWidth) {
        originalMinWidth = originalMinWidth == null ? getMinWidth() : originalMinWidth;
        originalMaxWidth = originalMaxWidth == null ? getMaxWidth() : originalMaxWidth;
        super.setMinWidth(lineWidth);
        super.setMaxWidth(lineWidth);
    }

    private void resetLineWidth() {
        if (originalMinWidth != null) {
            setMinWidth(originalMinWidth);
        }
        if (originalMaxWidth != null) {
            setMaxWidth(originalMaxWidth);
        }
    }
}
