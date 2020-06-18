package widget.cf.com.widgetlibrary.indicator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FolderData {

    private int id;
    private String name;
    private Set<String> dialogIdSet = new HashSet<>();
    private int unreadCount;
    private boolean isAll;

    public FolderData(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public FolderData(int id, String name, List<String> dialogIds) {
        this.id = id;
        this.name = name;
        this.dialogIdSet.addAll(dialogIds);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAll() {
        return isAll;
    }

    public FolderData setAll(boolean all) {
        isAll = all;
        return this;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    @Override
    public String toString() {
        return "FolderData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", unreadCount=" + unreadCount +
                ", isAll=" + isAll +
                '}';
    }
}
