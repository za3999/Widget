package widget.cf.com.widgetlibrary.util;

import android.content.res.ColorStateList;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewUtil {

    public static void setBold(TextView textView) {
        if (textView == null) {
            return;
        }

        if (textView.getPaint() != null) {
            textView.getPaint().setFakeBoldText(true);
        }
    }

    public static void setViewBackgroundTint(View view, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (color == -1) {
                view.setBackgroundTintList(null);
            } else {
                int[][] states = new int[1][];
                states[0] = new int[]{};
                ColorStateList colorStateList = new ColorStateList(states, new int[]{color});
                view.setBackgroundTintList(colorStateList);
            }
        }
    }

    public static void setImageTint(ImageView view, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (color == -1) {
                view.setImageTintList(null);
            } else {
                int[][] states = new int[1][];
                states[0] = new int[]{};
                ColorStateList colorStateList = new ColorStateList(states, new int[]{color});
                view.setImageTintList(colorStateList);
            }
        }
    }
}
