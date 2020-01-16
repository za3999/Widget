package widget.cf.com.widgetlibrary.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class AutoCloseSoftRecyclerView extends RecyclerView {

    boolean isSoftOpen = true;

    public AutoCloseSoftRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }

    public AutoCloseSoftRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoCloseSoftRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            final Rect r = new Rect();
            getWindowVisibleDisplayFrame(r);
            final int heightDiff = getRootView().getHeight() - (r.bottom - r.top);
            isSoftOpen = heightDiff > 300;
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isSoftOpen) {
            InputMethodManager inputMethodManager = (InputMethodManager) ApplicationUtil.getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getWindowToken(), InputMethodManager.SHOW_FORCED);
            isSoftOpen = false;
        }
        return super.dispatchTouchEvent(ev);
    }
}
