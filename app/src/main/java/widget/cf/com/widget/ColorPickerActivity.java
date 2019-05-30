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
            public void onColorBack(int a, int r, int g, int b) {
                tv.setText("R:" + r + "\tG:" + g + "\tB:" + b + "\t" + "\n" + colorPickerView.getStrColor());
                int color = Color.argb(a, r, g, b);
                tv.setBackgroundColor(color);
            }
        });
    }
}
