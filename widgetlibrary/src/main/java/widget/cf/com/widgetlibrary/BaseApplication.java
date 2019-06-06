package widget.cf.com.widgetlibrary;

import android.app.Application;

import widget.cf.com.widgetlibrary.emoji.EmojiHelper;

public class BaseApplication extends Application {

    private static BaseApplication mInstamce;

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                EmojiHelper.loadEmoji();
            }
        }).start();
        mInstamce = this;
    }

    public static BaseApplication getApplication() {
        return mInstamce;
    }
}
