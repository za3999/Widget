package widget.cf.com.widgetlibrary.touchmenu;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;

public class TouchMenuHelper {

    private TouchWidget mTouchWidget;

    public TouchMenuHelper registerView(View view, ITouchPopMenu menu) {
        if (mTouchWidget == null) {
            mTouchWidget = new TouchWidget((Activity) view.getContext());
        }
        if (menu.getPopParam().isLongClickEnable()) {
            view.setOnLongClickListener(v -> {
                if (!mTouchWidget.isShowing()) {
                    initMenuWidget(v, menu, menu.getPopParam());
                    mTouchWidget.show(true);
                }
                return true;
            });
        }
        if (menu.getPopParam().isTouchDownEnable()) {
            view.setOnTouchListener(new View.OnTouchListener() {

                float y;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            y = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (event.getY() - y > 10) {
                                if (!mTouchWidget.isShowing()) {
                                    initMenuWidget(v, menu, menu.getPopParam());
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
        }
        if (menu.getPopParam().isClickEnable()) {
            view.setOnClickListener(v -> {
                if (mTouchWidget.isShowing()) {
                    return;
                }
                initMenuWidget(v, menu, menu.getPopParam());
                mTouchWidget.show(false);
            });
        }
        return this;
    }

    public boolean hide() {
        if (mTouchWidget == null) {
            return false;
        }
        return mTouchWidget.hide();
    }

    private void initMenuWidget(View view, ITouchPopMenu menu, TouchPopParam popParam) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = (int) (location[0] - (menu.getMenuWith() - view.getWidth()) / 2 + popParam.getXOffset());
        int y = (int) (location[1] + view.getHeight() + popParam.getYOffset());
        mTouchWidget.setMenuView(x, y, menu);
    }
}
