package fts.android.audio;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

/**
 * Helper APIs for constructing MediaStyle notifications
 */
public class MediaStyleHelper {
    private static final String CHANNEL_ID = "TraX Player";

    private static boolean channelCreated = false;

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT < 26 || channelCreated) return;
        channelCreated = true;

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence name = CHANNEL_ID;
        String description = "TraX Player Info";

        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(false);
        mChannel.enableVibration(false);
        mNotificationManager.createNotificationChannel(mChannel);
    }

    /**
     * Build a notification using the information from the given media session. Makes heavy use
     * of {@link MediaMetadataCompat#getDescription()} to extract the appropriate information.
     * @param context Context used to construct the notification.
     * @param mediaSession Media session to get information.
     * @return A pre-built notification with information from the given media session.
     */
    public static NotificationCompat.Builder from(
            Context context, MediaSessionCompat mediaSession) {
        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        if (mediaMetadata == null) return null;

        createNotificationChannel(context);

        MediaDescriptionCompat description = mediaMetadata.getDescription();

        Log.d("MSESSION", "mediaMetadata " + mediaMetadata);
        Log.d("MSESSION", "from " + description);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder
                .setContentTitle("Title " +description.getTitle())
                .setContentText("Text " + description.getSubtitle())
                .setSubText("SubText " + description.getDescription())
                .setLargeIcon(description.getIconBitmap())
                .setContentIntent(controller.getSessionActivity())
                .setDeleteIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setChannelId(CHANNEL_ID);
        return builder;
    }
}