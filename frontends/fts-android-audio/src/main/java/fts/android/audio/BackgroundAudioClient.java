package fts.android.audio;

import android.app.Activity;

public interface BackgroundAudioClient {
    BackgroundAudioPlayer createAudioPlayer();
    void playNext();
    void playPrev();
    int getSmallIconResourceId();
    Class<? extends Activity> getNotificationActivatedActivityClass();
    String getAppName();
    String getNotificationChannelId();

}
