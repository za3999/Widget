package widget.cf.com.widgetlibrary.touchmenu;

public class PopParam {
    private boolean longClickEnable;
    private boolean touchDownEnable;
    private boolean clickEnable;
    private float xOffset;
    private float yOffset;

    public boolean isLongClickEnable() {
        return longClickEnable;
    }

    public PopParam setLongClickEnable(boolean longClickEnable) {
        this.longClickEnable = longClickEnable;
        return this;
    }

    public boolean isTouchDownEnable() {
        return touchDownEnable;
    }

    public PopParam setTouchDownEnable(boolean touchDownEnable) {
        this.touchDownEnable = touchDownEnable;
        return this;
    }

    public boolean isClickEnable() {
        return clickEnable;
    }

    public PopParam setClickEnable(boolean clickEnable) {
        this.clickEnable = clickEnable;
        return this;
    }

    public float getXOffset() {
        return xOffset;
    }

    public PopParam setXOffset(float xOffset) {
        this.xOffset = xOffset;
        return this;
    }

    public float getYOffset() {
        return yOffset;
    }

    public PopParam setYOffset(float yOffset) {
        this.yOffset = yOffset;
        return this;
    }
}
