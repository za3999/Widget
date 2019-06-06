package widget.cf.com.widgetlibrary.util;

import android.app.Application;
import android.content.res.Resources;
import android.os.Looper;
import android.util.TypedValue;

import widget.cf.com.widgetlibrary.BaseApplication;


public class ApplicationUtil {

    public static Application getApplication() {
        return BaseApplication.getApplication();
    }

    public static Resources getResources() {
        return BaseApplication.getApplication().getResources();
    }

    public static boolean isMainThread() {
        return Looper.myLooper() != null && Looper.myLooper() == Looper.getMainLooper();
    }

    public static String getResString(int resId) {
        return getResources().getString(resId);
    }

    public static int getIntDimension(int resId) {
        return (int) getResources().getDimension(resId);
    }

    public static int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, ApplicationUtil.getResources().getDisplayMetrics());
    }
    public static int sp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, ApplicationUtil.getResources().getDisplayMetrics());
    }

    public static int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }
}
