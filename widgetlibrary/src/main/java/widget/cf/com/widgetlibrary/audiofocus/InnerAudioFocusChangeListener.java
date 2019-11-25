package widget.cf.com.widgetlibrary.audiofocus;

import android.media.AudioManager;

public abstract class InnerAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {

    private AudioManager.OnAudioFocusChangeListener systemAudioFocusChangeListener;
    private boolean ignoreSystemFocusChange;

    public InnerAudioFocusChangeListener() {

    }

    public InnerAudioFocusChangeListener(boolean ignoreSystemFocusChange) {
        this.ignoreSystemFocusChange = ignoreSystemFocusChange;
    }

    public void checkDefaultSystemFocusListener(AudioManager audioManager) {
        if (!ignoreSystemFocusChange && systemAudioFocusChangeListener == null) {
            setSystemAudioFocusChangeListener(new AudioFocusChangeAdapter(audioManager) {

                @Override
                public void onAudioFocusChangeWarp(int focusChange) {
                    InnerAudioFocusChangeListener.this.onAudioFocusChange(focusChange);
                }
            });
        }
    }

    public InnerAudioFocusChangeListener setSystemAudioFocusChangeListener(AudioManager.OnAudioFocusChangeListener listener) {
        this.systemAudioFocusChangeListener = listener;
        return this;
    }

    public void onSystemAudioFocusChange(int focusChange) {
        if (ignoreSystemFocusChange) {
            return;
        }
        if (systemAudioFocusChangeListener != null) {
            systemAudioFocusChangeListener.onAudioFocusChange(focusChange);
        } else {
            onAudioFocusChange(focusChange);
        }
    }

}
