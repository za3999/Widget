package widget.cf.com.widgetlibrary.emoji;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

public class EmojiAppCompatEditText extends AppCompatEditText implements IEmojiObserve {

    OnKeyListenerAgent onKeyListenerAgent = new OnKeyListenerAgent();

    public EmojiAppCompatEditText(Context context) {
        super(context);
        init();
    }

    public EmojiAppCompatEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmojiAppCompatEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setOnKeyListener(OnKeyListener l) {
        onKeyListenerAgent.setOnKeyListener(l);
        super.setOnKeyListener(onKeyListenerAgent);
    }

    private void init() {
        TransformationMethod tm = getTransformationMethod();
        if (tm != null && !(tm instanceof PasswordTransformationMethod)) {
            setTransformationMethod(EmojiHelper.wrapTransformationMethod(tm));
        }
        setOnKeyListener(onKeyListenerAgent);
    }

    @Override
    public void setFilters(InputFilter[] filters) {
        super.setFilters(EmojiHelper.getFilters(filters, this));
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
    public void onEmojiLoaded() {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        setText(getText());
        setSelection(selectionStart, selectionEnd);
    }

    private class OnKeyListenerAgent implements OnKeyListener {

        OnKeyListener onKeyListener;

        public void setOnKeyListener(OnKeyListener onKeyListener) {
            this.onKeyListener = onKeyListener;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (onKeyListener != null && onKeyListener.onKey(v, keyCode, event)) {
                return true;
            }
            if (delEmoji(keyCode, event)) {
                return true;
            }
            return false;
        }
    }

    private boolean delEmoji(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
            int beforeSelectionEnd = getSelectionEnd();
            if (getSelectionStart() == beforeSelectionEnd) {
                int lengthWhenEmojiEnd = EmojiHelper.getLengthWhenEmojiEnd(getText(), beforeSelectionEnd);
                if (lengthWhenEmojiEnd != -1) {
                    SpannableStringBuilder result = new SpannableStringBuilder();
                    int afterSelectionEnd = beforeSelectionEnd - lengthWhenEmojiEnd;
                    if (afterSelectionEnd <= 0) {
                        afterSelectionEnd = 0;
                    }
                    Editable editable = getText();
                    if (afterSelectionEnd > 0) {
                        result.append(editable.subSequence(0, afterSelectionEnd));
                    }
                    if (beforeSelectionEnd < editable.length()) {
                        result.append(editable.subSequence(beforeSelectionEnd, editable.length()));
                    }
                    setText(result);
                    setSelection(afterSelectionEnd);
                    return true;
                }
            }
        }
        return false;
    }
}
