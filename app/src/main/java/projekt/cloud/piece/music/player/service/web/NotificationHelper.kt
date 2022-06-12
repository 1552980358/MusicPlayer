package projekt.cloud.piece.music.player.service.web

import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.Service
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.NotificationManagerCompat
import projekt.cloud.piece.music.player.BuildConfig.APPLICATION_ID
import projekt.cloud.piece.music.player.R

/**
 * [NotificationHelper]
 *
 * Methods:
 * [startForeground]
 * [createNotification]
 **/
class NotificationHelper(context: Context) {
    
    companion object {
        private const val CHANNEL_NAME = "WebServiceNotification"
        private const val CHANNEL_ID = "${APPLICATION_ID}.WebServiceNotification"
        private const val NOTIFICATION_ID = 80
    }
    
    init {
        if (SDK_INT >= O) {
            NotificationManagerCompat.from(context).createNotificationChannel(
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_HIGH).apply {
                    enableVibration(false)
                    vibrationPattern = longArrayOf(0)
                    setSound(null, null)
                }
            )
        }
    }
    
    fun startForeground(service: Service, content: String) =
        service.startForeground(NOTIFICATION_ID, createNotification(service, content))
    
    private fun createNotification(context: Context, content: String) = NotificationCompat.Builder(context, CHANNEL_ID)
        .setPriority(PRIORITY_MAX)
        .setOngoing(true)
        .setVibrate(longArrayOf(0))
        .setSilent(true)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(context.getString(R.string.web_service_notification_title))
        .setContentText(content)
        .build()
    
}