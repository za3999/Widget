package widget.cf.com.widgetlibrary.bubble;

import android.view.View;


import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.base.BaseCallBack;

public class DragBubbleHelper {

    public static boolean startDragBubbleView(View view, int color, BaseCallBack.CallBack1<Boolean> onResultListener) {
        DragBubbleFrameLayout dragBubbleFrameLayout = view.getRootView().findViewById(R.id.drag_bubble_Layout);
        if (dragBubbleFrameLayout == null) {
            return false;
        }
        int location[] = new int[2];
        dragBubbleFrameLayout.getLocationInWindow(location);
        return dragBubbleFrameLayout.startDragBubbleView(view, location[1], color, onResultListener);
    }

    public static void forceStopDragBubble(View view) {
        DragBubbleFrameLayout dragBubbleFrameLayout = view.getRootView().findViewById(R.id.drag_bubble_Layout);
        if (dragBubbleFrameLayout != null) {
            dragBubbleFrameLayout.forceStopDragBubble(view);
        }
    }

    public static boolean isDragBubbleView(View view) {
        DragBubbleFrameLayout dragBubbleFrameLayout = view.getRootView().findViewById(R.id.drag_bubble_Layout);
        if (dragBubbleFrameLayout == null) {
            return false;
        }
        return dragBubbleFrameLayout.getDragView() == view;
    }
}
