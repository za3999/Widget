package widget.cf.com.widgetlibrary.indicator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import widget.cf.com.widgetlibrary.LinearCallbackSmoothScroller;
import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.adapter.BaseCommonAdapter;
import widget.cf.com.widgetlibrary.adapter.DefaultViewHolder;
import widget.cf.com.widgetlibrary.tintview.TintColorManager;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public abstract class BaseIndicator<T> extends RecyclerView {

    private static float indicatorHigh = ApplicationUtil.getDimension(R.dimen.dp_2);
    protected ViewPager mPager;
    private BaseCommonAdapter<T> adapter;
    private LinearLayoutManager layoutManager;
    private RectF indicatorRect = new RectF();
    private Paint indicatorPaint;
    private boolean indicatorScroll = false;
    private T select;
    int selectPosition = 0;
    private LinearCallbackSmoothScroller mSmoothScroller;

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
        mSmoothScroller = new LinearCallbackSmoothScroller(getContext());
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

            private boolean isDragging;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateIndicatorRect(position, positionOffset, draggingDirection(position));
                if (positionOffset != 0f || selectPosition == position || adapter.getData().size() <= position) {
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
                selectPosition = position;
                select = adapter.getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                isDragging = state == ViewPager.SCROLL_STATE_DRAGGING;
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    setIndicatorScroll(false);
                } else {
                    setIndicatorScroll(true);
                }
            }

            private int draggingDirection(int position) {
                if (!isDragging) {
                    return 0;
                }
                if (position == mPager.getCurrentItem()) {
                    return 1;
                } else {
                    return -1;
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
        this.selectPosition = selectPosition;
        select = adapter.getItem(selectPosition);
        layoutManager.scrollToPositionWithOffset(selectPosition, 0);
        mPager.setCurrentItem(selectPosition, false);
    }

    private void updateIndicatorRect(int position, float pageOffset, int draggingDirection) {
        if (adapter.getData().size() == 0) {
            return;
        }

        IndicatorHolder holder = (IndicatorHolder) findViewHolderForAdapterPosition(position);
        if (holder == null) {
            scrollToPosition(position);
            return;
        }
        holder.updateIndicatorColor(pageOffset);
        View menuView = holder.itemView;
        if (pageOffset == 0f) {
            invalidate();
            return;
        }
        holder = (IndicatorHolder) findViewHolderForAdapterPosition(position + 1);
        if (holder == null) {
            if (position + 1 < adapter.getItemCount()) {
                scrollToPosition(position + 1);
            }
            return;
        }
        holder.updateIndicatorColor(1 - pageOffset);
        View nextView = holder.itemView;
        int leftStart = menuView.getLeft() + menuView.findViewById(getIndicatorTarget()).getLeft();
        int rightStart = leftStart + menuView.findViewById(getIndicatorTarget()).getWidth();
        int leftEnd = nextView.getLeft() + nextView.findViewById(getIndicatorTarget()).getLeft();
        int rightEnd = leftEnd + nextView.findViewById(getIndicatorTarget()).getWidth();
        int left = (int) (leftStart + (leftEnd - leftStart) * pageOffset);
        int right = (int) (rightStart + (rightEnd - rightStart) * pageOffset);
        updateRect(left, right);
        if (!mSmoothScroller.isScrolling()) {
            int needScroll = needScroll(position, pageOffset, draggingDirection);
            if (needScroll != 0) {
                mSmoothScroller.startScroll(needScroll == 1 ? position + 2 : position - 1, layoutManager, null);
            }
        }
    }

    protected int needScroll(int position, float pageOffset, int draggingDirection) {
        int scroll = 0;
        if (pageOffset == 0f || draggingDirection == 0) {
            return 0;
        }
        int firstPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        int lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();
        if (draggingDirection == 1) {
            if (position + 1 >= lastPosition && position + 1 < adapter.getData().size() - 1) {
                scroll = 1;
            }
        } else {
            if (position <= firstPosition && position > 0) {
                scroll = -1;
            }
        }
        return scroll;
    }

    private void updateRect(int left, int right) {
        int bottom = getHeight();
        float top = bottom - indicatorHigh;
        indicatorRect.set(left, top, right, bottom);
        invalidate();
    }

    private int getScrollPosition(int position) {
        int result = position;
        int firstPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        int lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();
        if (position <= firstPosition && position != 0) {
            result--;
        } else if (position >= lastPosition && position < adapter.getData().size() - 1) {
            result++;
        }
        return result;
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

    private void clickPosition(int position) {
        select = adapter.getItem(position);
        setIndicatorScroll(true);
        updateItemStatus();
        int scrollPosition = getScrollPosition(position);
        if (scrollPosition == position) {
            Point startPoint = getIndicatorLocation(mPager.getCurrentItem());
            Point endPosition = getIndicatorLocation(position);
            startClickAnimator(startPoint, endPosition);
            mPager.setCurrentItem(position, false);
        } else {
            mSmoothScroller.startScroll(scrollPosition, layoutManager, () -> {
                Point startPoint = getIndicatorLocation(mPager.getCurrentItem());
                Point endPosition = getIndicatorLocation(position, scrollPosition);
                if (endPosition != null) {
                    startClickAnimator(startPoint, endPosition);
                }
                mPager.setCurrentItem(position, false);
            });
        }
    }

    private void startClickAnimator(Point startPoint, Point endPosition) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1f);
        valueAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            int left;
            int right;
            if (endPosition.x > startPoint.x) {
                left = startPoint.x + (int) ((endPosition.x - startPoint.x) * value);
                right = startPoint.y + (int) ((endPosition.y - startPoint.y) * value);
            } else {
                left = endPosition.x + (int) ((startPoint.x - endPosition.x) * (1 - value));
                right = endPosition.y + (int) ((startPoint.y - endPosition.y) * (1 - value));
            }
            updateRect(left, right);
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                setIndicatorScroll(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setIndicatorScroll(false);
            }
        });

        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

    private Point getIndicatorLocation(int position) {
        Point point = new Point();
        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        int lastPosition = layoutManager.findLastVisibleItemPosition();
        if (position >= firstPosition && position <= lastPosition) {
            IndicatorHolder holder = (IndicatorHolder) findViewHolderForAdapterPosition(position);
            View menuView = holder.itemView;
            point.x = menuView.getLeft() + menuView.findViewById(getIndicatorTarget()).getLeft();
            point.y = point.x + menuView.findViewById(getIndicatorTarget()).getWidth();
        } else if (position < firstPosition) {
            point.x = 0;
            point.y = 0;
        } else {
            point.x = getWidth();
            point.y = getWidth();
        }
        return point;
    }

    private Point getIndicatorLocation(int position, int scrollPosition) {
        if (position == scrollPosition) {
            return getIndicatorLocation(position);
        }
        View view = ((IndicatorHolder) findViewHolderForAdapterPosition(position)).itemView;
        IndicatorHolder scrollHolder = ((IndicatorHolder) findViewHolderForAdapterPosition(scrollPosition));
        if (scrollHolder == null) {
            return null;
        }
        View scrollView = scrollHolder.itemView;
        int offset = (view.getWidth() - view.findViewById(getIndicatorTarget()).getWidth()) / 2;
        Point point = new Point();
        if (position < scrollPosition) {
            point.x = getWidth() - scrollView.getWidth() - view.getWidth() + offset;
        } else {
            point.x = scrollView.getWidth() + offset;
        }
        point.y = point.x + view.findViewById(getIndicatorTarget()).getWidth();
        return point;
    }

    public abstract class IndicatorHolder<H> extends DefaultViewHolder<H> {

        public IndicatorHolder(View view) {
            super(view, true);
        }

        @Override
        public void onItemClick(View v, H t) {
            clickPosition(getChildAdapterPosition(itemView));
        }

        void updateIndicatorColor(float offset) {
        }

        abstract void updateSelect();
    }
}
