package widget.cf.com.widgetlibrary.indicator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.adapter.BaseCommonAdapter;
import widget.cf.com.widgetlibrary.adapter.BaseViewHolder;
import widget.cf.com.widgetlibrary.adapter.DefaultViewHolder;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;


public class RecycleIndicator extends RecyclerView {
    private boolean isEditModel;
    private PartScrollViewPager mPager;
    private BaseCommonAdapter<Menu> adapter;
    private LinearLayoutManager layoutManager;
    private Rect indicatorRect = new Rect();
    private Paint indicatorPaint;
    private boolean indicatorScroll = true;
    private ItemTouchHelper mItemTouchHelper;
    private EditListener mEditListener;

    public RecycleIndicator(Context context) {
        this(context, null);
    }

    public RecycleIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecycleIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        indicatorPaint = new Paint();
        indicatorPaint.setColor(ContextCompat.getColor(context, R.color.color_red_ccfa3c55));
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(layoutManager);
        setItemAnimator(null);
        adapter = new BaseCommonAdapter<Menu>() {
            @NonNull
            @Override
            public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ItemHolder(createView(R.layout.recycle_indicatior_item, parent));
            }

            @Override
            public void onItemClick(int position, View v, Menu item) {
                if (v.getId() == R.id.del_view) {
                    adapter.getData().remove(position);
                    adapter.notifyItemRemoved(position);
                } else if (!isEditModel) {
                    mPager.setCurrentItem(position, true);
                }
            }

            @Override
            public void onItemLongClick(int position, View v, Menu item) {
                setEditModel(true);
            }
        };
        setAdapter(adapter);
        addItemDecoration(new ItemDecoration() {

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, State state) {
                if (indicatorScroll) {
                    c.drawRect(indicatorRect, indicatorPaint);
                }
            }
        });
        addTouchHelper();
    }

    public void setEditListener(EditListener editListener) {
        this.mEditListener = editListener;
    }

    private void setEditModel(boolean editModel) {
        isEditModel = editModel;
        mPager.setScroll(!editModel);
        adapter.notifyDataSetChanged();
    }

    public boolean finishEditModel() {
        if (isEditModel) {
            setEditModel(false);
            if (mEditListener != null) {
                mEditListener.onFinish(getSelect(), new ArrayList<>(adapter.getData()));
            }
            return true;
        }
        return false;
    }

    private int getSelect() {
        int size = adapter.getData().size();
        for (int i = 0; i < size; i++) {
            if (adapter.getData().get(i).isSelect()) {
                return i;
            }
        }
        return 0;
    }

    public void setViewPager(PartScrollViewPager pager) {
        this.mPager = pager;
        mPager.setScroll(true);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                selectChild(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setData(int selectPosition, List<Menu> menuData) {
        adapter.setData(menuData);
        layoutManager.scrollToPositionWithOffset(selectPosition, 0);
        mPager.setCurrentItem(selectPosition);
    }

    private void updateView(int position) {
        List<Menu> menuData = adapter.getData();
        if (adapter.getData().size() == 0) {
            return;
        }
        int size = menuData.size();
        for (int i = 0; i < size; i++) {
            Menu itemData = menuData.get(i);
            if (i == position) {
                itemData.setSelect(true);
                adapter.notifyItemChanged(i);
            } else if (itemData.isSelect()) {
                itemData.setSelect(false);
                adapter.notifyItemChanged(i);
            }
        }
    }

    private void selectChild(int position, float offset) {
        if (adapter.getData().size() == 0) {
            return;
        }
        updateView(position);
        calculateIndicatorRect(position, offset);
    }

    private void calculateIndicatorRect(int position, float pageOffset) {
        RecyclerView recyclerView = adapter.getRecyclerView();
        if (recyclerView == null) {
            return;
        }
        ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
        if (holder == null) {
            return;
        }
        View menuView = holder.itemView;
        if (menuView == null) {
            return;
        }
        int bottom = getHeight();
        int top = bottom - ApplicationUtil.getIntDimension(R.dimen.dp_2);
        int left;
        int right;
        if (pageOffset == 0f) {
            left = menuView.getLeft();
            right = menuView.getRight();
            indicatorScroll = false;
        } else {
            indicatorScroll = true;
            holder = recyclerView.findViewHolderForAdapterPosition(position + 1);
            if (holder == null) {
                return;
            }
            View nextView = holder.itemView;
            if (nextView == null) {
                return;
            }
            left = (int) (menuView.getLeft() + menuView.getWidth() * pageOffset);
            right = (int) (nextView.getLeft() + nextView.getWidth() * pageOffset);
            indicatorRect.set(left, top, right, bottom);
        }
        scroll2Center(left, right);
    }

    private void scroll2Center(int left, int right) {
        int offset = left + ((right - left) / 2) - getWidth() / 2;
        if (offset < 0 && !canScrollHorizontally(-1)
                || offset > 0 && !canScrollHorizontally(1)) {
            return;
        }
        scrollBy(offset, 0);
        indicatorRect.set(left - offset, indicatorRect.top, right - offset, indicatorRect.bottom);
    }

    private void addTouchHelper() {
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return isEditModel;
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, 0);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int
                    fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                if (target instanceof ItemHolder) {
                    super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
                    Collections.swap(adapter.getData(), fromPos, toPos);
                    adapter.notifyItemMoved(fromPos, toPos);
                }
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

        });
        mItemTouchHelper.attachToRecyclerView(this);
    }

    public void shakeView(final View view, final int xOffset) {
        if (view == null) {
            return;
        }
        if (!isEditModel) {
            view.setTag(false);
            return;
        }
        view.setTag(true);
        view.postDelayed(() -> {
            if (!isEditModel) {
                view.setTag(false);
                return;
            }
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, View.ROTATION, xOffset, 0);
            objectAnimator.setDuration(50);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    shakeView(view, xOffset);
                }
            });
            objectAnimator.start();
        }, 5000);
    }

    private class ItemHolder extends DefaultViewHolder<Menu> implements IndicatorListener {

        public ItemHolder(View view) {
            super(view, true);
        }

        TextView nameTv;
        TextView unreadNumberView;
        View delView;
        View lineView;
        View contentLayout;

        @Override
        public void initView(View view) {
            nameTv = view.findViewById(R.id.tv_name);
            unreadNumberView = view.findViewById(R.id.unread_number_view);
            delView = view.findViewById(R.id.del_view);
            delView.setOnClickListener(this);
            lineView = view.findViewById(R.id.line);
            contentLayout = view.findViewById(R.id.content_layout);
        }

        @Override
        public void onAttachedToWindow() {
            updateUnreadCount();
        }

        @Override
        public void onDetachedFromWindow() {
        }

        @Override
        public void bindData(int position, Menu data) {
            nameTv.setText(data.getTitle());
            itemView.setSelected(data.isSelect());
            updateLine();
            updateUnreadCount();
            delView.setVisibility(isEditModel && !data.isAll() ? View.VISIBLE : View.GONE);
            if (isEditModel) {
                if (contentLayout.getTag() != null && (Boolean) contentLayout.getTag()) {
                    return;
                }
                shakeView(contentLayout, ApplicationUtil.getIntDimension(R.dimen.dp_2));
            }
        }

        private void updateLine() {
            if (!indicatorScroll && itemData.isSelect()) {
                lineView.setVisibility(View.VISIBLE);
            } else {
                lineView.setVisibility(View.GONE);
            }
        }

        private void updateUnreadCount() {
            unreadNumberView.setVisibility(itemData.getUnreadCount() > 0 && !isEditModel ? View.VISIBLE : View.GONE);
            unreadNumberView.setText(itemData.getUnreadCount() + "");
        }

        @Override
        public void unreadChange(int unreadCount) {
            updateUnreadCount();
        }

        @Override
        public int getKey() {
            return itemData.getId();
        }
    }

    public interface EditListener {
        void onFinish(int selectPosition, List<Menu> menuList);
    }

    public interface IndicatorListener {

        void unreadChange(int unreadCount);

        int getKey();
    }
}
