package widget.cf.com.widgetlibrary.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.tintview.TintColorManager;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;
import widget.cf.com.widgetlibrary.util.ColorUtils;


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
    public IndicatorAdapter<Pair<Integer, String>> getDataAdapter() {

        return new IndicatorAdapter<Pair<Integer, String>>() {

            @NonNull
            @Override
            public IndicatorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

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
                        boolean isSelect = getSelect() == itemData;
                        nameTv.setSelected(isSelect);
                        boolean lineVisible = isSelect && !isIndicatorScroll();
                        lineView.setVisibility(lineVisible ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void updateIndicatorColor(float offset) {
                        int selectColor = TintColorManager.getColor();
                        int unSelectColor = ApplicationUtil.getColor(R.color.prompt_text);
                        int color = ColorUtils.blend(selectColor, unSelectColor, offset);
                        nameTv.setTextColor(color);
                    }
                };
            }
        };
    }
}
