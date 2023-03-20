package projekt.cloud.piece.music.player.service.playback

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.media.MediaBrowserServiceCompat
import androidx.media.app.NotificationCompat.MediaStyle
import projekt.cloud.piece.music.player.R

class PlaybackNotificationHelper(context: Context) {

    private companion object PlaybackNotificationHelperConstants {
        const val PLAYBACK_CHANNEL_ID_SUFFIX = ".PlaybackNotificationChannel"

    }

    private val notificationManagerCompat = NotificationManagerCompat.from(context)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManagerCompat.createNotificationChannel(
                createNotificationChannel(context)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context): NotificationChannel {
        return NotificationChannel(
            context.packageName + PLAYBACK_CHANNEL_ID_SUFFIX,
            context.getString(R.string.playback_channel_name),
            IMPORTANCE_HIGH
        ).apply {
            enableVibration(false)
            vibrationPattern = longArrayOf(0)
            setSound(null, null)
        }
    }

    fun notifyNotification(context: Context, notification: Notification, id: Int) {
        if (ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
            notificationManagerCompat.notify(id, notification)
        }
    }

    fun createNotification(
        service: MediaBrowserServiceCompat,
        title: String, artist: String, album: String,
        bitmap: Bitmap? = null
    ) = NotificationCompat.Builder(service, service.packageName + PLAYBACK_CHANNEL_ID_SUFFIX)
            .setPriority(PRIORITY_MAX)
            .setOngoing(false)
            .setVibrate(longArrayOf(0))
            .setSilent(true)
            .setStyle(MediaStyle().setMediaSession(service.sessionToken))
            .setSmallIcon(R.drawable.ic_round_audiotrack_24)
            .setLargeIcon(bitmap)
            .setContentTitle(title)
            .setContentText("$artist - $album")
            .build()

}