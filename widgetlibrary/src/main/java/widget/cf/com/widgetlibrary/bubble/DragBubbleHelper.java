package widget.cf.com.widgetlibrary.bubble;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.base.BaseCallBack;
import widget.cf.com.widgetlibrary.util.LogUtils;

public class DragBubbleHelper {

    private static View testView;

    public static boolean startDragBubbleView(View view, int color, BaseCallBack.CallBack1<Boolean> onResultListener) {
        DragBubbleFrameLayout dragBubbleFrameLayout = view.getRootView().findViewById(R.id.drag_bubble_Layout);
        if (dragBubbleFrameLayout == null) {
            return false;
        }
        int location[] = new int[2];
        dragBubbleFrameLayout.getLocationInWindow(location);
        testLocation(view, dragBubbleFrameLayout);
        return dragBubbleFrameLayout.startDragBubbleView(view, location[1], color, onResultListener);
    }

    private static void testLocation(View view, DragBubbleFrameLayout dragBubbleFrameLayout) {
        if (testView == null) {
            testView = new View(view.getContext());
            testView.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.black));
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(view.getWidth(), view.getHeight());
            testView.setLayoutParams(layoutParams);
            dragBubbleFrameLayout.addView(testView);
        } else {
            LogUtils.d("caifu", "testView:" + testView.getTop() + "," + testView.getBottom());
        }
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
