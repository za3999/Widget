package widget.cf.com.widgetlibrary.indicator;

public class MenuData {
    private int id;
    private String title;
    private String unreadCount;

    public MenuData(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
