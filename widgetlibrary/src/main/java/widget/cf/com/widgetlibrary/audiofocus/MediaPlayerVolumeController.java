package widget.cf.com.widgetlibrary.audiofocus;

import android.media.MediaPlayer;

public abstract class MediaPlayerVolumeController implements VolumeController {

    protected abstract MediaPlayer getMediaPlayer();

    boolean isVolumeDown;

    @Override
    public void volumeDown() {
        MediaPlayer mediaPlayer = getMediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(0.3f, 0.3f);
        }
        isVolumeDown = true;
    }

    @Override
    public void restoreVolume() {
        MediaPlayer mediaPlayer = getMediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(1f, 1f);
        }
        isVolumeDown = false;
    }

    @Override
    public boolean isVolumeDown() {
        return isVolumeDown;
    }
}
