package widget.cf.com.widgetlibrary.util;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class StatusBarUtil {


    public static void setTranslucentStatus(Window window, boolean needOffset) {
        setTranslucentStatus(window, needOffset, false);
    }

    public static void setTranslucentStatus(Window window, boolean needOffset, boolean lightBar) {
        if (window == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if (lightBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                option = option | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            attributes.flags |= flagTranslucentStatus;
            window.setAttributes(attributes);
        }
        if (needOffset) {
            ViewGroup contentView = window.getDecorView().findViewById(android.R.id.content);
            if (contentView.getChildCount() > 0) {
                View view = contentView.getChildAt(0);
                if (view instanceof ViewGroup) {
                    contentView = (ViewGroup) view;
                }
                if (contentView.getChildCount() > 0) {
                    view = contentView.getChildAt(0);
                    if (view instanceof ViewGroup) {
                        contentView = (ViewGroup) view;
                    }
                }
            }
            contentView.setPadding(contentView.getPaddingLeft(), contentView.getPaddingTop() + getStatusBarHeight(window.getContext()), contentView.getPaddingRight(), contentView.getPaddingBottom());
        }
    }

    public static void setOffsetView(View view) {
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(view.getContext()), view.getPaddingRight(), view.getPaddingBottom());
    }

    public static void setStatusBarLightMode(Window window, boolean isLight) {
        int flag = window.getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isLight) {
            flag = flag | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        window.getDecorView().setSystemUiVisibility(flag);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

}

