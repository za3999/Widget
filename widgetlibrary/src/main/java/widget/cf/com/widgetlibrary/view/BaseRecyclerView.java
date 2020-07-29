package widget.cf.com.widgetlibrary.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

import widget.cf.com.widgetlibrary.adapter.BaseCommonAdapter;


public class BaseRecyclerView extends RecyclerView {

    private BaseCommonAdapter mCommonAdapter;

    public BaseRecyclerView(@androidx.annotation.NonNull Context context) {
        super(context);
    }

    public BaseRecyclerView(@androidx.annotation.NonNull Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRecyclerView(@androidx.annotation.NonNull Context context, @androidx.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCommonAdapter != null) {
            mCommonAdapter.detachedAllFromWindow();
        }
    }

    @Override
    public void setAdapter(@androidx.annotation.Nullable Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof BaseCommonAdapter) {
            mCommonAdapter = (BaseCommonAdapter) adapter;
        } else {
            mCommonAdapter = null;
        }
    }
}
