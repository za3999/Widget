package widget.cf.com.widgetlibrary.touchmenu;

import android.app.Activity;
import android.view.View;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;


public class TouchMenuHelper {

    private TouchWidget mTouchWidget;

    public TouchMenuHelper(Activity activity) {
        mTouchWidget = new TouchWidget(activity);
    }

    public void hide() {
        mTouchWidget.hide();
    }

    public void registerView(View view, IMenu menu) {
        view.setOnLongClickListener(v -> {
            if (!mTouchWidget.isShowing()) {
                initMenuWidget(v, menu);
                mTouchWidget.show(true);
            }
            return true;
        });
        view.setOnClickListener(v -> {
            if (mTouchWidget.isShowing()) {
                return;
            }
            initMenuWidget(v, menu);
            mTouchWidget.show(false);
        });
    }

    private <T> void initMenuWidget(View view, IMenu menu) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0] - (menu.getMenuWith() - view.getWidth()) / 2;
        int y = location[1] + view.getHeight() + ApplicationUtil.getIntDimension(R.dimen.dp_5);
        mTouchWidget.setMenuView(x, y, menu);
    }
}
