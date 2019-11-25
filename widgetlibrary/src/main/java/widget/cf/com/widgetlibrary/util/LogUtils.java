package widget.cf.com.widgetlibrary.util;

import android.content.Context;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import widget.cf.com.widgetlibrary.BaseApplication;
import widget.cf.com.widgetlibrary.BuildConfig;

public class LogUtils {
    public static String tag = "mylog";

    public static boolean fileLog;

    static {
        fileLog = BaseApplication.getApplication()
                .getSharedPreferences("signal", Context.MODE_PRIVATE)
                .getBoolean("fileLog", BuildConfig.DEBUG);
    }

    public static void v(Object obj) {
        if (!BuildConfig.DEBUG) return;
        try {
            Log.v(tag, String.valueOf(obj));
        } catch (Exception e) {
        }
    }

    public static void v(String tag, Object obj) {
        if (!BuildConfig.DEBUG) return;
        try {
            Log.v(tag, String.valueOf(obj));
        } catch (Exception e) {
        }
    }

    public static void d(Object obj) {
        if (!BuildConfig.DEBUG) return;
        try {
            Log.d(tag, String.valueOf(obj));
        } catch (Exception e) {
        }
    }

    public static void d(String tag, Object object) {
        if (!BuildConfig.DEBUG) return;
        try {
            Log.d(tag, String.valueOf(object));
        } catch (Exception e) {
        }
    }

    public static void d(String tag, String msg) {
        if (!BuildConfig.DEBUG) return;
        try {
            Log.d(tag, msg);
        } catch (Exception e) {
        }
    }

    public static void printStackTrace(String topTip) {
        if (!BuildConfig.DEBUG) return;
        try {
            if (topTip == null) topTip = "";
            Log.e(tag, topTip + "\n" + Log.getStackTraceString(new Throwable()));
        } catch (Exception e) {
        }
    }

    public static void dTask(String tag, String msg) {
        if (!BuildConfig.DEBUG) return;
        try {
            Log.d(tag, msg, new Exception(tag));
        } catch (Exception e) {
        }
    }

    public static void ex(Throwable e) {
        ex(tag, e);
    }

    public static void ex(String tag, Throwable e) {
        if (!BuildConfig.DEBUG) return;
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        try {
            Log.e(tag, writer.toString());
        } catch (Exception e2) {
        }
    }

    public static void w(String tag, String msg) {
        if (!BuildConfig.DEBUG) return;
        try {
            Log.w(tag, msg);
        } catch (Exception e) {
        }
    }

    public static void e(String tag, String msg) {
        if (!BuildConfig.DEBUG) return;
        try {
            Log.e(tag, msg);
        } catch (Exception e) {
        }
    }
}
