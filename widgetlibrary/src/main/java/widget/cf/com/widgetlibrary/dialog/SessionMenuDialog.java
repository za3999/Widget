package widget.cf.com.widgetlibrary.dialog;

import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.adapter.BaseCommonAdapter;
import widget.cf.com.widgetlibrary.adapter.BaseViewHolder;
import widget.cf.com.widgetlibrary.adapter.DefaultViewHolder;
import widget.cf.com.widgetlibrary.adapter.MultiItem;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;
import widget.cf.com.widgetlibrary.util.ViewUtil;
import widget.cf.com.widgetlibrary.view.LineItemDecoration;

public class SessionMenuDialog extends BaseBlurDialog {

    private static final int LINE_WIDTH = ApplicationUtil.getIntDimension(R.dimen.dp_1);

    private RecyclerView mRecyclerView;
    private BaseCommonAdapter mAdapter = getAdapter();

    public SessionMenuDialog(View clickView) {
        super(clickView);
        setCancelable(true);
    }

    @Override
    public Point getAnimPoint() {
        return new Point(0, mContentView.getHeight());
    }

    @Override
    public int getTargetViewPadding() {
        return 0;
    }

    @Override
    public int getTargetBgResource() {
        return R.color.transparent;
    }

    @Override
    public RelativeLayout.LayoutParams getTargetLayoutParams() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mTargetRect.width(), mTargetRect.height());
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.leftMargin = getTargetLeftMargin();
        return layoutParams;
    }

    @Override
    public RelativeLayout.LayoutParams getContentLayoutParams() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getMenuWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = mTargetRect.width() / 3;
        layoutParams.bottomMargin = ApplicationUtil.getIntDimension(R.dimen.dp_8);
        layoutParams.topMargin = ApplicationUtil.getIntDimension(R.dimen.dp_100);
        layoutParams.addRule(RelativeLayout.ABOVE, mTargetView.getId());
        return layoutParams;
    }

    @Override
    public View getContentView() {
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRecyclerView.setBackgroundResource(R.drawable.white_radius_bg);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new LineItemDecoration(LINE_WIDTH, ApplicationUtil.getColor(R.color.gray_line)));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.addData(getMultiItems());
        return mRecyclerView;
    }

    private List<Menu> getMultiItems() {
        List<Menu> result = new ArrayList<>();
        Collections.addAll(result,
                new Menu(1, "Test1", R.drawable.edit_folder),
                new Menu(2, "Test2", R.drawable.edit_folder),
                new Menu(3, "Test3", R.drawable.edit_folder),
                new Menu(4, "Test4", R.drawable.edit_folder),
                new Menu(5, "Test5", R.drawable.edit_folder),
                new Menu(6, "Test6", R.drawable.edit_folder),
                new Menu(7, "Test7", R.drawable.edit_folder));
        return result;
    }

    private BaseCommonAdapter getAdapter() {
        BaseCommonAdapter adapter = new BaseCommonAdapter<MultiItem>() {

            @NonNull
            @Override
            public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new DefaultViewHolder<MultiItem<Menu>>(ViewUtil.createView(R.layout.blur_menu_layout, parent), true) {
                    TextView mName;
                    ImageView mImage;

                    @Override
                    public void initView(View view) {
                        mName = view.findViewById(R.id.name_tv);
                        mImage = view.findViewById(R.id.image_iv);
                    }

                    @Override
                    public void bindData(int position, MultiItem<Menu> menuMultiItem) {
                        mName.setText(itemData.getData().getName());
                        mImage.setImageResource(itemData.getData().getImageRes());
                    }
                };
            }

            @Override
            public void onItemClick(int position, View v, MultiItem item) {
                dismiss();
            }
        };
        return adapter;
    }


    public static class Menu {
        int key;
        String name;
        int imageRes;

        public Menu(int key, String name, int imageRes) {
            this.key = key;
            this.name = name;
            this.imageRes = imageRes;
        }

        public int getKey() {
            return key;
        }

        public String getName() {
            return name;
        }

        public int getImageRes() {
            return imageRes;
        }
    }


}
