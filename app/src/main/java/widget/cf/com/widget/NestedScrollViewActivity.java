package widget.cf.com.widget;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

public class NestedScrollViewActivity extends AppCompatActivity {

    NestedScrollView mNestedScrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nested_layout);
        mNestedScrollView = findViewById(R.id.nestedScrollView);
    }

}
