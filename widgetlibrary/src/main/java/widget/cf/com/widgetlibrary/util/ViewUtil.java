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
            view.setBackgroundTintList(getColorStateList(color));
        }
    }

    public static void setImageTint(ImageView view, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setImageTintList(getColorStateList(color));
        }
    }

    public static void setImageTint(ImageView view, int selectColor, int unSelect) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setImageTintList(getColorStateList(selectColor, unSelect));
        }
    }

    public static ColorStateList getColorStateList(int color) {
        ColorStateList colorStateList = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int[][] states = new int[1][];
            states[0] = new int[]{};
            colorStateList = new ColorStateList(states, new int[]{color});
        }
        return colorStateList;
    }

    public static ColorStateList getColorStateList(int selectColor, int unSelect) {
        ColorStateList colorStateList = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int[] colors = new int[]{selectColor, unSelect};
            int[][] states = new int[2][];
            states[0] = new int[]{android.R.attr.state_selected};
            states[1] = new int[]{};
            colorStateList = new ColorStateList(states, colors);
        }
        return colorStateList;
    }
}
