package widget.cf.com.widgetlibrary.adapter;

public class MultiItem<T> {

    protected int itemType;
    protected T data;

    public MultiItem(int itemType, T data) {
        this.itemType = itemType;
        this.data = data;
    }

    public int getItemType() {
        return itemType;
    }

    public T getData() {
        return data;
    }

}
