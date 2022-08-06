package projekt.cloud.piece.music.player.service.play

import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationManagerCompat
import projekt.cloud.piece.music.player.BuildConfig.APPLICATION_ID

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
    
}