package fts.android.audio;

import android.content.Context;

public interface BackgroundAudioPlayer {
    void play();
    void pause();
    void stop();
    boolean isPlaying();
    void setDataSource(Context context, String url);
    void setVolume(float volume);
}
