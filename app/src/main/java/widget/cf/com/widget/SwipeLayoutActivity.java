package widget.cf.com.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import widget.cf.com.widget.databinding.ActivitySwipelayoutBinding;
import widget.cf.com.widgetlibrary.SwipeLayout;
import widget.cf.com.widgetlibrary.SwipeLayoutManager;
import widget.cf.com.widgetlibrary.base.BaseActivity;

public class SwipeLayoutActivity extends BaseActivity {

    private ActivitySwipelayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySwipelayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.recyclerView.setAdapter(new ListAdapter(this, new SwipeLayoutManager()));
    }

    private static class ListAdapter extends RecyclerView.Adapter<DataHolder> implements HolderClickListener {

        private Context context;
        private SwipeLayoutManager manager;
        private ArrayList<String> dataList = new ArrayList<>();

        public ListAdapter(Context context, SwipeLayoutManager manager) {
            this.context = context;
            this.manager = manager;
            for (int i = 0; i < 100; i++) {
                dataList.add(i + "");
            }
        }

        @Override
        public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.swipe_layout_item, parent, false);
            SwipeLayout swipeLayout = view.findViewById(R.id.swipeLayout);
            if (swipeLayout != null) {
                swipeLayout.setSwipeManager(manager);
            }
            return new DataHolder(view, this);
        }

        @Override
        public void onBindViewHolder(@NonNull DataHolder holder, int position) {
            holder.binding("数据:" + position);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        @Override
        public void onClick(View v, int position) {
            manager.closeCurrentLayout();
            manager.closeCurrentLayout();
            switch (v.getId()) {
                case R.id.tv_pin:
                    Toast.makeText(v.getContext(), "置顶:" + position, Toast.LENGTH_LONG).show();
                    String text = dataList.remove(position);
                    dataList.add(0, text);
                    notifyItemMoved(position, 0);
                    break;
                case R.id.tv_delete:
                    Toast.makeText(v.getContext(), "删除:" + position, Toast.LENGTH_LONG).show();
//                    dataList.remove(position);
                    notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

    private static class DataHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvPin;
        TextView tvDelete;
        TextView tvMessage;
        HolderClickListener clickListener;

        public DataHolder(View itemView, HolderClickListener listener) {
            super(itemView);
            tvPin = itemView.findViewById(R.id.tv_pin);
            tvDelete = itemView.findViewById(R.id.tv_delete);
            tvMessage = itemView.findViewById(R.id.tv_message);
            clickListener = listener;
            tvPin.setOnClickListener(this);
            tvDelete.setOnClickListener(this);
        }

        public void binding(String text) {
            tvMessage.setText(text);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }

    private interface HolderClickListener {
        void onClick(View v, int position);
    }

}
