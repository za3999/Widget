package widget.cf.com.widgetlibrary.emoji;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;
import widget.cf.com.widgetlibrary.util.ViewUtil;

public class EmojiAppCompatTextView extends AppCompatTextView implements IEmojiObserve {

    private boolean ignoreCutText;
    private CharSequence realText;
    private boolean isExactly;

    public EmojiAppCompatTextView(Context context) {
        super(context);
    }

    public EmojiAppCompatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmojiAppCompatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        TransformationMethod tm = getTransformationMethod();
        if (tm != null && !(tm instanceof PasswordTransformationMethod)) {
            setTransformationMethod(EmojiHelper.wrapTransformationMethod(tm));
        }
    }

    public void init(AttributeSet attrs) {
        TypedArray array = null;
        try {
            array = getContext().obtainStyledAttributes(attrs, R.styleable.EmojiAppCompatTextView);
            if (array != null) {
                boolean isBold = array.getBoolean(R.styleable.EmojiAppCompatTextView_support_bold, false);
                if (isBold) {
                    ViewUtil.setBold(this);
                }
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
    public void setFilters(InputFilter[] filters) {
        super.setFilters(EmojiHelper.getFilters(filters, this));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        isExactly = false;
        if (layoutParams != null) {
            if (ViewGroup.LayoutParams.MATCH_PARENT == layoutParams.width) {
                isExactly = MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY;
            } else if (ViewGroup.LayoutParams.WRAP_CONTENT != layoutParams.width) {
                isExactly = true;
            }
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        realText = text;
        if (!EmojiHelper.isUseSysEmoji() && getEllipsize() != null && !TextUtils.isEmpty(text)) {
            setIgnoreCutText(false);
            text = EmojiHelper.replaceEmoji(TextUtils.ellipsize(text, getPaint(), ApplicationUtil.getScreenWidth() * getMaxLines(), getEllipsize()), getPaint());
            if (isExactly) {
                text = cutText(text);
            }
        }
        super.setText(text, type);
    }

    public CharSequence getRealText() {
        return realText;
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
    public void onEmojiLoaded() {
        postInvalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        EmojiHelper.register(this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        EmojiHelper.unRegister(this);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!ignoreCutText) {
            checkText(cutText(getText()));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!ignoreCutText) {
            checkText(cutText(getText()));
        }
    }

    protected CharSequence cutText(CharSequence text) {
        float maxLines = getMaxLines();
        int textWidth = getMeasuredWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
        float avail = maxLines > 1 ? maxLines * textWidth - getTextSize() : maxLines * textWidth;
        return TextUtils.ellipsize(text, getPaint(), avail > 0 ? avail : 0, getEllipsize());
    }

    private void checkText(CharSequence text) {
        ignoreCutText = true;
        if (getText() instanceof Spanned && getText().length() != text.length()) {
            if (EmojiHelper.isExistEmojiSpan((Spanned) getText(), text.length() - EmojiHelper.getMaxEmojiLength(), text.length() + EmojiHelper.getMaxEmojiLength())) {
                super.setText(text);
            }
        }
    }

    private void setIgnoreCutText(boolean ignoreCutText) {
        if (isExactly) {
            this.ignoreCutText = true;
        } else {
            this.ignoreCutText = ignoreCutText;
        }
    }
}
