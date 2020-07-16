package widget.cf.com.widgetlibrary.audiofocus;

import android.media.AudioManager;

public class AudioStreamVolumeController implements VolumeController {

    private AudioManager mAudioManager;
    int originalVolume = -1;

    public AudioStreamVolumeController(AudioManager mAudioManager) {
        this.mAudioManager = mAudioManager;
    }

    @Override
    public void volumeDown() {
        originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (originalVolume / 2f), 0);
    }

    @Override
    public void restoreVolume() {
        if (originalVolume != -1) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
        }
        originalVolume = -1;
    }

    @Override
    public boolean isVolumeDown() {
        return originalVolume != -1;
    }
}
