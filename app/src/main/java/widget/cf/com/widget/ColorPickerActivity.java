package widget.cf.com.widget;

import android.os.Bundle;
import android.view.View;

import widget.cf.com.widget.databinding.ColorPickLayoutBinding;
import widget.cf.com.widgetlibrary.appearance.AppearanceManager;
import widget.cf.com.widgetlibrary.appearance.AppearanceUtil;
import widget.cf.com.widgetlibrary.base.BaseActivity;
import widget.cf.com.widgetlibrary.util.SPUtil;

public class ColorPickerActivity extends BaseActivity {

    private ColorPickLayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppearanceUtil.setDelegate(this);
        super.onCreate(savedInstanceState);
        binding = ColorPickLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.appearanceIv.setSelected(true);
        binding.colorPickV.setOnColorBackListener(colorProperty -> {
            binding.tvInfo.setText("R:" + colorProperty.r + "\tG:" + colorProperty.g + "\tB:" + colorProperty.b + "\t" + "\n" + colorProperty.getColorStr());
            binding.tvInfo.setBackgroundColor(colorProperty.getColor());
            SPUtil.put(ColorPickerActivity.this, "color", colorProperty.getColor());
            AppearanceManager.getInstance().notifyAppearanceChange();
        });
    }

    public void onTestClick(View view) {
        view.setSelected(!view.isSelected());
    }
}
