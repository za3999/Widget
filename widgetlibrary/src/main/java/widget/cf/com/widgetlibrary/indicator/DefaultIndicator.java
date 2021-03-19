package widget.cf.com.widgetlibrary.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.adapter.BaseCommonAdapter;
import widget.cf.com.widgetlibrary.adapter.BaseViewHolder;


public class DefaultIndicator extends BaseIndicator<Pair<Integer, String>> {

    public DefaultIndicator(Context context) {
        super(context);
    }

    public DefaultIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public int getIndicatorTarget() {
        return R.id.target_view;
    }

    @Override
    public BaseCommonAdapter<Pair<Integer, String>> getDataAdapter() {

        return new BaseCommonAdapter<Pair<Integer, String>>() {

            @NonNull
            @Override
            public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                return new IndicatorHolder<Pair<Integer, String>>(createView(R.layout.tab_indicatior_item, parent)) {
                    TextView nameTv;
                    View lineView;

                    @Override
                    public void initView(View view) {
                        nameTv = view.findViewById(R.id.tv_name);
                        lineView = view.findViewById(R.id.line);
                    }

                    @Override
                    public void bindData(int position, Pair<Integer, String> data) {
                        nameTv.setText(data.second);
                        updateSelect();
                    }

                    @Override
                    public void updateSelect() {
                        boolean isSelect = getSelect().first == itemData.first;
                        nameTv.setSelected(isSelect);
                        boolean lineVisible = isSelect && !isIndicatorScroll();
                        lineView.setVisibility(lineVisible ? View.VISIBLE : View.GONE);
                    }
                };
            }
        };
    }
}
