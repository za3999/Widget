package widget.cf.com.widgetlibrary.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class IndicatorLayoutManager extends LinearLayoutManager {

    public IndicatorLayoutManager(Context context) {
        super(context);
    }

    public IndicatorLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public IndicatorLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (onLayoutChildrenBySelf(recycler)) {
            return;
        }
        super.onLayoutChildren(recycler, state);
    }

    private boolean onLayoutChildrenBySelf(RecyclerView.Recycler recycler) {
        int mIdleWidth = getWidth();
        int count = getItemCount();
        if (count == 0) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            View view = recycler.getViewForPosition(i);
            if (view == null) {
                continue;
            }
            measureChildWithMargins(view, 0, 0);
            mIdleWidth = mIdleWidth - getDecoratedMeasuredWidth(view);
            if (mIdleWidth <= 0) {
                return false;
            }
        }
        detachAndScrapAttachedViews(recycler);
        int childPadding = mIdleWidth / count;
        int left = 0;
        int right;
        for (int i = 0; i < count; i++) {
            View view = recycler.getViewForPosition(i);
            if (view == null) {
                continue;
            }
            measureChildWithMargins(view, 0, 0);
            addView(view);
            right = left + getDecoratedMeasuredWidth(view) + childPadding;
            layoutDecoratedWithMargins(view, left, 0, right, getHeight());
            left = right;
        }
        return true;
    }
}
