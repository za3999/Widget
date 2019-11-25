package widget.cf.com.widgetlibrary.audiofocus;

import android.content.Context;
import android.media.AudioManager;
import android.util.Pair;

import widget.cf.com.widgetlibrary.util.ApplicationUtil;
import widget.cf.com.widgetlibrary.util.LogUtils;


public class InnerAudioFocusManager {

    private static final String TAG = "InnerAudioFocusManager";

    private static volatile InnerAudioFocusManager mInstance;
    private InnerFocusRecord records = new InnerFocusRecord();
    private AudioManager mAudioManager;
    private SystemAudioFocusManager mSystemAudioFocusManager;
    private InnerAudioFocusChangeListener defaultInnerAudioFocusChangeListener;

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

    public static boolean request(@InnerAudioFocusType int audioType) {
        return getInstance().requestInner(audioType, true, true, getInstance().defaultInnerAudioFocusChangeListener);
    }

    public static boolean request(@InnerAudioFocusType int audioType, InnerAudioFocusChangeListener innerAudioFocusChangeListener) {
        return getInstance().requestInner(audioType, true, true, innerAudioFocusChangeListener);
    }

    public static boolean request(@InnerAudioFocusType int audioType, boolean systemLongHold, InnerAudioFocusChangeListener innerAudioFocusChangeListener) {
        return getInstance().requestInner(audioType, true, systemLongHold, innerAudioFocusChangeListener);
    }

    public static boolean request(@InnerAudioFocusType int audioType, boolean innerLongHold, boolean systemLongHold, InnerAudioFocusChangeListener innerAudioFocusChangeListener) {
        return getInstance().requestInner(audioType, innerLongHold, systemLongHold, innerAudioFocusChangeListener);
    }

    public static void release(@InnerAudioFocusType int audioType) {
        getInstance().releaseInner(audioType);
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

    private synchronized boolean requestInner(@InnerAudioFocusType int audioType, boolean innerLongHold, boolean systemLongHold, InnerAudioFocusChangeListener innerAudioFocusChangeListener) {
        LogUtils.d(TAG, "requestFocus:" + audioType);
        if (innerAudioFocusChangeListener == null) {
            return false;
        }
        innerAudioFocusChangeListener.checkDefaultSystemFocusListener(mAudioManager);
        mSystemAudioFocusManager.requestFocus(systemLongHold);
        Pair<Integer, InnerAudioFocusChangeListener> record = records.getCurrentRecord();
        if (record == null) {
            records.add(audioType, innerAudioFocusChangeListener);
            return true;
        }

        if (record.first == audioType) {
            if (innerAudioFocusChangeListener != record.second) {
                records.add(audioType, innerAudioFocusChangeListener);
            }
            return true;
        }

        if (getLevel(audioType) <= getLevel(record.first)) {
            onAudioFocusChange(record.second, innerLongHold ? AudioManager.AUDIOFOCUS_LOSS : AudioManager.AUDIOFOCUS_LOSS_TRANSIENT);
            records.add(audioType, innerAudioFocusChangeListener);
            return true;
        } else {
            LogUtils.d(TAG, "request fail current is:" + record.first);
            onAudioFocusChange(innerAudioFocusChangeListener, innerLongHold ? AudioManager.AUDIOFOCUS_LOSS : AudioManager.AUDIOFOCUS_LOSS_TRANSIENT);
            return false;
        }
    }

    private synchronized void releaseInner(@InnerAudioFocusType int audioType) {
        LogUtils.d(TAG, "release:" + audioType);
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
    }

    private static void onAudioFocusChange(InnerAudioFocusChangeListener innerAudioFocusAdapter, int focusChange) {
        if (innerAudioFocusAdapter != null) {
            ApplicationUtil.runOnMainThread(() -> innerAudioFocusAdapter.onAudioFocusChange(focusChange));
        }
    }

    private int getLevel(@InnerAudioFocusType int audioType) {
        if (InnerAudioFocusType.CALL == audioType) {
            return 0;
        } else if (InnerAudioFocusType.MEDIA_RECORD == audioType || InnerAudioFocusType.AUDIO_RECORD == audioType) {
            return 1;
        } else {
            return 2;
        }
    }
}
