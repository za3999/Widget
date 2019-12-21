package widget.cf.com.widgetlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import widget.cf.com.widgetlibrary.base.BaseCallBack;


public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener {
    private BaseCallBack.CallBack2<View, T> itemClick;
    private BaseCallBack.CallBack2<View, T> itemLongClick;
    protected T itemData;

    public BaseViewHolder(View view) {
        this(view, true);
    }

    public BaseViewHolder(View view, boolean itemClickEnable) {
        super(view);
        initView(view);
        if (itemClickEnable) {
            itemView.setOnClickListener(v -> onItemClick(itemView, itemData));
        }
        itemView.setOnLongClickListener(v -> {
            onItemLongClick(v, itemData);
            return false;
        });
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

}
