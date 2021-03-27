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

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import widget.cf.com.widgetlibrary.LinearCallbackSmoothScroller;
import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.adapter.BaseCommonAdapter;
import widget.cf.com.widgetlibrary.adapter.DefaultViewHolder;
import widget.cf.com.widgetlibrary.base.BaseCallBack;
import widget.cf.com.widgetlibrary.tintview.TintColorManager;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public abstract class BaseIndicator<T> extends RecyclerView {
    private static float indicatorHigh = ApplicationUtil.getDimension(R.dimen.dp_2);
    protected ViewPager mPager;
    protected BaseCommonAdapter<T> adapter;
    protected LinearLayoutManager layoutManager;
    protected LinearCallbackSmoothScroller mSmoothScroller;
    protected T select;

    private RectF indicatorRect = new RectF();
    private Paint indicatorPaint;
    private boolean indicatorScroll = false;
    private IScroll iScroll = new ScrollV1();
    private ValueAnimator mClickAnimator;
    private int mAnimatorPosition = -1;

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

    public void setIndicatorColor(int color) {
        indicatorPaint.setColor(color);
        invalidate();
    }

    public boolean isIndicatorScroll() {
        return indicatorScroll;
    }

    public abstract IndicatorAdapter<T> getDataAdapter();

    public abstract int getIndicatorTarget();

    public T getSelect() {
        return select;
    }

    public void setViewPager(ViewPager pager) {
        this.mPager = pager;
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int selectPosition = 0;
            private boolean isDragging;
            private int dragDirection;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateIndicatorRect(position, positionOffset, getDragDirection(position));
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
                    iScroll.resetScroll();
                    mSmoothScroller.startScroll(mPager.getCurrentItem(), layoutManager, null);
                } else {
                    setIndicatorScroll(true);
                }
            }

            private int getDragDirection(int position) {
                if (!isDragging) {
                    return dragDirection;
                }
                if (position == mPager.getCurrentItem()) {
                    dragDirection = 1;
                } else {
                    dragDirection = -1;
                }
                return dragDirection;
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

    private void updateIndicatorRect(int position, float pageOffset, int dragDirection) {
        if (adapter.getData().size() == 0) {
            return;
        }
        if (pageOffset == 0f) {
            return;
        }
        IndicatorHolder holder = (IndicatorHolder) findViewHolderForAdapterPosition(position);
        if (holder == null) {
            scrollToPosition(position);
            return;
        }
        holder.updateIndicatorColor(pageOffset);
        View menuView = holder.itemView;
        holder = (IndicatorHolder) findViewHolderForAdapterPosition(position + 1);
        if (holder == null) {
            if (position + 1 < adapter.getItemCount()) {
                scrollToPosition(position + 1);
            }
            return;
        }
        holder.updateIndicatorColor(1 - pageOffset);
        iScroll.scroll(position, pageOffset, dragDirection, menuView, holder.itemView);
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
        if (position == mAnimatorPosition) {
            return;
        }
        mAnimatorPosition = position;
        select = adapter.getItem(position);
        int scrollPosition = getScrollPosition(position);
        if (scrollPosition == position) {
            setIndicatorScroll(true);
            Point startPoint = getIndicatorLocation(mPager.getCurrentItem());
            Point endPoint = getIndicatorLocation(position);
            startClickAnimator(startPoint, endPoint, () -> {
                endAnimator(position);
                if (!isPositionCompletelyVisible(position)) {
                    mSmoothScroller.startScroll(position, layoutManager, null);
                }
            });
        } else {
            mSmoothScroller.startScroll(scrollPosition, layoutManager, () -> {
                setIndicatorScroll(true);
                Point startPoint = getIndicatorLocation(mPager.getCurrentItem());
                Point endPoint = getIndicatorLocation(position, scrollPosition);
                if (endPoint != null) {
                    startClickAnimator(startPoint, endPoint, () -> endAnimator(position));
                }
            });
        }
    }

    private void endAnimator(int position) {
        mClickAnimator = null;
        mAnimatorPosition = -1;
        setIndicatorScroll(false);
        mPager.setCurrentItem(position, false);
    }

    private boolean isPositionCompletelyVisible(int position) {
        int firstPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        int lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();
        return position >= firstPosition && position <= lastPosition;
    }


    private void startClickAnimator(Point startPoint, Point endPoint, BaseCallBack.CallBack onAnimatorEnd) {
        if (mClickAnimator != null) {
            mClickAnimator.cancel();
        }
        mClickAnimator = ValueAnimator.ofFloat(0, 1f);
        mClickAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            int left;
            int right;
            if (endPoint.x > startPoint.x) {
                left = startPoint.x + (int) ((endPoint.x - startPoint.x) * value);
                right = startPoint.y + (int) ((endPoint.y - startPoint.y) * value);
            } else {
                left = endPoint.x + (int) ((startPoint.x - endPoint.x) * (1 - value));
                right = endPoint.y + (int) ((startPoint.y - endPoint.y) * (1 - value));
            }
            updateRect(left, right);
        });
        mClickAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                BaseCallBack.onCallBack(onAnimatorEnd);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                BaseCallBack.onCallBack(onAnimatorEnd);
            }
        });
        mClickAnimator.setInterpolator(new FastOutSlowInInterpolator());
        mClickAnimator.setDuration(500);
        mClickAnimator.start();
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
            point.y = 0;
            point.x = (int) -indicatorRect.width();
        } else {
            point.x = getWidth();
            point.y = getWidth() + (int) indicatorRect.width();
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

    public abstract class IndicatorAdapter<T> extends BaseCommonAdapter<T> {
        @Override
        public void onItemClick(int position, View v, T item) {
            if (mPager.getCurrentItem() == position) {
                return;
            }
            clickPosition(position);
        }
    }

    public abstract class IndicatorHolder<H> extends DefaultViewHolder<H> {

        public IndicatorHolder(View view) {
            super(view, true);
        }

        public IndicatorHolder(View view, boolean longClickEnable) {
            super(view, true, longClickEnable);
        }

        public void updateIndicatorColor(float offset) {
        }

        public abstract void updateSelect();
    }

    interface IScroll {
        void scroll(int position, float pageOffset, int dragDirection, View menuView, View nextView);

        default void resetScroll() {
        }
    }

    public class ScrollV1 implements IScroll {

        private float lastPageOffset = 0;
        private View scrollView;

        @Override
        public void resetScroll() {
            lastPageOffset = 0f;
            scrollView = null;
        }

        public void scroll(int position, float pageOffset, int dragDirection, View menuView, View nextView) {
            if (scrollView != menuView) {
                lastPageOffset = dragDirection == 1 ? 0 : 1;
                this.scrollView = menuView;
            }
            int leftStart = menuView.getLeft() + menuView.findViewById(getIndicatorTarget()).getLeft();
            int rightStart = leftStart + menuView.findViewById(getIndicatorTarget()).getWidth();
            int leftEnd = nextView.getLeft() + nextView.findViewById(getIndicatorTarget()).getLeft();
            int rightEnd = leftEnd + nextView.findViewById(getIndicatorTarget()).getWidth();
            int left = (int) (leftStart + (leftEnd - leftStart) * pageOffset);
            int right = (int) (rightStart + (rightEnd - rightStart) * pageOffset);
            updateRect(left, right);
            int needScroll = needScroll(position, pageOffset, dragDirection);
            if (needScroll != 0) {
                int width = needScroll == 1 ? nextView.getWidth() : menuView.getWidth();
                int offset = (int) (width * (pageOffset - lastPageOffset) * 1.1f);
                scrollBy(offset, 0);
                lastPageOffset = pageOffset;
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
                if (position + 1 >= lastPosition) {
                    scroll = 1;
                }
            } else {
                if (position <= firstPosition) {
                    scroll = -1;
                }
            }
            return scroll;
        }
    }

    public class ScrollV2 implements IScroll {

        private View scrollView;

        public void scroll(int position, float pageOffset, int dragDirection, View menuView, View nextView) {
            int leftStart = menuView.getLeft() + menuView.findViewById(getIndicatorTarget()).getLeft();
            int rightStart = leftStart + menuView.findViewById(getIndicatorTarget()).getWidth();
            int leftEnd = nextView.getLeft() + nextView.findViewById(getIndicatorTarget()).getLeft();
            int rightEnd = leftEnd + nextView.findViewById(getIndicatorTarget()).getWidth();
            int left = (int) (leftStart + (leftEnd - leftStart) * pageOffset);
            int right = (int) (rightStart + (rightEnd - rightStart) * pageOffset);
            updateRect(left, right);
            if (scrollView == menuView) {
                return;
            }
            scrollView = menuView;
            if (!mSmoothScroller.isScrolling()) {
                int needScroll = needScroll(position, pageOffset, dragDirection);
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
    }

    public class ScrollV3 implements IScroll {

        public void scroll(int position, float pageOffset, int dragDirection, View menuView, View nextView) {
            int leftStart = menuView.getLeft() + menuView.findViewById(getIndicatorTarget()).getLeft();
            int rightStart = leftStart + menuView.findViewById(getIndicatorTarget()).getWidth();
            int leftEnd = nextView.getLeft() + nextView.findViewById(getIndicatorTarget()).getLeft();
            int rightEnd = leftEnd + nextView.findViewById(getIndicatorTarget()).getWidth();
            int left = (int) (leftStart + (leftEnd - leftStart) * pageOffset);
            int right = (int) (rightStart + (rightEnd - rightStart) * pageOffset);
            updateRect(left, right);
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
    }

}
