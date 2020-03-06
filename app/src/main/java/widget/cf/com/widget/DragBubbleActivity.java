package widget.cf.com.widget;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import widget.cf.com.widgetlibrary.base.BaseActivity;
import widget.cf.com.widgetlibrary.bubble.DragBubbleHelper;

public class DragBubbleActivity extends BaseActivity {

    private TextView mText;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drag_bubble);
        mText = findViewById(R.id.test_view);
        mText.setOnTouchListener((v, event) -> {
            boolean success = DragBubbleHelper.startDragBubbleView(v, ContextCompat.getColor(this, R.color.color_26b36d), reset -> {
                v.setVisibility(View.VISIBLE);
            });
            if (success) {
                v.setVisibility(View.INVISIBLE);
            }
            return true;
        });
    }
}
