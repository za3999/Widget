package widget.cf.com.widgetlibrary.touchmenu;

import android.view.View;
import android.view.ViewGroup;

import widget.cf.com.widgetlibrary.base.BaseCallBack;

public interface ITouchPopMenu<T> {

    ViewGroup getRoot();

    int getMenuWith();

    void onTouchChange(int index);

    void onResult();

    void setCloseHelper(BaseCallBack.CallBack closeListener);

    TouchPopParam getPopParam();

    ITouchPopMenu setTouchListener(TouchItemListener<T> touchListener);

    default void onTouchMove(float eventX, float eventY) {
        if (getRoot() == null || getRoot().getChildCount() == 0) {
            return;
        }
        float x = eventX - getRoot().getX();
        float y = eventY - getRoot().getY();
        for (int i = 0; i < getRoot().getChildCount(); i++) {
            View view = getRoot().getChildAt(i);
            if (TouchUtils.isInView(view, x, y)) {
                onTouchChange(i);
                break;
            }
        }
    }

    default void onTouchUp() {
        onResult();
    }

    default ITouchPopMenu setXOffset(float offset) {
        getPopParam().setXOffset(offset);
        return this;
    }

    default ITouchPopMenu setYOffset(float offset) {
        getPopParam().setYOffset(offset);
        return this;
    }

}
