package widget.cf.com.widgetlibrary.util;

import android.app.Application;
import android.content.res.Resources;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.TypedValue;

import widget.cf.com.widgetlibrary.BaseApplication;


public class ApplicationUtil {

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static HandlerThread mHandlerThread;
    private static Handler mBgHandler;

    static {
        mHandlerThread = new HandlerThread("application_bg_thread");
        mHandlerThread.start();
        mBgHandler = new Handler(mHandlerThread.getLooper());
    }

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

    public static void runOnMainThread(Runnable action) {
        if (isMainThread()) {
            action.run();
        } else {
            mainHandler.post(action);
        }
    }

    public static void runOnMainThread(Runnable action, long delay) {
        if (delay == 0) {
            runOnMainThread(action);
        } else {
            mainHandler.postDelayed(action, delay);
        }
    }

    public static Handler getBgHandler() {
        return mBgHandler;
    }

    public static Handler getMainHandler() {
        return mainHandler;
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        mainHandler.removeCallbacks(runnable);
    }

    public static void runOnBgThread(Runnable action) {
        if (isMainThread()) {
            mBgHandler.post(action);
        } else {
            action.run();
        }
    }

    public static boolean isAppForeground() {
        return BaseApplication.isAppForeground;
    }

}
