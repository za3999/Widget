package widget.cf.com.widgetlibrary.touchmenu;

public interface TouchItemListener<T> {

    void onSelect(T data);

    void onTouchChange(T data);
}
