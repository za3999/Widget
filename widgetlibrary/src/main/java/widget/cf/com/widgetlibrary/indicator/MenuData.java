package widget.cf.com.widgetlibrary.indicator;

import android.view.View;

public class MenuData {
    private int id;
    private String title;
    private String unreadCount;
    private View menuView;
    private boolean isSelect;

    public MenuData(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public View getMenuView() {
        return menuView;
    }

    public void setMenuView(View menuView) {
        this.menuView = menuView;
    }

}
