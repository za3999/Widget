package widget.cf.com.widgetlibrary.appearance;

import android.util.SparseArray;

import java.lang.ref.SoftReference;

public class AppearanceManager {

    private static volatile AppearanceManager mInstance;
    private SparseArray<SoftReference<IAppearanceChange>> appearanceViews = new SparseArray<>();

    private AppearanceManager() {
    }

    public static AppearanceManager getInstance() {
        if (mInstance == null) {
            synchronized (AppearanceManager.class) {
                if (mInstance == null) {
                    mInstance = new AppearanceManager();
                }
            }
        }
        return mInstance;
    }

    public void register(IAppearanceChange appearanceView) {
        synchronized (appearanceViews) {
            appearanceViews.put(appearanceView.hashCode(), new SoftReference<>(appearanceView));
        }
    }

    public void unRegister(IAppearanceChange appearanceView) {
        synchronized (appearanceViews) {
            appearanceViews.remove(appearanceView.hashCode());
        }
    }

    public void notifyAppearanceChange() {
        synchronized (appearanceViews) {
            for (int i = 0; i < appearanceViews.size(); i++) {
                SoftReference<IAppearanceChange> viewRef = appearanceViews.valueAt(i);
                IAppearanceChange view = viewRef.get();
                if (view != null) {
                    view.onChange();
                }
            }
        }
    }
}
