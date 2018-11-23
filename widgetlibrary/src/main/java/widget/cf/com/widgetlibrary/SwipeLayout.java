package widget.cf.com.widgetlibrary;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 自定义一个可以滑动删除listView的item布局
 */

public class SwipeLayout extends FrameLayout {

    private View contentView; //item内容区域的view
    private View deleteView; //delete区域的view
    private int contentHeight; ///item内容区域的高度
    private int contentWidth; //item内容区域的宽
    private int deleteHeight; //delete区域的高度
    private int deleteWidth;  //delete区域的宽度

    private ViewDragHelper mViewDragHelper; //可以对viewgroup中的子view进行拖拽

    private float downX, downY;
    SwipeLayoutManager manager;

    enum SwipeState {
        Open, Close;
    }

    private SwipeState currentState = SwipeState.Close; //当前默认是关闭状态

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setSwipeManager(SwipeLayoutManager manager) {
        this.manager = manager;
    }

    private void init() {
        mViewDragHelper = ViewDragHelper.create(this, callback);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        deleteView = getChildAt(1);
    }

    /**
     * 这个方法会在onMeasure执行完后执行，可以在该方法中获取给控件自己和子控件的宽高
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        contentHeight = contentView.getMeasuredHeight();
        contentWidth = contentView.getMeasuredWidth();
        deleteHeight = deleteView.getMeasuredHeight();
        deleteWidth = deleteView.getMeasuredWidth();
    }

    public void setOpen(boolean isOpen) {
        currentState = isOpen ? SwipeState.Open : SwipeState.Close;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (currentState == SwipeState.Close) {
            //摆放contentView
            contentView.layout(0, 0, contentWidth, contentHeight);
            //摆放deleteView
            deleteView.layout(contentView.getRight(), 0, contentView.getRight() + deleteWidth, deleteHeight);
        } else {
            contentView.layout(-deleteWidth, 0, contentWidth - deleteWidth, contentHeight);
            deleteView.layout(contentView.getRight(), 0, contentView.getRight() + deleteWidth, deleteHeight);
            manager.setSwipeLayout(SwipeLayout.this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 让ViewDragHelper帮我们判断是否应该拦截
        boolean result = mViewDragHelper.shouldInterceptTouchEvent(ev);

        //如果当前有打开的，则需要直接拦截，交给onTouch处理
        if (manager != null && !manager.isShouldSwipe(this)) {
            //先关闭已经打开的layout
            manager.closeCurrentLayout();
            result = true;
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //如果当前有打开的，就不能再打开新的，要先关闭已经打开的,才能打开新的，则下面的逻辑不能执行
        if (manager != null && !manager.isShouldSwipe(this)) {
            requestDisallowInterceptTouchEvent(true);  //listview不能滑动
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();

                //获取x和y方向移动的距离
                float dx = moveX - downX;
                float dy = moveY - downY;

                if (Math.abs(dx) > Math.abs(dy)) {
                    requestDisallowInterceptTouchEvent(true); //水平滚动的时候，不能上下滑动
                }
                //更新downX，downY
                downX = moveX;
                downY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        mViewDragHelper.processTouchEvent(event);
        return true;
    }


    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 用于判断是否捕获当前child的触摸事件
         * @param child 当前触摸的子View
         * @param pointerId
         * @return true:捕获并解析  false:不处理
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == contentView || child == deleteView;
        }

        /**
         * 获取view水平方向的拖拽范围,但是目前不能限制边界,返回的值目前用在手指抬起的时候
         *  view缓慢移动的动画时间的计算; 最好不要返回0
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return deleteWidth;
        }


        /**
         * 控制child在水平方向的移动
         * @param child 当前触摸的子View
         * @param left 当前child的即将移动到的位置,left=chile.getLeft()+dx
         * @param dx 本次child水平方向移动的距离
         * @return 表示你真正想让child的left变成的值
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //限定边界
            if (child == contentView) {
                if (left > 0) {
                    left = 0;
                }
                //当deleteView完全滑出来时，contentView不能再滑动
                if (left < -deleteWidth) {
                    left = -deleteWidth;
                }
            } else if (child == deleteView) {
                if (left > contentWidth) {
                    left = contentWidth;
                }
                if (left < contentWidth - deleteWidth) {
                    left = contentWidth - deleteWidth;
                }
            }
            return left;
        }

        /**
         * 当child的位置改变的时候执行,一般用来做其他子View跟随该view移动
         * @param changedView 当前位置改变的child
         * @param left child当前最新的left
         * @param top child当前最新的top
         * @param dx 本次水平移动的距离
         * @param dy 本次垂直移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == contentView) {
                //contentView移动的时候，deleteview伴随移动
                deleteView.layout(deleteView.getLeft() + dx, deleteView.getTop() + dy,
                        deleteView.getRight() + dx, deleteView.getBottom() + dy);
            } else if (changedView == deleteView) {
                //deleteview移动的时候，contentview伴随移动
                contentView.layout(contentView.getLeft() + dx, contentView.getTop() + dy,
                        contentView.getRight() + dx, contentView.getBottom() + dy);
            }

            /**
             * 不能同时打开多个条目，只能打开一个条目
             */
            if (contentView.getLeft() == 0 && currentState != SwipeState.Close) {
                currentState = SwipeState.Close; //更改为关闭状态

                //回调接口关闭的方法
                if (listener != null) {
                    listener.onClose(getTag());
                }
                //说明当前的SwipeLayout已经关闭
                if (manager != null) {
                    manager.clearCurrentLayout();
                }

            } else if (contentView.getLeft() == -deleteWidth && currentState != SwipeState.Open) {
                currentState = SwipeState.Open;

                //回调接口打开的方法
                if (listener != null) {
                    listener.onOpen(getTag());
                }
                //当前的Swipelayout已经打开
                if (manager != null) {
                    manager.setSwipeLayout(SwipeLayout.this);
                }
            }
        }

        /**
         * 手指抬起的执行该方法
         * @param releasedChild 当前抬起的view
         * @param xvel x方向的移动速度有 正：向右移动
         * @param yvel 方向的移动速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (contentView.getLeft() < -deleteWidth / 2) {
                //滑动超过一半，打开
                open();
            } else {
                //滑动小于一半，关闭
                close();
            }
        }
    };

    /**
     * 打开的方法
     */
    public void open() {
        if (listener != null) {
            listener.onOpen(getTag());
        }
        mViewDragHelper.smoothSlideViewTo(contentView, -deleteWidth, contentView.getTop());
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this); //刷新
    }

    /**
     * 关闭的方法
     */
    public void close() {
        if (listener != null) {
            listener.onClose(getTag());
        }
        mViewDragHelper.smoothSlideViewTo(contentView, 0, contentView.getTop());
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //如果动画还没结束
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private OnSwipeStateChangeListener listener;

    public void setOnSwipeStateChangeListener(OnSwipeStateChangeListener listener) {
        this.listener = listener;
    }

    //把打开或关闭的状态暴露给外界
    public interface OnSwipeStateChangeListener {

        void onOpen(Object tag);

        void onClose(Object tag);
    }
}
