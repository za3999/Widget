package widget.cf.com.widgetlibrary.touchmenu;

public class TouchPopParam {
    private boolean longClickEnable;
    private boolean touchDownEnable = true;
    private boolean clickEnable = true;
    private float xOffset;
    private float yOffset;

    public boolean isLongClickEnable() {
        return longClickEnable;
    }

    public TouchPopParam setLongClickEnable(boolean longClickEnable) {
        this.longClickEnable = longClickEnable;
        return this;
    }

    public boolean isTouchDownEnable() {
        return touchDownEnable;
    }

    public TouchPopParam setTouchDownEnable(boolean touchDownEnable) {
        this.touchDownEnable = touchDownEnable;
        return this;
    }

    public boolean isClickEnable() {
        return clickEnable;
    }

    public TouchPopParam setClickEnable(boolean clickEnable) {
        this.clickEnable = clickEnable;
        return this;
    }

    public float getXOffset() {
        return xOffset;
    }

    public TouchPopParam setXOffset(float xOffset) {
        this.xOffset = xOffset;
        return this;
    }

    public float getYOffset() {
        return yOffset;
    }

    public TouchPopParam setYOffset(float yOffset) {
        this.yOffset = yOffset;
        return this;
    }
}
