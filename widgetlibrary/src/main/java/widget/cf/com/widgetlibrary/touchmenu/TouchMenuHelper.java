package widget.cf.com.widgetlibrary.touchmenu;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;


public class TouchMenuHelper {

    private TouchWidget mTouchWidget;

    public TouchMenuHelper(Activity activity) {
        mTouchWidget = new TouchWidget(activity);
    }

    public boolean hide() {
        return mTouchWidget.hide();
    }

    public void registerView(View view, IMenu menu) {
//        view.setOnLongClickListener(v -> {
//            if (!mTouchWidget.isShowing()) {
//                initMenuWidget(v, menu);
//                mTouchWidget.show(true);
//            }
//            return true;
//        });
        view.setOnTouchListener(new View.OnTouchListener() {

            float y;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        y = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getY() - y > 10) {
                            if (!mTouchWidget.isShowing()) {
                                initMenuWidget(v, menu);
                                mTouchWidget.show(true);
                                return true;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
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
