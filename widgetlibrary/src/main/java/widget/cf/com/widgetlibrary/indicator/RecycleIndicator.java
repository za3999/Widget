package widget.cf.com.widgetlibrary.indicator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import widget.cf.com.widgetlibrary.dialog.IndicatorMenuDialog;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;
import widget.cf.com.widgetlibrary.util.ViewUtil;


public class RecycleIndicator extends RecyclerView {
    private static final String TAG = "RecycleIndicator";
    private boolean isEditModel;
    private PartScrollViewPager mPager;
    private BaseCommonAdapter<Menu> adapter;
    private LinearLayoutManager layoutManager;
    private Rect indicatorRect = new Rect();
    private Paint indicatorPaint;
    private boolean indicatorScroll = false;
    private ItemTouchHelper mItemTouchHelper;
    private EditListener mEditListener;
    private Handler mainHandler = ApplicationUtil.getMainHandler();
    private Runnable runnable;
    private String selectId;
    private int indicatorTargetViewId;

    public RecycleIndicator(Context context) {
        this(context, null);
    }

    public RecycleIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecycleIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        indicatorPaint = new Paint();
        layoutManager = new IndicatorLayoutManager(context);
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
                    if (mPager.getCurrentItem() != position) {
                        mPager.setCurrentItem(position, false);
                    } else {
                        mEditListener.scroll2NextUnreadItem();
                    }
                }
            }

            @Override
            public void onItemLongClick(int position, View v, Menu item) {
                IndicatorMenuDialog indicatorMenuDialog = new IndicatorMenuDialog(v, code -> {
                    Toast.makeText(v.getContext(), "callback:" + code, Toast.LENGTH_LONG).show();
                });
                indicatorMenuDialog.show();
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

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setIndicatorTargetViewId(int indicatorTargetViewId) {
        this.indicatorTargetViewId = indicatorTargetViewId;
    }

    public boolean isEditModel() {
        return isEditModel;
    }

    public void setEditListener(EditListener editListener) {
        this.mEditListener = editListener;
    }

    public void setEditModel(boolean editModel) {
        isEditModel = editModel;
        mEditListener.onIndicatorEditChange(isEditModel);
        mPager.setScrollAble(!editModel);
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
            if (TextUtils.equals(adapter.getData().get(i).getId(), selectId)) {
                return i;
            }
        }
        return 0;
    }

    public String getSelectItemId() {
        return selectId;
    }

    public void scroll2Menu(Menu Menu) {
        int size = adapter.getData().size();
        Menu menu;
        for (int i = 0; i < size; i++) {
            menu = adapter.getData().get(i);
            if (TextUtils.equals(menu.getId(), Menu.getId())) {
                mPager.setCurrentItem(i, false);
                break;
            }
        }
    }

    public void setViewPager(PartScrollViewPager pager) {
        this.mPager = pager;
        mPager.setScrollAble(true);
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
            }

            private void changePosition(int position) {
                oldPosition = position;
                selectId = adapter.getData().get(position).getId();
                updateItemStatus();
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

    public void setData(int selectPosition, List<Menu> menuData) {
        if (menuData == null) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(menuData.size() > 1 ? View.VISIBLE : View.GONE);
        selectId = menuData.get(selectPosition).getId();
        adapter.setData(menuData);
        if (!isEditModel) {
            layoutManager.scrollToPositionWithOffset(getSelect(), 0);
            mPager.setCurrentItem(getSelect(), false);
        }
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
        int top = bottom - ApplicationUtil.getIntDimension(R.dimen.dp_2);
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
            int leftStart = menuView.getLeft() + menuView.findViewById(indicatorTargetViewId).getLeft();
            int leftEnd = nextView.getLeft() + nextView.findViewById(indicatorTargetViewId).getLeft();
            int rightStart = menuView.getRight() - (menuView.getWidth() - menuView.findViewById(indicatorTargetViewId).getWidth()) / 2;
            int rightEnd = nextView.getRight() - (nextView.getWidth() - nextView.findViewById(indicatorTargetViewId).getWidth()) / 2;
            left = (int) (leftStart + (leftEnd - leftStart) * pageOffset);
            right = (int) (rightStart + (rightEnd - rightStart) * pageOffset);
            indicatorRect.set(left, top, right, bottom);
        }
        scroll2Center(left, right);
    }

    void setIndicatorScroll(boolean scroll) {
        if (indicatorScroll != scroll) {
            indicatorScroll = scroll;
            updateItemStatus();
        }
    }

    private void updateItemStatus() {
        adapter.runOnHolders(viewHolder -> {
            if (viewHolder instanceof ItemHolder) {
                ((ItemHolder) viewHolder).updateSelect();
            }
        });
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

    private void addTouchHelper() {
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
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
                    ItemHolder itemHolder = (ItemHolder) target;
                    if (itemHolder.getItemData().isAll()) {
                        return;
                    }
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
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, View.ROTATION, xOffset, 0, -xOffset, 0);
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

    private class ItemHolder extends DefaultViewHolder<Menu> {

        TextView nameTv;
        TextView unreadNumberView;
        View delView;
        View lineView;
        View contentLayout;

        public ItemHolder(View view) {
            super(view, true);
        }

        @Override
        public void initView(View view) {
            setIndicatorTargetViewId(R.id.target_view);
            nameTv = view.findViewById(R.id.tv_name);
            unreadNumberView = view.findViewById(R.id.unread_number_view);
            delView = view.findViewById(R.id.del_view);
            delView.setOnClickListener(this);
            lineView = view.findViewById(R.id.line);
            contentLayout = view.findViewById(R.id.content_layout);
            contentLayout.setOnClickListener(this);
            contentLayout.setOnTouchListener((v, event) -> {
                if (isEditModel && !itemData.isAll()) {
                    mItemTouchHelper.startDrag(this);
                    return true;
                }
                return false;
            });
            contentLayout.setOnLongClickListener(v -> {
                if (!isEditModel) {
                    onItemLongClick(v, itemData);
                    return true;
                }
                return false;
            });
        }

        @Override
        public void bindData(int position, Menu data) {
            update();
        }

        private void updateSelect() {
            if (!indicatorScroll && TextUtils.equals(itemData.getId(), selectId)) {
                lineView.setVisibility(View.VISIBLE);
            } else {
                lineView.setVisibility(View.GONE);
            }
            nameTv.setSelected(TextUtils.equals(itemData.getId(), selectId));
        }

        public void updateUnread() {
            if (itemData.getUnreadCount() <= 0 || isEditModel) {
                unreadNumberView.setVisibility(View.GONE);
            } else {
                unreadNumberView.setVisibility(View.VISIBLE);
            }
            unreadNumberView.setText(itemData.getUnreadCount() + "");
        }

        public void update() {
            itemView.setSelected(TextUtils.equals(itemData.getId(), selectId));
            nameTv.setText(itemData.getTitle());
            updateSelect();
            updateUnread();
            delView.setVisibility(isEditModel && !itemData.isAll() ? View.VISIBLE : View.GONE);
            if (isEditModel && !itemData.isAll()) {
                if (contentLayout.getTag() != null && (Boolean) contentLayout.getTag()) {
                    return;
                }
                shakeView(contentLayout, ApplicationUtil.getIntDimension(R.dimen.dp_2));
            }
            ViewUtil.setViewBackgroundTint(unreadNumberView, ApplicationUtil.getColor(TextUtils.equals(itemData.getId(), selectId) ? R.color.brand : R.color.edit_prompt_text));
        }

    }

    public interface EditListener {
        void onFinish(int selectPosition, List<Menu> menuList);

        void onIndicatorEditChange(boolean isEdit);

        void scroll2NextUnreadItem();
    }
}
