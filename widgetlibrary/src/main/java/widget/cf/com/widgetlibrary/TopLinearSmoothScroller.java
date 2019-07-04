package widget.cf.com.widgetlibrary;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

public class TopLinearSmoothScroller extends LinearSmoothScroller {
    private static final int TARGET_SEEK_SCROLL_DISTANCE_PX = 10000;
    private static final float MILLISECONDS_PER_INCH = 50f;

    private boolean isConstantSpend;
    private int offset;

    public TopLinearSmoothScroller(Context context) {
        this(context, false);
    }

    public TopLinearSmoothScroller(Context context, boolean isConstantSpend) {
        super(context);
        this.isConstantSpend = isConstantSpend;
    }

    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
        final int dx = calculateDxToMakeVisible(targetView, getHorizontalSnapPreference());
        int dy = calculateDyToMakeVisible(targetView, getVerticalSnapPreference());
        dy = dy + offset;
        final int distance = (int) Math.sqrt(dx * dx + dy * dy);
        final int time = calculateTimeForDeceleration(distance);
        if (time > 0) {
            action.update(-dx, -dy, time, mDecelerateInterpolator);
        }
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    protected int calculateTimeForScrolling(int dx) {
        if (!isConstantSpend && dx < TARGET_SEEK_SCROLL_DISTANCE_PX) {
            dx = TARGET_SEEK_SCROLL_DISTANCE_PX / 12;
        }
        return super.calculateTimeForScrolling(dx);
    }

    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
    }

    @Override
    protected int getVerticalSnapPreference() {
        return SNAP_TO_START;
    }

    public void scroll2Position(LinearLayoutManager layoutManager, int position, int offset, boolean smoothScroll) {
        if (smoothScroll) {
            smoothScroll2Position(layoutManager, position, offset);
        } else {
            layoutManager.scrollToPositionWithOffset(position, offset);
        }
    }

    public void smoothScroll2Position(LinearLayoutManager layoutManager, int position, int offset) {
        setTargetPosition(position);
        setOffset(offset);
        layoutManager.startSmoothScroll(this);
    }
}
