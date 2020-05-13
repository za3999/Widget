package widget.cf.com.widgetlibrary.audiofocus;

import android.util.Pair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InnerFocusRecord {
    private static HashMap<Integer, WeakReference<InnerAudioFocusChangeListener>> audioMap = new HashMap<>();
    private static List<Integer> typeList = new ArrayList<>();

    public synchronized void add(int audioType, InnerAudioFocusChangeListener innerAudioFocusChangeListener) {
        audioMap.put(audioType, new WeakReference<>(innerAudioFocusChangeListener));
        if (typeList.contains(audioType)) {
            typeList.remove(new Integer(audioType));
        }
        typeList.add(audioType);
    }

    public synchronized void insert(int audioType, InnerAudioFocusChangeListener innerAudioFocusChangeListener) {
        audioMap.put(audioType, new WeakReference<>(innerAudioFocusChangeListener));
        if (typeList.contains(audioType)) {
            typeList.remove(new Integer(audioType));
        }
        int index = 0;
        for (int i = 0; i < typeList.size(); i++) {
            int type = typeList.get(i);
            if (InnerAudioFocusManager.getLevel(audioType) > InnerAudioFocusManager.getLevel(type)) {
                index = i;
                break;
            }
        }
        typeList.add(index, audioType);
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
        WeakReference<InnerAudioFocusChangeListener> listenerRef = audioMap.get(type);
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
