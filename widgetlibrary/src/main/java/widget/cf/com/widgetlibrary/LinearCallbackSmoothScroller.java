package widget.cf.com.widgetlibrary;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import widget.cf.com.widgetlibrary.base.BaseCallBack;


public class LinearCallbackSmoothScroller extends LinearSmoothScroller {

    private boolean isScrolling;

    private BaseCallBack.CallBack mScrollCallback;

    public LinearCallbackSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
        super.onTargetFound(targetView, state, action);
        isScrolling = false;
        if (mScrollCallback != null) {
            mScrollCallback.onCallBack();
        }
    }

    public void startScroll(int position, LinearLayoutManager layoutManager, BaseCallBack.CallBack scrollCallback) {
        isScrolling = true;
        this.mScrollCallback = scrollCallback;
        setTargetPosition(position);
        layoutManager.startSmoothScroll(this);
    }

    public boolean isScrolling() {
        return isScrolling;
    }
}
