package widget.cf.com.widgetlibrary.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BaseDialog extends Dialog {

    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        DialogUtil.setOnCancelListener(this, listener);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        DialogUtil.setOnDismissListener(this, listener);
    }

    @Override
    public void setOnShowListener(@Nullable OnShowListener listener) {
        DialogUtil.setOnShowListener(this, listener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (getWindow() != null) {
            getWindow().setDimAmount(0.3f);
        }
    }
}
