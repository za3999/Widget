package widget.cf.com.widgetlibrary.adapter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

import widget.cf.com.widgetlibrary.base.BaseCallBack;

public abstract class BaseCommonAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    protected List<T> result = new ArrayList();
    protected BaseCallBack.CallBack3<Integer, View, T> mItemClickListener;
    protected BaseCallBack.CallBack3<Integer, View, T> mItemLongClickListener;

    public void setItemClickListener(BaseCallBack.CallBack3<Integer, View, T> itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(BaseCallBack.CallBack3<Integer, View, T> itemLongClickListener) {
        this.mItemLongClickListener = itemLongClickListener;
    }

    @Override
    @CallSuper
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBindData(position, result.get(position));
        holder.setItemClick((BaseCallBack.CallBack2<View, T>) (view, t) -> onItemClick(getPosition(t), view, t));
        holder.setItemLongClick((BaseCallBack.CallBack2<View, T>) (View view, T t) -> onItemLongClick(getPosition(t), view, t));
    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (result.get(position) instanceof MultiItem) {
            return ((MultiItem) result.get(position)).getItemType();
        }
        return 0;
    }

    public T getItem(int position) {
        if (position < result.size())
            return result.get(position);
        else {
            return null;
        }
    }

    public void setData(List<T> list) {
        result.clear();
        if (list != null && !list.isEmpty()) {
            result.addAll(list);
        }
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return result;
    }

    public View createView(int layoutId, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }

    public void onItemClick(int position, View v, T item) {
        BaseCallBack.onCallBack(mItemClickListener, position, v, item);
    }

    public void onItemLongClick(int position, View v, T item) {
        BaseCallBack.onCallBack(mItemLongClickListener, position, v, item);
    }

    private int getPosition(T t) {
        int position = 0;
        int size = result.size();
        for (int i = 0; i < size; i++) {
            if (result.get(i) == t) {
                position = i;
                break;
            }
        }
        return position;
    }
}