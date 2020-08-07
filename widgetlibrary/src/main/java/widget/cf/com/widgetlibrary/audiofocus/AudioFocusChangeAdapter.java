package widget.cf.com.widgetlibrary.audiofocus;

import android.media.AudioManager;

import widget.cf.com.widgetlibrary.util.LogUtils;


public abstract class AudioFocusChangeAdapter implements AudioManager.OnAudioFocusChangeListener {

    private VolumeController mVolumeController;
    private int mLostType;

    public AudioFocusChangeAdapter(VolumeController volumeController) {

        this.mVolumeController = volumeController;
    }

    public abstract void onAudioFocusChangeWarp(int focusChange);

    @Override
    public final void onAudioFocusChange(int focusChange) {
        LogUtils.d(AudioConstant.TAG, "systemAudioFocus:" + focusChange);
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            if (mVolumeController.isVolumeDown()) {
                mVolumeController.restoreVolume();
            }
            if (mLostType == AudioManager.AUDIOFOCUS_LOSS || mLostType == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                onAudioFocusChangeWarp(focusChange);
            }
        } else {
            mLostType = focusChange;
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                onAudioFocusChangeWarp(focusChange);
            } else {
                mVolumeController.volumeDown();
            }
        }
    }
}
