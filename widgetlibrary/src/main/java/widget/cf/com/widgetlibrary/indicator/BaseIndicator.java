package widget.cf.com.widgetlibrary.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.adapter.BaseCommonAdapter;
import widget.cf.com.widgetlibrary.adapter.DefaultViewHolder;
import widget.cf.com.widgetlibrary.tintview.TintColorManager;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public abstract class BaseIndicator<T> extends RecyclerView {

    private static float indicatorHigh = ApplicationUtil.getDimension(R.dimen.dp_2);
    private ViewPager mPager;
    private BaseCommonAdapter<T> adapter;
    private LinearLayoutManager layoutManager;
    private RectF indicatorRect = new RectF();
    private Paint indicatorPaint;
    private boolean indicatorScroll = false;
    private Handler mainHandler = ApplicationUtil.getMainHandler();
    private Runnable runnable;
    private T select;

    public BaseIndicator(Context context) {
        this(context, null);
    }

    public BaseIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        indicatorPaint = new Paint();
        indicatorPaint.setColor(TintColorManager.getColor());
        layoutManager = new IndicatorLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(layoutManager);
        setItemAnimator(null);
        adapter = getDataAdapter();
        setAdapter(adapter);
        addItemDecoration(new ItemDecoration() {

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, State state) {
                if (indicatorScroll) {
                    c.drawRoundRect(indicatorRect, indicatorHigh / 2, indicatorHigh / 2, indicatorPaint);
                }
            }
        });
    }

    public boolean isIndicatorScroll() {
        return indicatorScroll;
    }

    public abstract BaseCommonAdapter<T> getDataAdapter();

    public abstract int getIndicatorTarget();

    public T getSelect() {
        return select;
    }

    public void setViewPager(ViewPager pager) {
        this.mPager = pager;
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int oldPosition = -1;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateIndicatorRect(position, positionOffset);
                if (positionOffset != 0f || oldPosition == position || adapter.getData().size() <= position) {
                    return;
                }
                changePosition(position);
            }

            @Override
            public void onPageSelected(int position) {
                if (adapter.getData().size() <= position) {
                    return;
                }
                changePosition(position);
                updateItemStatus();
            }

            private void changePosition(int position) {
                oldPosition = position;
                select = adapter.getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    setIndicatorScroll(false);
                } else {
                    setIndicatorScroll(true);
                }
            }
        });
    }

    public void setData(int selectPosition, List<T> menuData) {
        if (menuData == null) {
            setVisibility(View.GONE);
            return;
        }
        adapter.setData(menuData);
        select = adapter.getItem(selectPosition);
        layoutManager.scrollToPositionWithOffset(selectPosition, 0);
        mPager.setCurrentItem(selectPosition, false);
    }

    private void updateIndicatorRect(int position, float pageOffset) {
        if (adapter.getData().size() == 0) {
            return;
        }
        if (runnable != null) {
            mainHandler.removeCallbacks(runnable);
            runnable = null;
        }
        ViewHolder holder = findViewHolderForAdapterPosition(position);
        if (holder == null) {
            scrollToPosition(position);
            return;
        }
        View menuView = holder.itemView;
        if (menuView == null) {
            return;
        }
        int bottom = getHeight();
        float top = bottom - indicatorHigh;
        int left;
        int right;
        if (pageOffset == 0f) {
            left = menuView.getLeft();
            right = menuView.getRight();
        } else {
            holder = findViewHolderForAdapterPosition(position + 1);
            if (holder == null) {
                if (position + 1 < adapter.getItemCount()) {
                    scrollToPosition(position + 1);
                }
                return;
            }
            View nextView = holder.itemView;
            if (nextView == null) {
                return;
            }
            int leftStart = menuView.getLeft() + menuView.findViewById(getIndicatorTarget()).getLeft();
            int leftEnd = nextView.getLeft() + nextView.findViewById(getIndicatorTarget()).getLeft();
            int rightStart = menuView.getRight() - (menuView.getWidth() - menuView.findViewById(getIndicatorTarget()).getWidth()) / 2;
            int rightEnd = nextView.getRight() - (nextView.getWidth() - nextView.findViewById(getIndicatorTarget()).getWidth()) / 2;
            left = (int) (leftStart + (leftEnd - leftStart) * pageOffset);
            right = (int) (rightStart + (rightEnd - rightStart) * pageOffset);
            indicatorRect.set(left, top, right, bottom);
        }
        scroll2Center(left, right);
    }

    private void scroll2Center(int left, int right) {
        int offset = left + ((right - left) / 2) - getWidth() / 2;
        if (offset < 0 && !canScrollHorizontally(-1)
                || offset > 0 && !canScrollHorizontally(1)) {
            invalidate();
            return;
        }
        scrollBy(offset, 0);
    }

    void setIndicatorScroll(boolean scroll) {
        if (indicatorScroll != scroll) {
            indicatorScroll = scroll;
            updateItemStatus();
        }
    }

    private void updateItemStatus() {
        adapter.runOnHolders(viewHolder -> {
            if (viewHolder instanceof IndicatorHolder) {
                ((IndicatorHolder) viewHolder).updateSelect();
            }
        });
    }

    public abstract class IndicatorHolder<T> extends DefaultViewHolder<T> {

        public IndicatorHolder(View view) {
            super(view);
        }

        abstract void updateSelect();
    }
}
