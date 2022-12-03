package fts.android.audio;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

// Code based in this tutorial: https://code.tutsplus.com/tutorials/background-audio-in-android-with-mediasessioncompat--cms-27030
// Android is a mess

public abstract class BackgroundAudioService extends MediaBrowserServiceCompat implements MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener  {
    private static final String LOGTAG = BackgroundAudioService.class.getSimpleName();
    public static final String KEY_TITLE = "title";
    public static final String KEY_SUBTITLE = "subtitle";
    public static final String KEY_ICON = "icon";
    public static final String KEY_NEXT_PREV = "canSkipNextPrev";

    public static final String COMMAND_EXAMPLE = "command_example";

    private BackgroundAudioPlayer audioPlayer;
    private MediaSessionCompat mMediaSessionCompat;
    private boolean canMoveNextPrev = false;

    private BackgroundAudioClient backgroundAudioClient;

    private BroadcastReceiver mNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if( audioPlayer != null && audioPlayer.isPlaying() ) {
                audioPlayer.pause();
            }
        }
    };

    private MediaSessionCompat.Callback mMediaSessionCallback = new MediaSessionCompat.Callback() {

        @Override
        public void onStop() {
            super.onStop();
            audioPlayer.stop();
            mMediaSessionCompat.setActive(false);
            stopForeground(true);
            stopSelf();
        }

        @Override
        public void onPlay() {
            super.onPlay();
            if( !successfullyRetrievedAudioFocus() ) {
                return;
            }

            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            mMediaSessionCompat.setActive(true);

            showPlayingNotification();
            audioPlayer.play();
        }

        @Override
        public void onPause() {
            super.onPause();
            if( audioPlayer.isPlaying() ) {
                audioPlayer.pause();
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
                showPausedNotification();
            }
        }

        @Override
        public void onPlayFromMediaId(String url, Bundle extras) {
            super.onPlayFromMediaId(url, extras);

            audioPlayer.setDataSource(getApplicationContext(), url);
            canMoveNextPrev = extras.getBoolean(KEY_NEXT_PREV);
            initMediaSessionMetadata(
                    extras.getString(KEY_TITLE),
                    extras.getString(KEY_SUBTITLE),
                    extras.getParcelable(KEY_ICON));
        }

        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            super.onCommand(command, extras, cb);
            if( COMMAND_EXAMPLE.equalsIgnoreCase(command) ) {
                //Custom command here
            }
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
        }

        @Override
        public void onSkipToNext() {
            backgroundAudioClient.playNext();
        }

        @Override
        public void onSkipToPrevious() {
            backgroundAudioClient.playPrev();
        }
    };

    protected abstract BackgroundAudioClient getBackgroundAudioClient();

    @Override
    public void onCreate() {
        super.onCreate();

        backgroundAudioClient = getBackgroundAudioClient();
        audioPlayer = backgroundAudioClient.createAudioPlayer();

        initMediaSession();
        initNoisyReceiver();
    }

    private void initNoisyReceiver() {
        // Handles headphones coming unplugged. cannot be done through a manifest receiver
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(mNoisyReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(this);
        unregisterReceiver(mNoisyReceiver);
        mMediaSessionCompat.release();
        NotificationManagerCompat.from(this).cancel(1);
        if (audioPlayer!= null) {
            audioPlayer.stop();
            audioPlayer = null;
        }
    }

    private void showPlayingNotification() {
        NotificationCompat.Builder builder = MediaStyleHelper.from(this, backgroundAudioClient, mMediaSessionCompat);
        if( builder == null ) {
            return;
        }

        if (canMoveNextPrev) builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_previous, "Previous song", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)));
        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        if (canMoveNextPrev) builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_next, "Next song", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2).setMediaSession(mMediaSessionCompat.getSessionToken()));
        builder.setSmallIcon(backgroundAudioClient.getSmallIconResourceId());
        builder.setContentIntent(getPendingIntentForActivity());
        Notification notification = builder.build();
        startForeground(1, notification);
    }

    private void showPausedNotification() {
        NotificationCompat.Builder builder = MediaStyleHelper.from(this, backgroundAudioClient, mMediaSessionCompat);
        if( builder == null ) {
            return;
        }

        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mMediaSessionCompat.getSessionToken()));
        builder.setSmallIcon(backgroundAudioClient.getSmallIconResourceId());
        builder.setContentIntent(getPendingIntentForActivity());
        NotificationManagerCompat.from(this).notify(1, builder.build());
        stopForeground(false);
    }

    private PendingIntent getPendingIntentForActivity() {
        Intent intent = new Intent(this, backgroundAudioClient.getNotificationActivatedActivityClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(Intent.ACTION_MAIN);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private void initMediaSession() {
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, PendingIntent.FLAG_IMMUTABLE);
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);

        mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(), LOGTAG, mediaButtonReceiver, pendingIntent);
        mMediaSessionCompat.setCallback(mMediaSessionCallback);

        setSessionToken(mMediaSessionCompat.getSessionToken());
    }

    private void setMediaPlaybackState(int state) {
        PlaybackStateCompat.Builder playbackstateBuilder = new PlaybackStateCompat.Builder();

        long actions = PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_STOP;
        actions |= PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        actions |= state == PlaybackStateCompat.STATE_PLAYING ? PlaybackStateCompat.ACTION_PAUSE : PlaybackStateCompat.ACTION_PLAY;
        playbackstateBuilder.setActions(actions);

        playbackstateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        mMediaSessionCompat.setPlaybackState(playbackstateBuilder.build());
    }

    private void initMediaSessionMetadata(String title, String subtitle, Bitmap icon) {
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
        // Notification icon in card
        // metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.platform_3do));
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, icon);

        // lock screen icon for pre lollipop
        // metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(getResources(), R.mipmap.platform_3do));
        // metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "Display Title");
        // metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, "Display Subtitle");
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, title);
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, subtitle);

        mMediaSessionCompat.setMetadata(metadataBuilder.build());
    }

    private boolean successfullyRetrievedAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int result = audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        return result == AudioManager.AUDIOFOCUS_GAIN;
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if(TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(backgroundAudioClient.getAppName(), null);
        }

        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch( focusChange ) {
            case AudioManager.AUDIOFOCUS_LOSS: {
                if( audioPlayer.isPlaying() ) {
                    audioPlayer.stop();
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: {
                audioPlayer.pause();
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: {
                if( audioPlayer != null ) {
                    audioPlayer.setVolume(0.3f);
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_GAIN: {
                if( audioPlayer != null ) {
                    if( !audioPlayer.isPlaying() ) {
                        audioPlayer.play();
                    }
                    audioPlayer.setVolume(1.0f);
                }
                break;
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if( audioPlayer != null ) {
            audioPlayer.stop();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent);
        return super.onStartCommand(intent, flags, startId);
    }
}
