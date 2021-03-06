package widget.cf.com.widgetlibrary.audiofocus;

import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

public class SystemAudioFocusManager implements AudioManager.OnAudioFocusChangeListener {
    private AudioManager mAudioManager;
    private AudioFocusRequest mFocusRequest;
    private AudioAttributes mAudioAttributes;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener;

    public SystemAudioFocusManager(AudioManager audioManager) {
        mAudioManager = audioManager;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (mAudioFocusChangeListener != null) {
            mAudioFocusChangeListener.onAudioFocusChange(focusChange);
        }
    }

    public int requestFocus(boolean longHold) {
        int result = AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (mFocusRequest == null) {
                    if (mAudioAttributes == null) {
                        mAudioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
                    }
                    mFocusRequest = new AudioFocusRequest.Builder(longHold ? AudioManager.AUDIOFOCUS_GAIN : AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(mAudioAttributes).setWillPauseWhenDucked(true)
                            .setOnAudioFocusChangeListener(this).build();
                }
                result = mAudioManager.requestAudioFocus(mFocusRequest);
            } else {
                result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, longHold ? AudioManager.AUDIOFOCUS_GAIN : AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void releaseAudioFocus() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (mFocusRequest != null) {
                    mAudioManager.abandonAudioFocusRequest(mFocusRequest);
                }
            } else {
                mAudioManager.abandonAudioFocus(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnAudioFocusChangeListener(AudioManager.OnAudioFocusChangeListener listener) {
        mAudioFocusChangeListener = listener;
    }

}
