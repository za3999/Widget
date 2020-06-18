package widget.cf.com.widgetlibrary.appearance;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.LayoutInflaterCompat;

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
