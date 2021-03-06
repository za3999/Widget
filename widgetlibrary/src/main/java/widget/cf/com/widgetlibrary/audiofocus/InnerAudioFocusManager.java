package widget.cf.com.widgetlibrary.audiofocus;

import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Pair;

import java.util.Iterator;
import java.util.WeakHashMap;

import widget.cf.com.widgetlibrary.util.ApplicationUtil;
import widget.cf.com.widgetlibrary.util.LogUtils;

public class InnerAudioFocusManager {

    private static volatile InnerAudioFocusManager mInstance;
    private InnerFocusRecord records = new InnerFocusRecord();
    private AudioManager mAudioManager;
    private SystemAudioFocusManager mSystemAudioFocusManager;
    private InnerAudioFocusChangeListener defaultInnerAudioFocusChangeListener;
    private WeakHashMap<FocusTypeChangeListener, Integer> mTypeChangeListenerMap = new WeakHashMap<>();

    private InnerAudioFocusManager() {
        mAudioManager = (AudioManager) ApplicationUtil.getApplication().getSystemService(Context.AUDIO_SERVICE);
        mSystemAudioFocusManager = new SystemAudioFocusManager(mAudioManager);
        mSystemAudioFocusManager.setOnAudioFocusChangeListener(focusChange -> {
            Pair<Integer, InnerAudioFocusChangeListener> record = records.getCurrentRecord();
            if (record != null && record.second != null) {
                record.second.onSystemAudioFocusChange(focusChange);
            } else {
                mSystemAudioFocusManager.releaseAudioFocus();
            }
        });
        defaultInnerAudioFocusChangeListener = new InnerAudioFocusChangeListener(true) {
            @Override
            public void onAudioFocusChange(int focusChange) {

            }
        };
    }

    public static void addFocusTypeChangeListener(FocusTypeChangeListener focusTypeChangeListener) {
        ApplicationUtil.runOnBgThread(() -> {
            getInstance().mTypeChangeListenerMap.put(focusTypeChangeListener, focusTypeChangeListener.hashCode());
            notifyFocusTypeChange();
        });
    }

    public static void removeFocusTypeChangeListener(FocusTypeChangeListener focusTypeChangeListener) {
        ApplicationUtil.runOnBgThread(() -> getInstance().mTypeChangeListenerMap.remove(focusTypeChangeListener));
    }

    public static void notifyRequest(@InnerAudioFocusType int focusType, boolean success) {
        LogUtils.d(AudioConstant.TAG, "notifyRequest:" + focusType + ":" + success);
        Iterator<FocusTypeChangeListener> it = getInstance().mTypeChangeListenerMap.keySet().iterator();
        while (it.hasNext()) {
            it.next().onRequest(focusType, success);
        }
    }

    public static void notifyFocusTypeChange() {
        int focusType = getCurrentFocusType();
        LogUtils.d(AudioConstant.TAG, "notifyFocusTypeChange:" + focusType);
        Iterator<FocusTypeChangeListener> it = getInstance().mTypeChangeListenerMap.keySet().iterator();
        while (it.hasNext()) {
            it.next().onFocusTypeChange(focusType);
        }
    }

    public static boolean request(@InnerAudioFocusType int audioType) {
        return request(new RequestParam(audioType), getInstance().defaultInnerAudioFocusChangeListener);
    }

    public static boolean request(@InnerAudioFocusType int audioType, InnerAudioFocusChangeListener innerAudioFocusChangeListener) {
        return request(new RequestParam(audioType), innerAudioFocusChangeListener);
    }

    public static boolean request(RequestParam requestParam, InnerAudioFocusChangeListener innerAudioFocusChangeListener) {
        return getInstance().requestInner(requestParam, innerAudioFocusChangeListener);
    }

    public static void release(@InnerAudioFocusType int audioType) {
        getInstance().releaseInner(audioType);
    }

    public static String getFocusBusyHint() {
        String hint = "";
        int focusType = getCurrentFocusType();
        return hint;
    }

    public static int getCurrentFocusType() {
        Pair<Integer, InnerAudioFocusChangeListener> record = getInstance().records.getCurrentRecord();
        return record == null ? InnerAudioFocusType.NON : record.first;
    }

    public static boolean isFocusLoss(int focusChange) {
        return AudioManager.AUDIOFOCUS_LOSS == focusChange || AudioManager.AUDIOFOCUS_LOSS_TRANSIENT == focusChange;
    }

    private static InnerAudioFocusManager getInstance() {
        if (null == mInstance) {
            synchronized (InnerAudioFocusManager.class) {
                if (null == mInstance) {
                    mInstance = new InnerAudioFocusManager();
                }
            }
        }
        return mInstance;
    }

    private synchronized boolean requestInner(RequestParam param, InnerAudioFocusChangeListener innerAudioFocusChangeListener) {
        LogUtils.d(AudioConstant.TAG, "requestFocus:" + param.getAudioType());
        if (param == null || innerAudioFocusChangeListener == null) {
            return false;
        }

        if (getLevel(param.getAudioType()) == Integer.MAX_VALUE && mAudioManager.isMusicActive()) {
            notifyRequest(param.getAudioType(), false);
            return false;
        }

        boolean success = true;
        Pair<Integer, InnerAudioFocusChangeListener> record = records.getCurrentRecord();
        if (record == null) {
            records.add(param.getAudioType(), innerAudioFocusChangeListener);
            ApplicationUtil.runOnBgThread(() -> notifyFocusTypeChange());
        } else if (record.first == param.getAudioType()) {
            if (innerAudioFocusChangeListener != record.second) {
                records.add(param.getAudioType(), innerAudioFocusChangeListener);
            }
        } else if (getLevel(param.getAudioType()) <= getLevel(record.first)) {
            onAudioFocusChange(record.second, param.isInnerLongHold() ? AudioManager.AUDIOFOCUS_LOSS : AudioManager.AUDIOFOCUS_LOSS_TRANSIENT);
            records.add(param.getAudioType(), innerAudioFocusChangeListener);
            ApplicationUtil.runOnBgThread(() -> notifyFocusTypeChange());
        } else {
            if (param.isAddToRecordAlways()) {
                records.insert(param.getAudioType(), innerAudioFocusChangeListener);
                onAudioFocusChange(innerAudioFocusChangeListener, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT);
            }
            LogUtils.d(AudioConstant.TAG, "request fail current is:" + record.first);
            success = false;
        }
        if (success) {
            innerAudioFocusChangeListener.restoreVolume();
            mSystemAudioFocusManager.requestFocus(param.isSystemLongHold());
        } else if (param.isShowToast()) {
            String hint = getFocusBusyHint();
            if (!TextUtils.isEmpty(hint)) {
                //todo
            }
        }
        notifyRequest(param.getAudioType(), success);
        return success;
    }

    private synchronized void releaseInner(@InnerAudioFocusType int audioType) {
        LogUtils.d(AudioConstant.TAG, "release:" + audioType);
        Pair<Integer, InnerAudioFocusChangeListener> record = records.getCurrentRecord();
        boolean isCurrentRecord = record != null && record.first == audioType;
        records.remove(audioType);
        record = records.getCurrentRecord();
        if (record != null) {
            if (isCurrentRecord) {
                onAudioFocusChange(record.second, AudioManager.AUDIOFOCUS_GAIN);
            }
        } else {
            mSystemAudioFocusManager.releaseAudioFocus();
        }
        if (isCurrentRecord) {
            ApplicationUtil.runOnBgThread(() -> notifyFocusTypeChange());
        }
    }

    private static void onAudioFocusChange(InnerAudioFocusChangeListener innerAudioFocusAdapter, int focusChange) {
        if (innerAudioFocusAdapter != null) {
            ApplicationUtil.runOnMainThread(() -> innerAudioFocusAdapter.onAudioFocusChange(focusChange));
        }
    }

    static int getLevel(@InnerAudioFocusType int audioType) {
        if (InnerAudioFocusType.CALL == audioType) {
            return 0;
        }
        if (InnerAudioFocusType.MEDIA_RECORD == audioType || InnerAudioFocusType.AUDIO_RECORD == audioType) {
            return 1;
        }
        if (InnerAudioFocusType.SILENT_VIDEO_PLAY == audioType) {
            return Integer.MAX_VALUE;
        }
        return 2;
    }
}
