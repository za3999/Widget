package widget.cf.com.widget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import widget.cf.com.widgetlibrary.ColorPickerView;
import widget.cf.com.widgetlibrary.appearance.AppearanceManager;
import widget.cf.com.widgetlibrary.appearance.AppearanceUtil;
import widget.cf.com.widgetlibrary.util.SPUtil;

public class ColorPickerActivity extends AppCompatActivity {

    private TextView tv;
    private ColorPickerView colorPickerView;
    private View mAppearanceIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppearanceUtil.setDelegate(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_pick_layout);
        colorPickerView = findViewById(R.id.color_pick_v);
        mAppearanceIv =findViewById(R.id.appearance_iv);
        mAppearanceIv.setSelected(true);
        tv = findViewById(R.id.tv_info);
        colorPickerView.setOnColorBackListener(colorProperty -> {
            tv.setText("R:" + colorProperty.r + "\tG:" + colorProperty.g + "\tB:" + colorProperty.b + "\t" + "\n" + colorProperty.getColorStr());
            tv.setBackgroundColor(colorProperty.getColor());
            SPUtil.put(ColorPickerActivity.this, "color", colorProperty.getColor());
            AppearanceManager.getInstance().notifyAppearanceChange();
        });
    }

    public void onTestClick(View view) {
        view.setSelected(!view.isSelected());
    }
}
