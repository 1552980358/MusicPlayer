package projekt.cloud.piece.music.player.service.play

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media.MediaBrowserServiceCompat
import projekt.cloud.piece.music.player.BuildConfig.APPLICATION_ID
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.service.play.Actions.ACTION_BROADCAST_NEXT
import projekt.cloud.piece.music.player.service.play.Actions.ACTION_BROADCAST_PAUSE
import projekt.cloud.piece.music.player.service.play.Actions.ACTION_BROADCAST_PLAY
import projekt.cloud.piece.music.player.service.play.Actions.ACTION_BROADCAST_PREV

/**
 * Class [ServiceNotification]
 *  @param context: [Context]
 *
 * Constants:
 *  [CHANNEL_NAME]
 *  [CHANNEL_ID]
 *  [NOTIFICATION_ID]
 *
 * Getters:
 *  [notificationChannel]
 *
 *  Methods:
 *   [startForeground]
 *   [updateNotification]
 *   [createNotification]
 *
 **/
class ServiceNotification(context: Context) {
    
    companion object {
        private const val CHANNEL_NAME = "PlayServiceNotificationChannel"
        private const val CHANNEL_ID = "${APPLICATION_ID}:PlayService"
        private const val NOTIFICATION_ID = 1552
    }
    
    private val notificationManagerCompat: NotificationManagerCompat
    
    init {
        notificationManagerCompat = NotificationManagerCompat.from(context).notificationChannel
    }
    
    fun startForeground(service: MediaBrowserServiceCompat, audioItem: AudioItem, isPlaying: Boolean, largeBitmap: Bitmap) =
        service.startForeground(NOTIFICATION_ID, createNotification(service, audioItem, isPlaying, largeBitmap))
    
    fun updateNotification(service: MediaBrowserServiceCompat, audioItem: AudioItem, isPlaying: Boolean, largeBitmap: Bitmap) =
        notificationManagerCompat.notify(NOTIFICATION_ID, createNotification(service, audioItem, isPlaying, largeBitmap))
    
    private fun createNotification(service: MediaBrowserServiceCompat, audioItem: AudioItem, isPlaying: Boolean, largeBitmap: Bitmap) =
        NotificationCompat.Builder(service, CHANNEL_ID)
            .setPriority(PRIORITY_MAX)
            .setOngoing(false)
            .setVibrate(longArrayOf(0))
            .setSilent(true)
            .setStyle(MediaStyle().setMediaSession(service.sessionToken).setShowActionsInCompactView(0, 1, 2))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(largeBitmap)
            .setContentTitle(audioItem.title)
            .setContentText("${audioItem.artistName} - ${audioItem.albumTitle}")
            .addAction(R.drawable.ic_round_skip_previous_24, null, PendingIntent.getBroadcast(service, 0, Intent(ACTION_BROADCAST_PREV), pendingIntentFlag))
            .addPlayStateButton(service, isPlaying)
            .addAction(R.drawable.ic_round_skip_next_24, null, PendingIntent.getBroadcast(service, 0, Intent(ACTION_BROADCAST_NEXT), pendingIntentFlag))
            .build()
    
    private fun NotificationCompat.Builder.addPlayStateButton(service: Service, isPlaying: Boolean) = apply {
        when {
            isPlaying -> addAction(R.drawable.ic_round_pause_24, null, PendingIntent.getBroadcast(service, 0, Intent(ACTION_BROADCAST_PAUSE), pendingIntentFlag))
            else -> addAction(R.drawable.ic_round_play_arrow_24, null, PendingIntent.getBroadcast(service, 0, Intent(ACTION_BROADCAST_PLAY), pendingIntentFlag))
        }
    }
    
    private val pendingIntentFlag get() = if (SDK_INT >= M) PendingIntent.FLAG_IMMUTABLE else 0
    
    private val NotificationManagerCompat.notificationChannel get() = apply {
        if (SDK_INT >= O) {
            createNotificationChannel(
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                    enableVibration(false)
                    vibrationPattern = longArrayOf(0)
                    setSound(null, null)
                }
            )
        }
    }

}