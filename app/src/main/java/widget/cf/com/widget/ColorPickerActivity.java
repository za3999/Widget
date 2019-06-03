package widget.cf.com.widget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import widget.cf.com.widgetlibrary.ColorPickerView;
import widget.cf.com.widgetlibrary.util.AppearanceUtil;
import widget.cf.com.widgetlibrary.util.SPUtil;

public class ColorPickerActivity extends AppCompatActivity {

    private TextView tv;
    private ColorPickerView colorPickerView;
    private ViewGroup testViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppearanceUtil.setDelegate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_pick_layout);
        colorPickerView = findViewById(R.id.color_pick_v);
        tv = findViewById(R.id.tv_info);
        testViewGroup = findViewById(R.id.test_ll);
        colorPickerView.setOnColorBackListener(new ColorPickerView.OnColorBackListener() {
            @Override
            public void onColorBack(ColorPickerView.ColorProperty colorProperty) {
                tv.setText("R:" + colorProperty.r + "\tG:" + colorProperty.g + "\tB:" + colorProperty.b + "\t" + "\n" + colorProperty.getColorStr());
                tv.setBackgroundColor(colorProperty.getColor());
                SPUtil.put(ColorPickerActivity.this, "color", colorProperty.getColor());
                View view = testViewGroup.getChildAt(0);
                testViewGroup.removeAllViews();
                testViewGroup.addView(view);
            }
        });
    }

    public void onTestClick(View view) {
        view.setSelected(!view.isSelected());
    }
}
