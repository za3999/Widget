package widget.cf.com.widgetlibrary.audiofocus;

public interface FocusTypeChangeListener {

    default void onFocusTypeChange(@InnerAudioFocusType int type) {

    }

    default void onRequest(@InnerAudioFocusType int type, boolean success) {
    }

}
