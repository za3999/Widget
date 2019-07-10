package widget.cf.com.widgetlibrary.animator;

import android.animation.TypeEvaluator;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;

public class ViewSize {

    public static class SizeProperty extends Property<View, Size> {

        public SizeProperty() {
            super(Size.class, "size");
        }

        @Override
        public void set(View object, Size value) {
            ViewGroup.LayoutParams layoutParams = object.getLayoutParams();
            layoutParams.width = value.width;
            layoutParams.height = value.height;
            object.setLayoutParams(layoutParams);
        }

        @Override
        public Size get(View object) {
            ViewGroup.LayoutParams layoutParams = object.getLayoutParams();
            return new Size(layoutParams.width, layoutParams.height);
        }
    }

    public static class SizeTypeEvaluator implements TypeEvaluator<Size> {

        @Override
        public ViewSize.Size evaluate(float fraction, ViewSize.Size startValue, ViewSize.Size endValue) {
            int width = (int) (startValue.width + (endValue.width - startValue.width) * fraction);
            int height = (int) (startValue.height + (endValue.height - startValue.height) * fraction);
            return new ViewSize.Size(width, height);
        }
    }

    public static class Size {
        int width;
        int height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}
