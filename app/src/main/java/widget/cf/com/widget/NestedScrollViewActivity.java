package widget.cf.com.widget;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import widget.cf.com.widget.databinding.NestedLayoutBinding;

public class NestedScrollViewActivity extends AppCompatActivity {

    private NestedLayoutBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NestedLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

}
