package widget.cf.com.widget;

import android.os.Bundle;

import androidx.annotation.Nullable;

import widget.cf.com.widget.databinding.TestLayoutBinding;
import widget.cf.com.widgetlibrary.base.BaseActivity;

public class TestActivity extends BaseActivity {
    TestLayoutBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TestLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
