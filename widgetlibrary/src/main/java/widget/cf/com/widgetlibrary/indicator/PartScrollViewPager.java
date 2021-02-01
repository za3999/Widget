package widget.cf.com.widgetlibrary.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.viewpager.widget.ViewPager;

public class PartScrollViewPager extends ViewPager {

    private boolean scrollAble = true;
    private float touchX;
    private float scrollX;
    private float touchY;
    private float scrollY;
    private float rightThreshold;
    boolean isTouchValid;

    public PartScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PartScrollViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!scrollAble) {
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = ev.getX();
                touchY = ev.getY();
                isTouchValid = touchX < getWidth() * (1 - rightThreshold);
            case MotionEvent.ACTION_MOVE:
                if (!isTouchValid) {
                    return false;
                }
                scrollX = Math.abs(ev.getX() - touchX);
                scrollY = Math.abs(ev.getY() - touchY);
                if (scrollX > scrollY && scrollX > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    return true;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setRightThreshold(float rightThreshold) {
        this.rightThreshold = rightThreshold;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (scrollAble) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    public void setScrollAble(boolean scrollAble) {
        this.scrollAble = scrollAble;
    }
}
