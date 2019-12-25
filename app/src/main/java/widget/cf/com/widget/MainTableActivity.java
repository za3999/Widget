package widget.cf.com.widget;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import widget.cf.com.widgetlibrary.appearance.AppearanceUtil;
import widget.cf.com.widgetlibrary.base.BaseActivity;
import widget.cf.com.widgetlibrary.tabbar.MainTabLayout;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class MainTableActivity extends BaseActivity {

    private ViewPager mPager;
    private MainTabLayout mTabLayout;
    private int mCurrentFragmentIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppearanceUtil.setDelegate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_table_layout);
        mPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.removeAllViews();
        mTabLayout.setMaxShowCount(999);
        mTabLayout.setMaxShowText("999+");
        mTabLayout.addItemView(R.id.nv_menu_session, R.drawable.menu_session_selecter, ApplicationUtil.getResources().getString(R.string.chat));
        mTabLayout.addItemView(R.id.nv_menu_contacts, R.drawable.menu_contact_selecter, ApplicationUtil.getResources().getString(R.string.contact));
        mTabLayout.addItemView(R.id.nv_menu_settings, R.drawable.menu_user_selecter, ApplicationUtil.getResources().getString(R.string.setting));
        mTabLayout.setTableClickListener(new MainTabLayout.OnTableClickListener() {
            @Override
            public void onClick(MainTabLayout.MenuItem menuItem) {
                onItemClick(menuItem);
            }

            @Override
            public void onDoubleClick(MainTabLayout.MenuItem menuItem) {
                onItemDoubleClick(menuItem);
            }
        });

        mPager.setAdapter(new MainFragmentStatePagerAdapter(getSupportFragmentManager()));
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentFragmentIndex = position;
                mTabLayout.setItemSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabLayout.setPopCount(0, 5);
        mTabLayout.setPopCount(1, 1000);
        mTabLayout.setPopCount(2, -1);
        switchFragment(0, true);
    }

    private void onItemDoubleClick(MainTabLayout.MenuItem menuItem) {
        Toast.makeText(this, "onItemDoubleClick name:" + menuItem.getName(), Toast.LENGTH_LONG).show();
    }

    private void onItemClick(MainTabLayout.MenuItem menuItem) {
        Toast.makeText(this, "onItemClick name:" + menuItem.getName(), Toast.LENGTH_LONG).show();
        switch (menuItem.getId()) {
            case R.id.nv_menu_session:
                switchFragment(0, false);
                break;
            case R.id.nv_menu_contacts:
                switchFragment(1, false);
                break;
            case R.id.nv_menu_settings:
                switchFragment(2, false);
                break;
        }
    }

    public void switchFragment(int index, boolean isInit) {
        if (mCurrentFragmentIndex != index) {
            if (isInit) {
                mCurrentFragmentIndex = index;
                mTabLayout.setItemSelected(index);
            }
            mPager.setCurrentItem(index, false);
        }
    }

    class MainFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
        public MainFragmentStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return TestFragment.getInstance("我是第一个Fragment");
            } else if (position == 1) {
                return TestFragment.getInstance("我是第二个Fragment");
            } else {
                return TestFragment.getInstance("我是第三个Fragment");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
