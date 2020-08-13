package widget.cf.com.widgetlibrary.touchmenu;

import android.view.View;

public class TouchUtils {

    public static boolean isInView(View view, float x, float y) {
        return x > view.getX() && x < view.getX() + view.getWidth() && y > view.getY() && y < view.getY() + view.getHeight();
    }

}
