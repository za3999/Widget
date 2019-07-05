package widget.cf.com.widgetlibrary.animator;

import android.util.Property;
import android.util.TypedValue;
import android.widget.TextView;

public class TextSizeProperty extends Property<TextView, Float> {

    public TextSizeProperty() {
        super(Float.class, "textSize");
    }

    @Override
    public void set(TextView object, Float value) {
        object.setTextSize(TypedValue.COMPLEX_UNIT_PX, value);
    }

    @Override
    public Float get(TextView object) {
        return object.getTextSize();
    }
}
