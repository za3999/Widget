package widget.cf.com.widgetlibrary.indicator;

import android.view.ViewGroup;
import android.widget.TextView;

import widget.cf.com.widgetlibrary.R;

public class MenuHolder {

    private ViewGroup menuView;
    private MenuData menuData;
    private TextView nameView;

    public MenuHolder(ViewGroup menuView) {
        this.menuView = menuView;
        this.nameView = menuView.findViewById(R.id.tv_name);
    }

    public ViewGroup getMenuView() {
        return menuView;
    }

    public MenuData getMenuData() {
        return menuData;
    }

    public MenuHolder bindData(MenuData menuData) {
        this.menuData = menuData;
        nameView.setText(menuData.getTitle());
        return this;
    }

    public void setSelect(boolean select) {
        nameView.setSelected(select);
    }
}
