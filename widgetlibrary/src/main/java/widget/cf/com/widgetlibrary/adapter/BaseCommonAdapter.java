package widget.cf.com.widgetlibrary.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.base.BaseCallBack;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public abstract class BaseCommonAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    protected List<T> result = new ArrayList();
    protected BaseCallBack.CallBack3<Integer, View, T> mItemClickListener;
    protected BaseCallBack.CallBack3<Integer, View, T> mItemLongClickListener;
    protected RecyclerView mRecyclerView;

    public void setItemClickListener(BaseCallBack.CallBack3<Integer, View, T> itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(BaseCallBack.CallBack3<Integer, View, T> itemLongClickListener) {
        this.mItemLongClickListener = itemLongClickListener;
    }

    public void clear() {
        result.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    public void detachedAllFromWindow() {
        int count = mRecyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            BaseViewHolder holder = (BaseViewHolder) mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(i));
            holder.onDetachedFromWindow();
        }
    }

    public void runOnHolders(BaseCallBack.CallBack1<BaseViewHolder> callBack) {
        int count = mRecyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            BaseViewHolder holder = (BaseViewHolder) mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(i));
            BaseCallBack.onCallBack(callBack, holder);
        }
    }

    @CallSuper
    @Override
    public void onViewAttachedToWindow(@NonNull BaseViewHolder holder) {
        holder.onAttachedToWindow();
    }

    @CallSuper
    @Override
    public void onViewDetachedFromWindow(@NonNull BaseViewHolder holder) {
        holder.onDetachedFromWindow();
    }

    @Override
    @CallSuper
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBindData(position, result.size() > position ? result.get(position) : null);
        holder.setItemClick((BaseCallBack.CallBack2<View, T>) (view, t) -> onItemClick(getPosition(t), view, t));
        holder.setItemLongClick((BaseCallBack.CallBack2<View, T>) (View view, T t) -> onItemLongClick(getPosition(t), view, t));
    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < result.size() && result.get(position) instanceof MultiItem) {
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

    public void addData(List<T> list) {
        result.addAll(list);
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return result;
    }

    public View createView(int layoutId, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }

    public void notifyDataSetChangedSafe() {
        if (mRecyclerView != null) {
            if (!mRecyclerView.isComputingLayout()) {
                notifyDataSetChanged();
            } else {
                mRecyclerView.post(() -> notifyDataSetChangedSafe());
            }
        }
    }

    public int getMessageTextWidth(View itemView, int viewId) {
        int width = View.MeasureSpec.makeMeasureSpec(mRecyclerView.getWidth(), View.MeasureSpec.EXACTLY);
        int height = View.MeasureSpec.makeMeasureSpec(ApplicationUtil.getIntDimension(R.dimen.dp_1), View.MeasureSpec.EXACTLY);
        itemView.measure(width, height);
        TextView messageTv = itemView.findViewById(viewId);
        return messageTv.getMeasuredWidth() - messageTv.getCompoundPaddingLeft() - messageTv.getCompoundPaddingRight();
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
