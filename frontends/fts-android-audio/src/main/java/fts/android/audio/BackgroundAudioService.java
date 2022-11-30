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
import android.util.Log;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

public class BackgroundAudioService extends MediaBrowserServiceCompat implements MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener  {
    private static final String LOGTAG = BackgroundAudioService.class.getSimpleName();

    public static final String COMMAND_EXAMPLE = "command_example";

    private BackgroundAudioPlayer audioPlayer;
    private MediaSessionCompat mMediaSessionCompat;
    private boolean canMoveNextPrev = false;

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
        public void onPlay() {
            super.onPlay();
            Log.d(LOGTAG, "onPlay");
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
            Log.d(LOGTAG, "onPause");
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
            canMoveNextPrev = extras.getBoolean(TraXClient.KEY_NEXT_PREV);
            initMediaSessionMetadata(
                    extras.getString(TraXClient.KEY_TITLE),
                    extras.getString(TraXClient.KEY_SUBTITLE),
                    extras.getParcelable(TraXClient.KEY_ICON));
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
            TraXActivity.instance.playNext();
        }

        @Override
        public void onSkipToPrevious() {
            TraXActivity.instance.playPrev();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(LOGTAG, "onCreate");
        audioPlayer = new TraXAudioPlayer();

        initMediaSession();
        initNoisyReceiver();
    }

    private void initNoisyReceiver() {
        //Handles headphones coming unplugged. cannot be done through a manifest receiver
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(mNoisyReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOGTAG, "onDestroy");
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
        NotificationCompat.Builder builder = MediaStyleHelper.from(BackgroundAudioService.this, mMediaSessionCompat);
        if( builder == null ) {
            return;
        }

        Log.d("MSESSION", "showPlayingNotification");

        if (canMoveNextPrev) builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_previous, "Previous song", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)));
        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        if (canMoveNextPrev) builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_next, "Next song", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2).setMediaSession(mMediaSessionCompat.getSessionToken()));
        builder.setSmallIcon(R.drawable.wave_icon_squared_inverted_transparent);
        builder.setContentIntent(getPendingIntentForActivity());
        Notification notification = builder.build();
        // NotificationManagerCompat.from(this).notify(1, notification);
        startForeground(1, notification);
    }

    private void showPausedNotification() {
        NotificationCompat.Builder builder = MediaStyleHelper.from(this, mMediaSessionCompat);
        if( builder == null ) {
            return;
        }

        Log.d("MSESSION", "showPausedNotification");

        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mMediaSessionCompat.getSessionToken()));
        builder.setSmallIcon(R.drawable.wave_icon_squared_inverted_transparent);
        builder.setContentIntent(getPendingIntentForActivity());
        NotificationManagerCompat.from(this).notify(1, builder.build());
    }

    private PendingIntent getPendingIntentForActivity() {
        Intent intent = new Intent(this, TraXActivity.instance.getClass());
        intent.setAction(Intent.ACTION_MAIN);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private void initMediaSession() {
        Log.d("MSESSION", "initMediaSession");

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, PendingIntent.FLAG_IMMUTABLE);
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);

        mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "Tag", mediaButtonReceiver, pendingIntent);

        mMediaSessionCompat.setCallback(mMediaSessionCallback);
        mMediaSessionCompat.setFlags( MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS );

        setSessionToken(mMediaSessionCompat.getSessionToken());
    }

    private void setMediaPlaybackState(int state) {
        PlaybackStateCompat.Builder playbackstateBuilder = new PlaybackStateCompat.Builder();

        long actions = PlaybackStateCompat.ACTION_PLAY_PAUSE;
        actions |= PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;

        if( state == PlaybackStateCompat.STATE_PLAYING ) {
            Log.d("MSESSION", "setMediaPlaybackState PLAYING");
            playbackstateBuilder.setActions(actions | PlaybackStateCompat.ACTION_PAUSE);
        } else {
            Log.d("MSESSION", "setMediaPlaybackState PAUSE");
            playbackstateBuilder.setActions(actions | PlaybackStateCompat.ACTION_PLAY);
        }
        playbackstateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        mMediaSessionCompat.setPlaybackState(playbackstateBuilder.build());
    }

    private void initMediaSessionMetadata(String title, String subtitle, Bitmap icon) {
        Log.d(LOGTAG, "initMediaSessionMetadata");

        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
        //Notification icon in card
        // metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.platform_3do));
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, icon);

        //lock screen icon for pre lollipop
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


    //Not important for general audio service, required for class
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if(TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }

        return null;
    }

    //Not important for general audio service, required for class
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
