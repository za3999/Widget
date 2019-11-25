package widget.cf.com.widgetlibrary.audiofocus;

import android.media.AudioManager;

public abstract class AudioFocusChangeAdapter implements AudioManager.OnAudioFocusChangeListener {

    private int originalVolume;
    private boolean isVoiceDuckDown;
    private AudioManager mAudioManager;

    public AudioFocusChangeAdapter(AudioManager mAudioManager) {
        this.mAudioManager = mAudioManager;
    }

    public abstract void onAudioFocusChangeWarp(int focusChange);

    @Override
    public final void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                || focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            onAudioFocusChangeWarp(focusChange);
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK) {
            if (!isVoiceDuckDown) {
                originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (originalVolume / 2f), 0);
                isVoiceDuckDown = true;
            }
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            if (isVoiceDuckDown) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
                isVoiceDuckDown = false;
            }
            onAudioFocusChangeWarp(focusChange);
        }
    }
}
