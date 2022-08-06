package projekt.cloud.piece.music.player.service.play

import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.graphics.Bitmap
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media.MediaBrowserServiceCompat
import projekt.cloud.piece.music.player.BuildConfig.APPLICATION_ID
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.item.AudioMetadata

class NotificationHelper(context: Context) {
    
    private companion object {
    
        const val CHANNEL_NAME = "PlayServiceNotificationChannel"
        const val CHANNEL_ID = "${APPLICATION_ID}:PlayService"
        const val NOTIFICATION_ID = 1552
        
    }
    
    private val notificationManagerCompat = NotificationManagerCompat.from(context)
        .createWithNotificationChannel()
    
    private fun NotificationManagerCompat.createWithNotificationChannel() = apply {
        if (SDK_INT >= O) {
            createNotificationChannel(
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_HIGH).apply {
                    enableVibration(false)
                    vibrationPattern = longArrayOf(0)
                    setSound(null, null)
                }
            )
        }
    }
    
    fun startForeground(service: MediaBrowserServiceCompat, audioMetadata: AudioMetadata,largeBitmap: Bitmap) =
        service.startForeground(NOTIFICATION_ID, createNotification(service, audioMetadata, largeBitmap))
    
    fun updateNotification(service: MediaBrowserServiceCompat, audioMetadata: AudioMetadata,largeBitmap: Bitmap) =
        service.startForeground(NOTIFICATION_ID, createNotification(service, audioMetadata, largeBitmap))
    
    fun createNotification(service: MediaBrowserServiceCompat, audioMetadata: AudioMetadata, largeBitmap: Bitmap) =
        NotificationCompat.Builder(service, CHANNEL_ID)
            .setPriority(PRIORITY_MAX)
            .setOngoing(false)
            .setVibrate(longArrayOf(0))
            .setSilent(true)
            .setStyle(MediaStyle().setMediaSession(service.sessionToken).setShowActionsInCompactView(0, 1, 2))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(largeBitmap)
            .setContentTitle(audioMetadata.title)
            .setContentText("${audioMetadata.artistName} - ${audioMetadata.albumTitle}")
            .build()
    
}