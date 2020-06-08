package widget.cf.com.widget;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import widget.cf.com.widgetlibrary.appearance.AppearanceUtil;
import widget.cf.com.widgetlibrary.base.BaseActivity;
import widget.cf.com.widgetlibrary.indicator.MenuData;
import widget.cf.com.widgetlibrary.indicator.TabIndicator;

public class MainIndicatorActivity extends BaseActivity {

    private ViewPager mPager;
    private TabIndicator mIndicator;
    private int mCurrentFragmentIndex = -1;
    private List<MenuData> menuDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppearanceUtil.setDelegate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_indicator_layout);
        menuDataList.add(new MenuData(1, "Test1"));
        menuDataList.add(new MenuData(2, "Test2"));
        menuDataList.add(new MenuData(3, "Test3"));
        menuDataList.add(new MenuData(4, "Test4"));
        menuDataList.add(new MenuData(5, "Test5"));
        menuDataList.add(new MenuData(6, "Test6"));
        menuDataList.add(new MenuData(7, "Test7"));
        menuDataList.add(new MenuData(8, "Test8"));
        menuDataList.add(new MenuData(9, "Test9"));
        menuDataList.add(new MenuData(10, "Test10"));
        mPager = findViewById(R.id.view_pager);
        mPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()));
        mIndicator = findViewById(R.id.indicator_layout);
        mIndicator.setViewPager(mPager, menuDataList);
        switchFragment(0);
    }

    public void switchFragment(int index) {
        if (mCurrentFragmentIndex != index) {
            mPager.setCurrentItem(index, false);
        }
    }

    class FragmentStatePagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {
        public FragmentStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TestFragment.getInstance("我是第" + position + "个Fragment");
        }

        @Override
        public int getCount() {
            return menuDataList.size();
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
