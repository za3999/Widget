package widget.cf.com.widgetlibrary.audiofocus;

import android.media.AudioManager;


public abstract class AudioFocusChangeAdapter implements AudioManager.OnAudioFocusChangeListener {

    private VolumeController mVolumeController;
    private int mLostType;

    public AudioFocusChangeAdapter(VolumeController volumeController) {

        this.mVolumeController = volumeController;
    }

    public abstract void onAudioFocusChangeWarp(int focusChange);

    @Override
    public final void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            if (mVolumeController.isVolumeDown()) {
                mVolumeController.restoreVolume();
            }
            if (mLostType == AudioManager.AUDIOFOCUS_LOSS) {
                onAudioFocusChangeWarp(focusChange);
            }
        } else {
            mLostType = focusChange;
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                onAudioFocusChangeWarp(focusChange);
            } else {
                mVolumeController.volumeDown();
            }
        }
    }
}
