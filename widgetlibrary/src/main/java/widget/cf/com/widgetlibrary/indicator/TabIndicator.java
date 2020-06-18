package widget.cf.com.widgetlibrary.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class TabIndicator extends HorizontalScrollView {
    private LayoutInflater mLayoutInflater;
    private ViewPager mPager;
    private LinearLayout tabsContainer;
    private ArrayList<MenuHolder> menuHolders = new ArrayList<>();
    private Rect indicatorRect = new Rect();
    private Paint indicatorPaint;
    private int currentSelect = -1;

    public TabIndicator(Context context) {
        this(context, null);
    }

    public TabIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        indicatorPaint = new Paint();
        indicatorPaint.setColor(ContextCompat.getColor(context, R.color.color_red_ccfa3c55));
        mLayoutInflater = LayoutInflater.from(context);
        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);
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
        menuHolders.clear();
        tabsContainer.removeAllViews();
        int size = menus.size();
        for (int i = 0; i < size; i++) {
            addTab(i, menus.get(i));
        }
    }

    private void addTab(final int position, MenuData menuData) {
        ViewGroup tabView = (ViewGroup) mLayoutInflater.inflate(R.layout.indicatior_item, this, false);
        tabView.setOnClickListener(v -> mPager.setCurrentItem(position, false));
        MenuHolder holder = new MenuHolder(tabView).bindData(menuData);
        tabsContainer.addView(tabView, position);
        menuHolders.add(holder);
    }

    private void selectChild(int position) {
        if (menuHolders.size() == 0) {
            return;
        }
        if (currentSelect != position) {
            int size = menuHolders.size();
            for (int i = 0; i < size; i++) {
                MenuHolder menuHolder = menuHolders.get(i);
                menuHolder.setSelect(i == position);
            }
            currentSelect = position;
        }
    }

    private void selectChild(int position, float offset) {
        if (menuHolders.size() == 0) {
            return;
        }
        selectChild(position);
        calculateIndicatorRect(position, offset);
    }

    private void calculateIndicatorRect(int position, float pageOffset) {
        View menuView = menuHolders.get(position).getMenuView();
        int bottom = tabsContainer.getBottom();
        int top = bottom - ApplicationUtil.getIntDimension(R.dimen.dp_2);
        int left;
        int right;
        if (pageOffset == 0f) {
            left = menuView.getLeft();
            right = menuView.getRight();
            indicatorRect.set(left, top, right, bottom);
        } else {
            View nextView = menuHolders.get(position + 1).getMenuView();
            left = (int) (menuView.getLeft() + menuView.getWidth() * pageOffset);
            right = (int) (nextView.getRight() - nextView.getWidth() * (1 - pageOffset));
            indicatorRect.set(left, top, right, bottom);
        }
        int scrollOffset = getWidth() / 2;
        int offset = left + ((right - left) / 2) - scrollOffset;
        scrollTo(offset > 0 ? offset : 0, 0);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(indicatorRect, indicatorPaint);
    }
}
