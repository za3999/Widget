package widget.cf.com.widgetlibrary.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LineItemDecoration extends RecyclerView.ItemDecoration {
    private int width;
    @ColorInt
    int Color;
    private Paint paint;

    public LineItemDecoration(int width, @ColorInt int color) {
        this.width = width;
        Color = color;
        paint = new Paint();
        paint.setColor(color);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, width);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        for (int i = 0; i < parent.getChildCount() - 1; i++) {
            if (i < parent.getChildCount()) {
                View childView = parent.getChildAt(i);
                c.drawRect(childView.getLeft(), childView.getBottom(), childView.getRight(), childView.getBottom() + width, paint);
            }
        }
    }
}
