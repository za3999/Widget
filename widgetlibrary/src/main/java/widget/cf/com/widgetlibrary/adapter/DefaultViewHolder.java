package widget.cf.com.widgetlibrary.adapter;

import android.view.View;

public class DefaultViewHolder<T> extends BaseViewHolder<T> {

    public DefaultViewHolder(View view) {
        this(view, false);
    }

    public DefaultViewHolder(View view, boolean itemClickEnable) {
        super(view, itemClickEnable);
    }

    public DefaultViewHolder(View view, boolean itemClickEnable, boolean itemLongClickEnable) {
        super(view, itemClickEnable, itemLongClickEnable);
    }

    @Override
    public void bindData(int position,T t) {

    }

    @Override
    public void initView(View view) {

    }
}
