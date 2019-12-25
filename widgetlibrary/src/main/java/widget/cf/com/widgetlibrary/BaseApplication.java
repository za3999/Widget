package widget.cf.com.widgetlibrary;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import widget.cf.com.widgetlibrary.emoji.EmojiHelper;
import widget.cf.com.widgetlibrary.language.MultiLanguageUtil;

public class BaseApplication extends Application {

    private static BaseApplication mInstance;
    public static boolean isAppForeground = false;
    private int count = 0, createCount;

    private ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            createCount++;
        }

        @Override
        public void onActivityStarted(Activity activity) {
            count++;
            if (count == 1) {
                if (!isAppForeground) {
                    onAppForegroundChange(true);
                    isAppForeground = true;
                }
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            count--;
            if (count == 0) {
                if (isAppForeground) {
                    onAppForegroundChange(false);
                    isAppForeground = false;
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            createCount--;
            if (createCount == 0) {
                onAppShutdown();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(() -> EmojiHelper.loadEmoji()).start();
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        mInstance = this;
        MultiLanguageUtil.getInstance().attachBaseContext(this);
    }

    public static BaseApplication getApplication() {
        return mInstance;
    }

    public void onAppForegroundChange(boolean isAppForeground) {

    }

    public void onAppShutdown() {
    }
}
