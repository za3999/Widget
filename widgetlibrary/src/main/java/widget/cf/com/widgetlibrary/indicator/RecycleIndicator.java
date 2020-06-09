package widget.cf.com.widgetlibrary.indicator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private NoScrollViewPager mPager;
    private BaseCommonAdapter<MenuData> adapter;
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
        adapter = new BaseCommonAdapter<MenuData>() {
            @NonNull
            @Override
            public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ItemHolder(createView(R.layout.recycle_indicatior_item, parent));
            }

            @Override
            public void onItemClick(int position, View v, MenuData item) {
                mPager.setCurrentItem(position, true);
            }

            @Override
            public void onItemLongClick(int position, View v, MenuData item) {
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

    public void setViewPager(NoScrollViewPager pager) {
        this.mPager = pager;
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

    public void setData(List<MenuData> menuData) {
        adapter.setData(menuData);
    }

    private void updateView(int position) {
        List<MenuData> menuData = adapter.getData();
        if (adapter.getData().size() == 0) {
            return;
        }
        int size = menuData.size();
        for (int i = 0; i < size; i++) {
            MenuData itemData = menuData.get(i);
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
        calculateIndicatorRect(position, offset);
        updateView(position);
    }

    private void calculateIndicatorRect(int position, float pageOffset) {
        View menuView = adapter.getItem(position).getMenuView();
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
            View nextView = adapter.getItem(position + 1).getMenuView();
            if (nextView == null) {
                return;
            }
            left = (int) (menuView.getLeft() + menuView.getWidth() * pageOffset);
            right = (int) (nextView.getRight() - nextView.getWidth() * (1 - pageOffset));
            indicatorRect.set(left, top, right, bottom);
        }
        int scrollOffset = getWidth() / 2;
        int offset = left + ((right - left) / 2) - scrollOffset;
        scrollBy(offset, 0);
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

    private class ItemHolder extends DefaultViewHolder<MenuData> {

        public ItemHolder(View view) {
            super(view, true);
        }

        TextView nameTv;
        View lineView;
        View delView;
        View contentLayout;

        @Override
        public void initView(View view) {
            nameTv = view.findViewById(R.id.tv_name);
            lineView = view.findViewById(R.id.line);
            delView = view.findViewById(R.id.del_view);
            contentLayout = view.findViewById(R.id.content_layout);
        }

        @Override
        public void bindData(int position, MenuData data) {
            nameTv.setText(data.getTitle());
            data.setMenuView(itemView);
            itemView.setSelected(data.isSelect());
            if (!indicatorScroll && data.isSelect()) {
                lineView.setVisibility(View.VISIBLE);
            } else {
                lineView.setVisibility(View.GONE);
            }
            delView.setVisibility(isEditModel ? View.VISIBLE : View.GONE);
            if (isEditModel) {
                shakeView(contentLayout, ApplicationUtil.getIntDimension(R.dimen.dp_2));
            }
        }
    }

    public void shakeView(final View view, final int xOffset) {
        if (view == null) {
            return;
        }
        if (!isEditModel) {
            return;
        }
        view.postDelayed(() -> {
            if (!isEditModel) {
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playSequentially(ObjectAnimator.ofFloat(view, View.ROTATION, xOffset),
                    ObjectAnimator.ofFloat(view, View.ROTATION, 0));
            animatorSet.setDuration(50);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    shakeView(view, xOffset);
                }
            });
            animatorSet.start();
        }, 5000);
    }

    public interface EditListener {
        void onFinish(int selectPosition, List<MenuData> menuDataList);
    }
}
