package widget.cf.com.widgetlibrary.language;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.Locale;

import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class MultiLanguageUtil {
    private static final String TAG = "MultiLanguageUtil";
    public static final String SETTING_LANGUAGE = "SETTING_LANGUAGE";
    private static MultiLanguageUtil mInstance;
    private Context mContext;

    public static MultiLanguageUtil getInstance() {
        if (mInstance == null) {
            synchronized (MultiLanguageUtil.class) {
                if (mInstance == null) {
                    mInstance = new MultiLanguageUtil(ApplicationUtil.getApplication());
                }
            }
        }
        return mInstance;
    }

    private MultiLanguageUtil(Context context) {
        this.mContext = context;
    }

    public static Locale getLanguageLocale() {
        int languageType = getLanguageType();
        if (languageType == LanguageType.LANGUAGE_EN) {
            return Locale.ENGLISH;
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
            return Locale.SIMPLIFIED_CHINESE;
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_TRADITIONAL) {
            return Locale.TRADITIONAL_CHINESE;
        }
        return getSysLocale();
    }

    public static Locale getSysLocale() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = ApplicationUtil.getResources().getSystem().getConfiguration().getLocales().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return locale;
    }

    public String getLanguageName() {
        int languageType = getLanguageType();
        if (languageType == LanguageType.LANGUAGE_EN) {
            return "英文";
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
            return "简体中文";
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_TRADITIONAL) {
            return "繁体中文";
        }
        return "跟随系统";
    }


    public static void attachBaseContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createConfigurationResources(context);
        } else {
            getInstance().setConfiguration();
        }
    }

    private void setConfiguration() {
        Locale targetLocale = getLanguageLocale();
        Configuration configuration = mContext.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(targetLocale);
        } else {
            configuration.locale = targetLocale;
        }
        Resources resources = mContext.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
        ApplicationUtil.getResources().updateConfiguration(configuration, ApplicationUtil.getResources().getDisplayMetrics());
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static void createConfigurationResources(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = getInstance().getLanguageLocale();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        ApplicationUtil.getResources().updateConfiguration(configuration, ApplicationUtil.getResources().getDisplayMetrics());
        context.createConfigurationContext(configuration);
    }

    public static int getLanguageType() {
        SharedPreferences sharedPreferences = ApplicationUtil.getApplication().getSharedPreferences("name", Context.MODE_PRIVATE);
        int languageType = sharedPreferences.getInt(SETTING_LANGUAGE, 0);
        if (languageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
            return LanguageType.LANGUAGE_CHINESE_SIMPLIFIED;
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_TRADITIONAL) {
            return LanguageType.LANGUAGE_CHINESE_TRADITIONAL;
        } else if (languageType == LanguageType.LANGUAGE_FOLLOW_SYSTEM) {
            return LanguageType.LANGUAGE_FOLLOW_SYSTEM;
        }
        return languageType;
    }
}
