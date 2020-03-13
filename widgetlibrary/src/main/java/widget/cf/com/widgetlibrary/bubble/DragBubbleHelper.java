package widget.cf.com.widgetlibrary.bubble;

import android.view.View;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.base.BaseCallBack;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class DragBubbleHelper {

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
