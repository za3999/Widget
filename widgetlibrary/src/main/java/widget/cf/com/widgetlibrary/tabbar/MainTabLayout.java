package widget.cf.com.widgetlibrary.tabbar;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.base.BaseCallBack;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class MainTabLayout extends LinearLayout {

    private List<MenuItem> mMenuItems = new ArrayList<>();
    private OnTableClickListener mTableClickListener;
    private MenuItem mSelectItem;
    int mMaxShowCount = 99;
    String mMaxShowText = "99+";

    Handler mainHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            mTableClickListener.onClick((MenuItem) msg.obj);
        }
    };

    public MainTabLayout(Context context) {
        super(context);
    }

    public MainTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMaxShowText(String mMaxShowText) {
        this.mMaxShowText = mMaxShowText;
    }

    public void setMaxShowCount(int mMaxShowCount) {
        this.mMaxShowCount = mMaxShowCount;
    }

    public void setTableClickListener(OnTableClickListener tableClickListener) {
        this.mTableClickListener = tableClickListener;
    }

    public void addItemView(int id, int iconRes, String name) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.main_table_item, null);
        LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        view.setLayoutParams(layoutParams);
        addView(view);
        MenuItem menuItem = new MenuItem(view, menuItem1 -> {
            if (mTableClickListener == null) {
                return;
            }
            if (mSelectItem != null && menuItem1.getId() == mSelectItem.getId()) {
                if (mainHandler.hasMessages(menuItem1.getId())) {
                    mainHandler.removeMessages(menuItem1.getId());
                    mTableClickListener.onDoubleClick(menuItem1);
                } else {
                    Message message = new Message();
                    message.what = menuItem1.getId();
                    message.obj = menuItem1;
                    mainHandler.sendMessageDelayed(message, 400);
                }
            } else {
                mainHandler.removeCallbacksAndMessages(null);
                mTableClickListener.onClick(menuItem1);
            }
            mSelectItem = menuItem1;
        });
        menuItem.setMaxShowCount(mMaxShowCount);
        menuItem.setMaxShowText(mMaxShowText);
        menuItem.bindView(id, iconRes, name);
        mMenuItems.add(menuItem);
    }


    @Override
    public void removeAllViews() {
        mMenuItems.clear();
        super.removeAllViews();
    }

    public void setItemSelected(int position) {
        int size = mMenuItems.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = mMenuItems.get(i);
            if (i == position) {
                mSelectItem = item;
                item.setSelect(true);
            } else {
                item.setSelect(false);
            }
        }
    }

    public MenuItem getItem(int position) {
        if (position >= 0 && mMenuItems.size() > position) {
            return mMenuItems.get(position);
        } else {
            return null;
        }
    }

    public void setPopCount(int position, int count) {
        if (position >= 0 && mMenuItems.size() > position) {
            MenuItem item = mMenuItems.get(position);
            item.setPopCount(count);
        }
    }

    public static class MenuItem {
        View rootView;
        ImageView mIcon;
        TextView mNameTv;
        TextView mPopTv;
        View mSmallPopV;
        BaseCallBack.CallBack1<MenuItem> mOnSelectListener;
        int mMaxShowCount;
        String mMaxShowText;

        public MenuItem(View rootView, BaseCallBack.CallBack1<MenuItem> onSelectListener) {
            this.rootView = rootView;
            this.mOnSelectListener = onSelectListener;
            rootView.setOnClickListener(v -> {
                if (mOnSelectListener != null) {
                    mOnSelectListener.onCallBack(this);
                }
            });
            mIcon = rootView.findViewById(R.id.iv_icon);
            mNameTv = rootView.findViewById(R.id.tv_name);
            mPopTv = rootView.findViewById(R.id.tv_pop);
            mSmallPopV = rootView.findViewById(R.id.small_pop_v);
        }

        public void bindView(int id, int iconRes, String name) {
            rootView.setId(id);
            mIcon.setImageResource(iconRes);
            mNameTv.setText(name);
        }

        public void setMaxShowCount(int mMaxShowCount) {
            this.mMaxShowCount = mMaxShowCount;
        }

        public void setMaxShowText(String mMaxShowText) {
            this.mMaxShowText = mMaxShowText;
        }

        public void setSelect(boolean isSelected) {
            rootView.setSelected(isSelected);
        }

        public int getId() {
            return rootView.getId();
        }

        public CharSequence getName() {
            return mNameTv.getText();
        }

        private void setPopCount(int count) {
            if (count == 0) {
                mPopTv.setVisibility(View.GONE);
                mPopTv.setText("0");
                mSmallPopV.setVisibility(View.GONE);
            } else if (count > 0) {
                mSmallPopV.setVisibility(View.GONE);
                mPopTv.setVisibility(View.VISIBLE);
                int padding = count < 10 ? 0 : ApplicationUtil.getIntDimension(R.dimen.dp_4);
                mPopTv.setPadding(padding, 0, padding, 0);
                if (count > mMaxShowCount) {
                    mPopTv.setText(mMaxShowText);
                } else {
                    mPopTv.setText("" + count);
                }
            } else {
                mPopTv.setVisibility(View.GONE);
                mSmallPopV.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface OnTableClickListener {

        void onClick(MenuItem menuItem);

        void onDoubleClick(MenuItem menuItem);

    }

}
