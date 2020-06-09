package widget.cf.com.widgetlibrary.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.adapter.BaseCommonAdapter;
import widget.cf.com.widgetlibrary.adapter.BaseViewHolder;
import widget.cf.com.widgetlibrary.adapter.DefaultViewHolder;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class RecycleIndicator extends RecyclerView {
    private ViewPager mPager;
    private BaseCommonAdapter<MenuData> adapter;
    private LinearLayoutManager layoutManager;
    private Rect indicatorRect = new Rect();
    private Paint indicatorPaint;
    private int currentSelect = -1;

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
                return new DefaultViewHolder<MenuData>(createView(R.layout.recycle_indicatior_item, parent)) {
                    TextView nameTv;

                    @Override
                    public void initView(View view) {
                        nameTv = view.findViewById(R.id.tv_name);
                    }

                    @Override
                    protected void onBindData(int position, MenuData data) {
                        nameTv.setText(data.getTitle());
                        data.setMenuView(itemView);
                        itemView.setSelected(data.isSelect());
                        itemView.setOnClickListener(v -> mPager.setCurrentItem(position, true));
                    }
                };
            }
        };
        setAdapter(adapter);
        addItemDecoration(new ItemDecoration() {
            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, State state) {
                super.onDrawOver(c, parent, state);
                c.drawRect(indicatorRect, indicatorPaint);
            }
        });
    }

    public void setViewPager(ViewPager pager, List<MenuData> menus) {
        this.mPager = pager;
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                selectChild(position, positionOffset);
                invalidate();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        notifyDataSetChanged(menus);
    }

    public void notifyDataSetChanged(List<MenuData> menus) {
        adapter.setData(menus);
        adapter.notifyDataSetChanged();
    }

    private void selectChild(int position) {
        List<MenuData> menuData = adapter.getData();
        if (adapter.getData().size() == 0) {
            return;
        }
        if (currentSelect != position) {
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
            currentSelect = position;
        }
    }

    private void selectChild(int position, float offset) {
        if (adapter.getData().size() == 0) {
            return;
        }
        selectChild(position);
        calculateIndicatorRect(position, offset);
    }

    private void calculateIndicatorRect(int position, float pageOffset) {
        View menuView = adapter.getItem(position).getMenuView();
        if (menuView == null) {
            return;
        }
        int bottom = getBottom();
        int top = bottom - ApplicationUtil.getIntDimension(R.dimen.dp_2);
        int left;
        int right;
        if (pageOffset == 0f) {
            left = menuView.getLeft();
            right = menuView.getRight();
            indicatorRect.set(left, top, right, bottom);
        } else {
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
        invalidate();
    }
}
