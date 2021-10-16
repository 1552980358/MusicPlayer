package sakuraba.saki.player.music.web.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.Service
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import sakuraba.saki.player.music.BuildConfig
import sakuraba.saki.player.music.R

object NotificationUtil {

    private const val ChannelName = "WebServiceNotification"

    private const val ChannelId = "${BuildConfig.APPLICATION_ID}.WebServiceNotification"

    private const val NotificationId = 1552

    val Context.createNotificationManager get(): NotificationManagerCompat = NotificationManagerCompat.from(this).createChannel

    private val NotificationManagerCompat.createChannel get(): NotificationManagerCompat = apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                NotificationChannel(ChannelId, ChannelName, IMPORTANCE_HIGH).apply {
                    // Remove vibration with SDK >= 26
                    enableVibration(false)
                    vibrationPattern = longArrayOf(0)
                }
            )
        }
    }

    fun Service.startForeground(notification: Notification) = startForeground(NotificationId, notification)

    fun Context.getNotification(url: String) = NotificationCompat.Builder(this, ChannelId)
        .setContentTitle(getString(R.string.webservice_notification_title))
        .setContentText(url)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .build()

}