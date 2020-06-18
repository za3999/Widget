package widget.cf.com.widgetlibrary.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;


public class PartScrollViewPager extends ViewPager {

    private boolean isScroll;

    public PartScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PartScrollViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isScroll) {
            float touchX = ev.getX();
            if (touchX > getWidth() * 0.8) {
                return false;
            }
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isScroll) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    public void setScroll(boolean scroll) {
        isScroll = scroll;
    }
}
