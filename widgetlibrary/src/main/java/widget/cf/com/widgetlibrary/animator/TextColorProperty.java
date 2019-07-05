package widget.cf.com.widgetlibrary.animator;

import android.util.Property;
import android.widget.TextView;

public class TextColorProperty extends Property<TextView, Integer> {

    public TextColorProperty() {
        super(Integer.class, "textColor");
    }

    @Override
    public void set(TextView object, Integer value) {
        object.setTextColor(value);
    }

    @Override
    public Integer get(TextView object) {
        return object.getCurrentTextColor();
    }
}
