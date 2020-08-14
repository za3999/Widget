package widget.cf.com.widgetlibrary.tintview;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.UiThread;

import java.util.Iterator;
import java.util.WeakHashMap;

public class TintColorManager {

    private static int color = Color.GREEN;
    private static WeakHashMap<TintColorChangeListener, Boolean> mTintColorListenerMap = new WeakHashMap<>();

    public static int getColor() {
        return color;
    }

    @UiThread
    public static void addRefreshColorThemeListener(TintColorChangeListener listener, boolean needNotifyListener) {
        mTintColorListenerMap.put(listener, Boolean.TRUE);
        if (needNotifyListener) {
            listener.onRefreshColor(color);
        }
    }

    @UiThread
    public static void removeRefreshColorThemeListener(TintColorChangeListener listener) {
        mTintColorListenerMap.remove(listener);
    }

    @UiThread
    public static void setTintColor(@ColorInt int color) {
        if (mTintColorListenerMap.size() > 0) {
            Iterator<TintColorChangeListener> listenerIterator = mTintColorListenerMap.keySet().iterator();
            while (listenerIterator.hasNext()) {
                TintColorChangeListener listener = listenerIterator.next();
                if (listener != null) {
                    listener.onRefreshColor(color);
                }
            }
        }
    }
}
