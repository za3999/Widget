package widget.cf.com.widgetlibrary.audiofocus;

import android.util.Pair;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InnerFocusRecord {
    private static HashMap<Integer, SoftReference<InnerAudioFocusChangeListener>> audioMap = new HashMap<>();
    private static List<Integer> typeList = new ArrayList<>();

    public synchronized void add(int audioType, InnerAudioFocusChangeListener innerAudioFocusChangeListener) {
        audioMap.put(audioType, new SoftReference<>(innerAudioFocusChangeListener));
        if (typeList.contains(audioType)) {
            typeList.remove(audioType);
        }
        typeList.add(audioType);
    }

    public synchronized Pair<Integer, InnerAudioFocusChangeListener> getCurrentRecord() {
        if (typeList.size() == 0) {
            return null;
        }
        int type = typeList.get(typeList.size() - 1);
        if (type == InnerAudioFocusType.NON) {
            remove(type);
            return getCurrentRecord();
        }
        SoftReference<InnerAudioFocusChangeListener> listenerRef = audioMap.get(type);
        InnerAudioFocusChangeListener listener = listenerRef.get();
        if (listener == null) {
            remove(type);
            return getCurrentRecord();
        }
        return new Pair<>(type, listener);
    }

    public synchronized void remove(int audioType) {
        typeList.remove(new Integer(audioType));
        audioMap.remove(audioType);
    }
}
