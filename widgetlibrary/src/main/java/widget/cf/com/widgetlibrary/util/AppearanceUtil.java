package widget.cf.com.widgetlibrary.util;

import android.content.Context;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import widget.cf.com.widgetlibrary.AppearanceImageView;
import widget.cf.com.widgetlibrary.AppearanceTextView;


public class AppearanceUtil {

    public static void setDelegate(final AppCompatActivity activity) {
        LayoutInflaterCompat.setFactory2(LayoutInflater.from(activity), new LayoutInflater.Factory2() {

            @Override
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                return null;
            }

            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                AppCompatDelegate delegate = activity.getDelegate();
                View view = delegate.createView(parent, name, context, attrs);
                if (view == null) {
                    if (name.endsWith("AppearanceTextView")) {
                        view = new AppearanceTextView(context, attrs);
                    } else if (name.endsWith("AppearanceImageView")) {
                        view = new AppearanceImageView(context, attrs);
                    }
                }
                return view;
            }
        });
    }
}
