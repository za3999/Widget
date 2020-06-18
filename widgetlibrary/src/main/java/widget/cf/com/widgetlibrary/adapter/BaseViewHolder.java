package widget.cf.com.widgetlibrary.adapter;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import widget.cf.com.widgetlibrary.base.BaseCallBack;


public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener {
    private BaseCallBack.CallBack2<View, T> itemClick;
    private BaseCallBack.CallBack2<View, T> itemLongClick;
    protected T itemData;

    public BaseViewHolder(View view) {
        this(view, true);
    }

    public BaseViewHolder(View view, boolean itemClickEnable) {
        this(view, itemClickEnable, true);
    }

    public BaseViewHolder(View view, boolean itemClickEnable, boolean itemLongClickEnable) {
        super(view);
        initView(view);
        if (itemClickEnable) {
            itemView.setOnClickListener(v -> onItemClick(itemView, itemData));
        }
        if (itemLongClickEnable) {
            itemView.setOnLongClickListener(v -> {
                onItemLongClick(v, itemData);
                return true;
            });
        }
    }

    public void setItemClick(BaseCallBack.CallBack2<View, T> itemClick) {
        this.itemClick = itemClick;
    }

    public void setItemLongClick(BaseCallBack.CallBack2<View, T> itemLongClick) {
        this.itemLongClick = itemLongClick;
    }

    public Context getContext() {
        return itemView.getContext();
    }

    protected void onBindData(int position, T data) {
        this.itemData = data;
        bindData(position, data);
    }

    public T getItemData() {
        return itemData;
    }

    public abstract void bindData(int position, T t);

    public abstract void initView(View itemView);

    @Override
    public void onClick(View v) {
        onItemClick(v, getItemData());
    }

    public void onItemClick(View v, T t) {
        BaseCallBack.onCallBack(itemClick, v, t);
    }

    public void onItemLongClick(View v, T t) {
        BaseCallBack.onCallBack(itemLongClick, v, t);
    }

    public void onAttachedToWindow() {
    }

    public void onDetachedFromWindow() {
    }

}
