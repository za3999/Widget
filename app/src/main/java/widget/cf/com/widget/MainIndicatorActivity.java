package widget.cf.com.widget;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import widget.cf.com.widget.databinding.MainIndicatorLayoutBinding;
import widget.cf.com.widgetlibrary.appearance.AppearanceUtil;
import widget.cf.com.widgetlibrary.base.BaseActivity;
import widget.cf.com.widgetlibrary.indicator.FolderData;
import widget.cf.com.widgetlibrary.indicator.Menu;
import widget.cf.com.widgetlibrary.indicator.RecycleIndicator;

public class MainIndicatorActivity extends BaseActivity {

    private List<Menu> menuDataList = new ArrayList<>();
    private MainIndicatorLayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppearanceUtil.setDelegate(this);
        super.onCreate(savedInstanceState);
        binding = MainIndicatorLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        menuDataList.add(new Menu(new FolderData("1", "Test1")));
        menuDataList.add(new Menu(new FolderData("2", "Test2")));
        menuDataList.add(new Menu(new FolderData("3", "Test3")));
        menuDataList.add(new Menu(new FolderData("4", "Test4")));
        menuDataList.add(new Menu(new FolderData("5", "Test5")));
        menuDataList.add(new Menu(new FolderData("6", "Test6")));
        menuDataList.add(new Menu(new FolderData("7", "Test7")));
        menuDataList.add(new Menu(new FolderData("8", "Test8")));
        menuDataList.add(new Menu(new FolderData("9", "Test9")));
        menuDataList.add(new Menu(new FolderData("10", "Test10")));
        binding.viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()));
        binding.indicatorLayout.setEditListener(
                new RecycleIndicator.EditListener() {

                    @Override
                    public void onFinish(int selectPosition, List<Menu> menuList) {
                        menuDataList = menuList;
                        binding.viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()));
                        binding.indicatorLayout.setData(selectPosition, menuDataList);
                    }

                    @Override
                    public void onIndicatorEditChange(boolean isEdit) {
                    }

                    @Override
                    public void scroll2NextUnreadItem() {
                    }
                }

        );
        binding.indicatorLayout.setViewPager(binding.viewPager);
        binding.indicatorLayout.setData(0, menuDataList);
    }

    @Override
    public void onBackPressed() {
        if (!binding.indicatorLayout.finishEditModel()) {
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
