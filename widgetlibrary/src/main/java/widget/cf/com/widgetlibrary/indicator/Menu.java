package widget.cf.com.widgetlibrary.indicator;

public class Menu {

    private FolderData mFolderData;
    private boolean isSelect;

    public Menu(FolderData folderData) {
        this.mFolderData = folderData;
    }

    public int getId() {
        return mFolderData.getId();
    }

    public String getTitle() {
        return mFolderData.getName();
    }

    public int getUnreadCount() {
        return mFolderData.getUnreadCount();
    }

    public boolean isAll() {
        return mFolderData.isAll();
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
