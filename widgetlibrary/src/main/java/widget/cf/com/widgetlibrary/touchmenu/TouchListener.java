package widget.cf.com.widgetlibrary.touchmenu;

public interface TouchListener<T> {

    void onSelect(T data);

    void onTouchChange(T data);
}
