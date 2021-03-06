package widget.cf.com.widgetlibrary;


/**
 * 对SwipeLayout的管理
 */
public class SwipeLayoutManager {

    private SwipeLayout currentLayout; //用来记录当前打开的SwipeLayout

    public void setSwipeLayout(SwipeLayout layout) {
        this.currentLayout = layout;
    }

    /**
     * 清空当前所记录的已经打开的layout
     */
    public void clearCurrentLayout() {
        currentLayout = null;
    }

    /**
     * 关闭当前已经打开的SwipeLayout
     */
    public void closeCurrentLayout() {
        if (currentLayout != null) {
            currentLayout.close();
        }
    }

    /**
     * 判断当前是否应该能够滑动，如果没有打开的，则可以滑动。
     * 如果有打开的，则判断打开的layout和当前按下的layout是否是同一个,是同一个，可以滑动
     *
     * @return
     */
    public boolean isShouldSwipe(SwipeLayout swipeLayout) {
        if (currentLayout == null) {
            return true;
        } else {
            //判断打开的layout和当前按下的layout是否是同一个
            return currentLayout == swipeLayout;
        }
    }
}
