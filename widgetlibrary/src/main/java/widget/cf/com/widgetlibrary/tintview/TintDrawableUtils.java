package widget.cf.com.widgetlibrary.tintview;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class TintDrawableUtils {

    public static GradientDrawable getCircleDrawable(int stroke) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setStroke(stroke, Color.WHITE);
        drawable.setColor(TintColorManager.getColor());
        return drawable;
    }

    public static GradientDrawable getCircleDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(TintColorManager.getColor());
        return drawable;
    }

    public static GradientDrawable getDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(TintColorManager.getColor());
        return drawable;
    }

    public static GradientDrawable getDrawable(int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(radius);
        drawable.setColor(TintColorManager.getColor());
        return drawable;
    }

}
