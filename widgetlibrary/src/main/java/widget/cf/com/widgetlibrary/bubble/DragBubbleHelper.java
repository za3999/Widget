package widget.cf.com.widgetlibrary.bubble;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.base.BaseCallBack;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class DragBubbleHelper {

    private static Bitmap[] bombBitmaps;

    static {
        int[] BOOM_ARRAY = {R.drawable.burst_1, R.drawable.burst_2, R.drawable.burst_3, R.drawable.burst_4,
                R.drawable.burst_5, R.drawable.burst_6, R.drawable.burst_7, R.drawable.burst_8, R.drawable.burst_9,
                R.drawable.burst_10, R.drawable.burst_11, R.drawable.burst_12, R.drawable.burst_13};
        bombBitmaps = new Bitmap[BOOM_ARRAY.length];
        for (int i = 0; i < BOOM_ARRAY.length; i++) {
            bombBitmaps[i] = BitmapFactory.decodeResource(ApplicationUtil.getResources(), BOOM_ARRAY[i]);
        }
    }

    public static Bitmap[] getBombBitmaps() {
        return bombBitmaps;
    }

    public static void bindDragView(View view, int color, BaseCallBack.CallBack1<Boolean> onDragResultListener) {
        view.setOnTouchListener((v, event) -> {
            startDragBubbleView(v, color, onDragResultListener);
            return true;
        });
    }

    public static boolean startDragBubbleView(View view, int color, BaseCallBack.CallBack1<Boolean> onResultListener) {
        DragFrameLayout dragBubbleFrameLayout = view.getRootView().findViewById(R.id.drag_bubble_Layout);
        if (dragBubbleFrameLayout == null) {
            return false;
        }

        int location[] = new int[2];
        dragBubbleFrameLayout.getLocationInWindow(location);
        return dragBubbleFrameLayout.startDragBubbleView(view, location[1], color, onResultListener);
    }

    public static void startBomb(View view) {

    }

    public static void forceStopDragBubble(View view) {
        DragFrameLayout dragBubbleFrameLayout = view.getRootView().findViewById(R.id.drag_bubble_Layout);
        if (dragBubbleFrameLayout == null) {
            return;
        }
        dragBubbleFrameLayout.forceStopDragBubble();
    }

    public static boolean isDragBubbleView(View view) {
        DragFrameLayout dragBubbleFrameLayout = view.getRootView().findViewById(R.id.drag_bubble_Layout);
        if (dragBubbleFrameLayout == null) {
            return false;
        }

        return dragBubbleFrameLayout.getDragView() == view;
    }

    public static void updateLocation(View view) {
        DragFrameLayout dragBubbleFrameLayout = view.getRootView().findViewById(R.id.drag_bubble_Layout);
        if (dragBubbleFrameLayout == null) {
            return;
        }

        int location[] = new int[2];
        dragBubbleFrameLayout.getLocationInWindow(location);
        ApplicationUtil.runOnMainThread(() -> dragBubbleFrameLayout.updateLocation(location[1]), 100);
    }

    public static abstract class BubbleDataMonitor<T> implements BaseCallBack.CallBack1<Boolean> {

        T data;

        public void setData(T t) {
            this.data = t;
        }

        public T getData() {
            return data;
        }
    }
}
