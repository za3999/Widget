package widget.cf.com.widget;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import widget.cf.com.widgetlibrary.appearance.AppearanceUtil;
import widget.cf.com.widgetlibrary.base.BaseActivity;
import widget.cf.com.widgetlibrary.indicator.FolderData;
import widget.cf.com.widgetlibrary.indicator.Menu;
import widget.cf.com.widgetlibrary.indicator.PartScrollViewPager;
import widget.cf.com.widgetlibrary.indicator.RecycleIndicator;

public class MainIndicatorActivity extends BaseActivity {

    private PartScrollViewPager mPager;
    private RecycleIndicator mIndicator;
    private List<Menu> menuDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppearanceUtil.setDelegate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_indicator_layout);
        menuDataList.add(new Menu(new FolderData(1, "Test1")));
        menuDataList.add(new Menu(new FolderData(2, "Test2")));
        menuDataList.add(new Menu(new FolderData(3, "Test3")));
        menuDataList.add(new Menu(new FolderData(4, "Test4")));
        menuDataList.add(new Menu(new FolderData(5, "Test5")));
        menuDataList.add(new Menu(new FolderData(6, "Test6")));
        menuDataList.add(new Menu(new FolderData(7, "Test7")));
        menuDataList.add(new Menu(new FolderData(8, "Test8")));
        menuDataList.add(new Menu(new FolderData(9, "Test9")));
        menuDataList.add(new Menu(new FolderData(10, "Test10")));
        mPager = findViewById(R.id.view_pager);
        mPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()));
        mIndicator = findViewById(R.id.indicator_layout);
        mIndicator.setEditListener((selectPosition, menuDataList) -> {
            this.menuDataList = menuDataList;
            mPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()));
            mIndicator.setData(selectPosition, this.menuDataList);
        });
        mIndicator.setViewPager(mPager);
        mIndicator.setData(0, menuDataList);
    }

    @Override
    public void onBackPressed() {
        if (!mIndicator.finishEditModel()) {
            super.onBackPressed();
        }
    }

    private class FragmentStatePagerAdapter extends androidx.fragment.app.FragmentStatePagerAdapter {
        public FragmentStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TestFragment.getInstance("我是第" + menuDataList.get(position).getId() + "个Fragment");
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
