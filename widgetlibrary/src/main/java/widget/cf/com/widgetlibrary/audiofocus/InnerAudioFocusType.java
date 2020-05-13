package widget.cf.com.widgetlibrary.audiofocus;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({InnerAudioFocusType.NON, InnerAudioFocusType.MUSIC,
        InnerAudioFocusType.CALL, InnerAudioFocusType.MEDIA_RECORD,
        InnerAudioFocusType.VIDEO, InnerAudioFocusType.AUDIO_RECORD,
        InnerAudioFocusType.RECORD_PLAY_VIDEO,
        InnerAudioFocusType.SILENT_VIDEO_PLAY})
@Retention(RetentionPolicy.SOURCE)
public @interface InnerAudioFocusType {
    int NON = 0;
    int MUSIC = 1;
    int CALL = 2;
    int MEDIA_RECORD = 3;
    int VIDEO = 4;
    int AUDIO_RECORD = 5;
    int RECORD_PLAY_VIDEO = 6;
    int SILENT_VIDEO_PLAY = 7;
}
