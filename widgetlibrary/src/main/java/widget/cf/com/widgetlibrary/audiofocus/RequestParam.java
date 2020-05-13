package widget.cf.com.widgetlibrary.audiofocus;

public class RequestParam {
    private int audioType;
    private boolean innerLongHold = true;
    private boolean systemLongHold = true;
    private boolean showToast = true;
    private boolean addToRecordAlways = false;

    public RequestParam(@InnerAudioFocusType int audioType) {
        this.audioType = audioType;
    }

    public RequestParam setInnerLongHold(boolean innerLongHold) {
        this.innerLongHold = innerLongHold;
        return this;
    }

    public RequestParam setSystemLongHold(boolean systemLongHold) {
        this.systemLongHold = systemLongHold;
        return this;
    }

    public RequestParam setShowToast(boolean showToast) {
        this.showToast = showToast;
        return this;
    }

    public RequestParam setAddToRecordAlways(boolean addToRecordAlways) {
        this.addToRecordAlways = addToRecordAlways;
        return this;
    }

    public boolean isAddToRecordAlways() {
        return addToRecordAlways;
    }

    public int getAudioType() {
        return audioType;
    }

    public boolean isInnerLongHold() {
        return innerLongHold;
    }

    public boolean isSystemLongHold() {
        return systemLongHold;
    }

    public boolean isShowToast() {
        return showToast;
    }
}
