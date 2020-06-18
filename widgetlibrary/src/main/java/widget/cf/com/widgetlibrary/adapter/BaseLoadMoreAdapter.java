package widget.cf.com.widgetlibrary.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.List;

public abstract class BaseLoadMoreAdapter<T> extends BaseCommonAdapter<T> {
    private static final int TYPE_LOAD_MORE = Integer.MAX_VALUE;
    private boolean showLoadingMore;
    private int loadMoreRes;

    public abstract BaseViewHolder onCreateViewHolderWarp(@NonNull ViewGroup parent, int viewType);

    public BaseLoadMoreAdapter(int loadMoreRes) {
        this.loadMoreRes = loadMoreRes;
    }

    @Override
    public final BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (TYPE_LOAD_MORE == viewType) {
            return new DefaultViewHolder(createView(loadMoreRes, parent));
        } else {
            return onCreateViewHolderWarp(parent, viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoadingMore && position >= result.size()) {
            return TYPE_LOAD_MORE;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public int getItemCount() {
        if (showLoadingMore) {
            return super.getItemCount() + 1;
        }
        return super.getItemCount();
    }

    public void addData(boolean showLoadingMore, List<T> list) {
        this.showLoadingMore = showLoadingMore;
        super.addData(list);
    }

}
