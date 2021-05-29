package widget.cf.com.widgetlibrary.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ScrollerCompat;
import androidx.recyclerview.widget.RecyclerView;

import widget.cf.com.widgetlibrary.util.LogUtils;

public class DragSelectTouchListener implements RecyclerView.OnItemTouchListener {
    private static final String TAG = "DSTL";

    private boolean mIsActive;
    private int mStart, mEnd;
    private boolean mInTopSpot, mInBottomSpot;
    private int mScrollDistance;
    private float mScrollSpeedFactor;
    private float mLastX, mLastY;
    private int mLastStart, mLastEnd;

    private OnDragSelectListener mSelectListener;
    private RecyclerView mRecyclerView;
    private ScrollerCompat mScroller;
    private Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (mScroller != null && mScroller.computeScrollOffset()) {
                scrollBy(mScrollDistance);
                ViewCompat.postOnAnimation(mRecyclerView, mScrollRunnable);
            }
        }
    };

    // Definitions for touch auto scroll regions
    private int mTopBoundFrom, mTopBoundTo, mBottomBoundFrom, mBottomBoundTo;

    private int mMaxScrollDistance = 16;
    private int mAutoScrollDistance = (int) (Resources.getSystem().getDisplayMetrics().density * 56);
    private int mTouchRegionTopOffset = 0;
    private int mTouchRegionBottomOffset = 0;
    private boolean mScrollAboveTopRegion = true;
    private boolean mScrollBelowTopRegion = true;
    private boolean mDebug = true;


    public DragSelectTouchListener(OnDragSelectListener dragSelectListener) {
        this.mSelectListener = dragSelectListener;
        reset();
    }

    // -----------------------
    // Main functions
    // -----------------------

    public void startDragSelection(int position) {
        setIsActive(true);
        mStart = position;
        mEnd = position;
        mLastStart = position;
        mLastEnd = position;
        if (mSelectListener != null && mSelectListener instanceof OnDragSelectListener)
            ((OnDragSelectListener) mSelectListener).onSelectionStarted(position);
    }

    // -----------------------
    // Functions
    // -----------------------

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (!mIsActive || rv.getAdapter().getItemCount() == 0)
            return false;

        int action = MotionEventCompat.getActionMasked(e);
        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                reset();
                break;
        }

        mRecyclerView = rv;
        int height = rv.getHeight();
        mTopBoundFrom = 0 + mTouchRegionTopOffset;
        mTopBoundTo = 0 + mTouchRegionTopOffset + mAutoScrollDistance;
        mBottomBoundFrom = height + mTouchRegionBottomOffset - mAutoScrollDistance;
        mBottomBoundTo = height + mTouchRegionBottomOffset;
        return true;
    }

    public void startAutoScroll() {
        if (mRecyclerView == null)
            return;

        initScroller(mRecyclerView.getContext());
        if (mScroller.isFinished()) {
            mRecyclerView.removeCallbacks(mScrollRunnable);
            mScroller.startScroll(0, mScroller.getCurrY(), 0, 5000, 100000);
            ViewCompat.postOnAnimation(mRecyclerView, mScrollRunnable);
        }
    }

    private void initScroller(Context context) {
        if (mScroller == null)
            mScroller = ScrollerCompat.create(context, new LinearInterpolator());
    }

    public void stopAutoScroll() {
        if (mScroller != null && !mScroller.isFinished()) {
            mRecyclerView.removeCallbacks(mScrollRunnable);
            mScroller.abortAnimation();
        }
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        if (!mIsActive)
            return;

        int action = MotionEventCompat.getActionMasked(e);
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (!mInTopSpot && !mInBottomSpot)
                    updateSelectedRange(rv, e);
                processAutoScroll(e);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                reset();
                break;
        }
    }

    private void updateSelectedRange(RecyclerView rv, MotionEvent e) {
        updateSelectedRange(rv, e.getX(), e.getY());
//        updateSelectItem(rv, e.getX(), e.getY());
    }

    private void updateSelectedRange(RecyclerView rv, float x, float y) {
        View child = rv.findChildViewUnder(x, y);
        if (child != null) {
            int position = rv.getChildAdapterPosition(child);
            if (position != RecyclerView.NO_POSITION && mEnd != position) {
                mEnd = position;
                notifySelectRangeChange();
            }
        }
    }

    private void processAutoScroll(MotionEvent event) {
        int y = (int) event.getY();

        if (mDebug)
            Log.d("zero SCROLL", "y = " + y +
                    " | rv.height = " + mRecyclerView.getHeight() +
                    " | mTopBoundFrom => mTopBoundTo = " + mTopBoundFrom + " => " + mTopBoundTo +
                    " | mBottomBoundFrom => mBottomBoundTo = " + mBottomBoundFrom + " => " + mBottomBoundTo +
                    " | mTouchRegionTopOffset = " + mTouchRegionTopOffset +
                    " | mTouchRegionBottomOffset = " + mTouchRegionBottomOffset);

        if (y >= mTopBoundFrom && y <= mTopBoundTo) {
            mLastX = event.getX();
            mLastY = event.getY();
            mScrollSpeedFactor = (((float) mTopBoundTo - (float) mTopBoundFrom) - ((float) y - (float) mTopBoundFrom)) / ((float) mTopBoundTo - (float) mTopBoundFrom);
            mScrollDistance = (int) ((float) mMaxScrollDistance * mScrollSpeedFactor * -1f);
            if (mDebug)
                Log.d(TAG, "first SCROLL - mScrollSpeedFactor=" + mScrollSpeedFactor + " | mScrollDistance=" + mScrollDistance);
            if (!mInTopSpot) {
                mInTopSpot = true;
                startAutoScroll();
            }
        } else if (mScrollAboveTopRegion && y < mTopBoundFrom) {
            mLastX = event.getX();
            mLastY = event.getY();
            mScrollDistance = mMaxScrollDistance * -1;
            if (!mInTopSpot) {
                mInTopSpot = true;
                startAutoScroll();
            }
        } else if (y >= mBottomBoundFrom && y <= mBottomBoundTo) {
            mLastX = event.getX();
            mLastY = event.getY();
            mScrollSpeedFactor = (((float) y - (float) mBottomBoundFrom)) / ((float) mBottomBoundTo - (float) mBottomBoundFrom);
            mScrollDistance = (int) ((float) mMaxScrollDistance * mScrollSpeedFactor);
            if (mDebug)
                Log.d(TAG, "second SCROLL - mScrollSpeedFactor=" + mScrollSpeedFactor + " | mScrollDistance=" + mScrollDistance);
            if (!mInBottomSpot) {
                mInBottomSpot = true;
                startAutoScroll();
            }
        } else if (mScrollBelowTopRegion && y > mBottomBoundTo) {
            mLastX = event.getX();
            mLastY = event.getY();
            mScrollDistance = mMaxScrollDistance;
            if (!mInTopSpot) {
                mInTopSpot = true;
                startAutoScroll();
            }
        } else {
            mInBottomSpot = false;
            mInTopSpot = false;
            mLastX = Float.MIN_VALUE;
            mLastY = Float.MIN_VALUE;
            stopAutoScroll();
        }
    }

    private void notifySelectRangeChange() {
        if (mSelectListener == null)
            return;
        if (mStart == RecyclerView.NO_POSITION || mEnd == RecyclerView.NO_POSITION)
            return;

        int newStart, newEnd;
        newStart = Math.min(mStart, mEnd);
        newEnd = Math.max(mStart, mEnd);

        LogUtils.d(TAG, " mStart:" + mStart + " mEnd:" + mEnd + "===" + "newStart:" + newStart + " newEnd:" + newEnd);
        if (mLastStart == RecyclerView.NO_POSITION || mLastEnd == RecyclerView.NO_POSITION) {
            if (newEnd - newStart == 1)
                mSelectListener.onSelectChange(newStart, newStart, true);
            else
                mSelectListener.onSelectChange(newStart, newEnd, true);
        } else {
            if (newStart > mLastStart)
                mSelectListener.onSelectChange(mLastStart, newStart - 1, false);
            else if (newStart < mLastStart)
                mSelectListener.onSelectChange(newStart, mLastStart - 1, true);

            if (newEnd > mLastEnd)
                mSelectListener.onSelectChange(mLastEnd + 1, newEnd, true);
            else if (newEnd < mLastEnd)
                mSelectListener.onSelectChange(newEnd + 1, mLastEnd, false);

            if (newStart == newEnd) {
                mSelectListener.onSelectChange(newStart, newEnd, false);
            }
        }

        mLastStart = newStart;
        mLastEnd = newEnd;
    }

    private void reset() {
        setIsActive(false);
        if (mSelectListener != null && mSelectListener instanceof OnAdvancedDragSelectListener)
            ((OnAdvancedDragSelectListener) mSelectListener).onSelectionFinished(mEnd);
        mStart = RecyclerView.NO_POSITION;
        mEnd = RecyclerView.NO_POSITION;
        mLastStart = RecyclerView.NO_POSITION;
        mLastEnd = RecyclerView.NO_POSITION;
        mInTopSpot = false;
        mInBottomSpot = false;
        mLastX = Float.MIN_VALUE;
        mLastY = Float.MIN_VALUE;
        stopAutoScroll();
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // ignore
    }

    private void scrollBy(int distance) {
        int scrollDistance;
        if (distance > 0)
            scrollDistance = Math.min(distance, mMaxScrollDistance);
        else
            scrollDistance = Math.max(distance, -mMaxScrollDistance);
        mRecyclerView.scrollBy(0, scrollDistance);
        if (mLastX != Float.MIN_VALUE && mLastY != Float.MIN_VALUE)
            updateSelectedRange(mRecyclerView, mLastX, mLastY);
    }

    public void setIsActive(boolean isActive) {
        this.mIsActive = isActive;
    }

    public interface OnAdvancedDragSelectListener extends OnDragSelectListener {

        void onSelectionStarted(int start);

        void onSelectionFinished(int end);
    }

    public interface OnDragSelectListener {
        void onSelectChange(int start, int end, boolean isSelected);
        void onSelectionStarted(int start);
    }
}