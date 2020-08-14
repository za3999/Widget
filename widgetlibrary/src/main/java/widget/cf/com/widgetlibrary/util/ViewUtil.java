package widget.cf.com.widgetlibrary.util;

import android.content.res.ColorStateList;
import android.os.Build;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewUtil {

    public static final int TINT_NON = Integer.MAX_VALUE;

    public static void setBold(TextView textView) {
        if (textView == null) {
            return;
        }

        if (textView.getPaint() != null) {
            textView.getPaint().setFakeBoldText(true);
        }
    }

    public static void setViewBackgroundTint(View view, int color) {
        if (color == TINT_NON) {
            view.setBackgroundTintList(null);
        } else {
            view.setBackgroundTintList(ColorStateList.valueOf(color));
        }
    }

    public static void setImageTint(ImageView view, int color) {
        if (color == TINT_NON) {
            view.setImageTintList(null);
        } else {
            view.setImageTintList(ColorStateList.valueOf(color));
        }
    }

    public static void setCheckBoxTint(CheckBox checkBox, int color) {
        if (color == TINT_NON) {
            checkBox.setButtonTintList(null);
        } else {
            checkBox.setButtonTintList(createCheckedColorStateList(color));
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

    private static ColorStateList createCheckedColorStateList(int checkedColor) {
        return new ColorStateList(new int[][]{
                new int[]{android.R.attr.state_checked}
                , new int[]{-android.R.attr.state_checked}
        }
                , new int[]{checkedColor, checkedColor}
        );
    }
}
