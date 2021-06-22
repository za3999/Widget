package widget.cf.com.widget;

import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;

import widget.cf.com.widget.databinding.DragBubbleBinding;
import widget.cf.com.widgetlibrary.base.BaseActivity;
import widget.cf.com.widgetlibrary.bubble.DragBubbleHelper;

public class DragBubbleActivity extends BaseActivity {

    private DragBubbleBinding bubbleBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bubbleBinding = DragBubbleBinding.inflate(getLayoutInflater());
        setContentView(bubbleBinding.getRoot());
        DragBubbleHelper.bindDragView(bubbleBinding.testView, ContextCompat.getColor(this, R.color.color_26b36d),
                reset -> bubbleBinding.testView.setVisibility(View.VISIBLE));
    }
}
