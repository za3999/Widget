package widget.cf.com.widgetlibrary.audiofocus;

import android.media.AudioManager;

public abstract class InnerAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {

    private AudioManager.OnAudioFocusChangeListener systemAudioFocusChangeListener = focusChange -> InnerAudioFocusChangeListener.this.onAudioFocusChange(focusChange);
    private boolean ignoreSystemFocusChange;
    private VolumeController mVolumeController;

    public InnerAudioFocusChangeListener(boolean ignoreSystemFocusChange) {
        this.ignoreSystemFocusChange = ignoreSystemFocusChange;
    }

    public void restoreVolume() {
        if (mVolumeController != null && mVolumeController.isVolumeDown()) {
            mVolumeController.restoreVolume();
        }
    }

    public InnerAudioFocusChangeListener setVolumeController(VolumeController volumeController) {
        this.mVolumeController = volumeController;
        setSystemAudioFocusChangeListener(new AudioFocusChangeAdapter(volumeController) {
            @Override
            public void onAudioFocusChangeWarp(int focusChange) {
                InnerAudioFocusChangeListener.this.onAudioFocusChange(focusChange);
            }
        });
        return this;
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
