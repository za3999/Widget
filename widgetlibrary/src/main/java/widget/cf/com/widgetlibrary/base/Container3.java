package widget.cf.com.widgetlibrary.base;

import android.util.Pair;

public class Container3<F, S, T> extends Pair<F, S> {

    public T third;

    public Container3(F first, S second, T third) {
        super(first, second);
        this.third = third;
    }
}
