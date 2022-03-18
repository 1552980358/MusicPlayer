package projekt.cloud.piece.music.player.service.play

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.getBroadcast
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.NotificationManagerCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.app.NotificationCompat.MediaStyle
import projekt.cloud.piece.music.player.BuildConfig.APPLICATION_ID
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.service.play.Action.BROADCAST_ACTION_NEXT
import projekt.cloud.piece.music.player.service.play.Action.BROADCAST_ACTION_PAUSE
import projekt.cloud.piece.music.player.service.play.Action.BROADCAST_ACTION_PLAY
import projekt.cloud.piece.music.player.service.play.Action.BROADCAST_ACTION_PREV

object NotificationUtil {
    
    private const val CHANNEL_NAME = "PlayServiceNotification"
    
    private const val CHANNEL_ID = "${APPLICATION_ID}.PlayServiceNotification"
    
    private const val NOTIFICATION_ID = 23333
    
    val Context.createNotificationManager get(): NotificationManagerCompat = NotificationManagerCompat.from(this).createChannel
    
    val NotificationManagerCompat.createChannel get(): NotificationManagerCompat = apply {
        if (SDK_INT >= O) {
            createNotificationChannel(
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                    // Remove vibration with SDK >= 26
                    enableVibration(false)
                    vibrationPattern = longArrayOf(0)
                    setSound(null, null)
                }
            )
        }
    }
    
    fun Service.startForeground(notification: Notification) = startForeground(NOTIFICATION_ID, notification)
    
    fun NotificationManagerCompat.update(notification: Notification) = notify(NOTIFICATION_ID, notification)
    
    private val pendingIntentFlag get() = if (SDK_INT >= M) FLAG_IMMUTABLE else 0
    
    fun Service.createNotification(audioItem: AudioItem, isPaused: Boolean, largeBitmap: Bitmap) = Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setOngoing(false)
        .setPriority(PRIORITY_MAX)
        .setStyle(MediaStyle().setMediaSession((this as MediaBrowserServiceCompat).sessionToken))
        .setVibrate(longArrayOf(0))
        .setSilent(true)
        .setContentTitle(audioItem.title)
        .setContentText("${audioItem.artistItem.title} - ${audioItem.albumItem.title}")
        .setLargeIcon(largeBitmap)
        .addAction(R.drawable.ic_prev, null, getBroadcast(this, 0, Intent(BROADCAST_ACTION_PREV), pendingIntentFlag))
        .getPlayPauseAction(this, isPaused)
        .addAction(R.drawable.ic_next, null, getBroadcast(this, 0, Intent(BROADCAST_ACTION_NEXT), pendingIntentFlag))
        .build()
    
    private fun Builder.getPlayPauseAction(context: Context, isPaused: Boolean) = when {
        isPaused -> addAction(R.drawable.ic_play, null, getBroadcast(context, 0, Intent(BROADCAST_ACTION_PLAY), pendingIntentFlag))
        else -> addAction(R.drawable.ic_pause, null, getBroadcast(context, 0, Intent(BROADCAST_ACTION_PAUSE), pendingIntentFlag))
    }

}