package widget.cf.com.widgetlibrary.util;

import android.app.Application;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;

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

    public static int dp(float value) {
        if (value <= 0) {
            return (int) value;
        }
        return (int) Math.ceil(getResources().getDisplayMetrics().density * value);
    }

    public static int getScreenWidth(){
        return getResources().getDisplayMetrics().widthPixels;
    }
}
