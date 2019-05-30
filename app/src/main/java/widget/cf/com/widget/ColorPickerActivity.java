package widget.cf.com.widget;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import widget.cf.com.widgetlibrary.ColorPickerViewTest;

public class ColorPickerActivity extends AppCompatActivity {

    private TextView tv;
    private ColorPickerViewTest colorPickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_pick_layout);
        colorPickerView = findViewById(R.id.color_pick_v);
        tv = findViewById(R.id.tv_info);
        colorPickerView.setOnColorBackListener(new ColorPickerViewTest.OnColorBackListener() {
            @Override
            public void onColorBack(ColorPickerViewTest.ColorProperty colorProperty) {
                tv.setText("R:" + colorProperty.r + "\tG:" + colorProperty.g + "\tB:" + colorProperty.b + "\t" + "\n" + colorProperty.getColorStr());
                tv.setBackgroundColor(colorProperty.getColor());
            }
        });
    }
}
