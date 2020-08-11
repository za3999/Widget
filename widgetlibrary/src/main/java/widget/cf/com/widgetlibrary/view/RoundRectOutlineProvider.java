package widget.cf.com.widgetlibrary.view;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

public class RoundRectOutlineProvider extends ViewOutlineProvider {

    private float radius;

    public RoundRectOutlineProvider() {
    }

    public RoundRectOutlineProvider(float radius) {
        this.radius = radius;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), getRadius(view));
    }

    private float getRadius(View view) {
        if (radius != 0f) {
            return radius;
        }
        return Math.min(view.getWidth(), view.getHeight()) / 2;
    }
}
