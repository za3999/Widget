package widget.cf.com.widgetlibrary.audiofocus;

import android.media.AudioManager;

public abstract class InnerAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {

    private AudioManager.OnAudioFocusChangeListener systemAudioFocusChangeListener;
    private boolean ignoreSystemFocusChange;
    private VolumeController volumeController;

    public InnerAudioFocusChangeListener() {

    }

    public InnerAudioFocusChangeListener setVolumeController(VolumeController volumeController) {
        this.volumeController = volumeController;
        return this;
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

                @Override
                public void volumeDown() {
                    if (volumeController != null) {
                        volumeController.volumeDown();
                    } else {
                        super.volumeDown();
                    }
                }

                @Override
                public void restoreVolume() {
                    if (volumeController != null) {
                        volumeController.restoreVolume();
                    } else {
                        super.restoreVolume();
                    }
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
