package app.github1552980358.android.musicplayer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.Service
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat

/**
 * @file    : [NotificationUtil]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/13
 * @time    : 17:48
 **/

interface NotificationUtil {
    
    companion object {
        
        const val ChannelName = "PlayAudioNotification"
        const val ChannelId = "app.github1552980358.android.musicplayer"
        
        const val ServiceId = 23333
        
    }
    
    fun createNotificationManager(context: Context): NotificationManagerCompat {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return NotificationManagerCompat.from(context)
        
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(NotificationChannel(ChannelId, ChannelName, IMPORTANCE_HIGH))
        return NotificationManagerCompat.from(context)
    }
    
    fun startForeground(service: Service, notification: Notification) {
        service.startForeground(ServiceId, notification)
    }
    
    
    
}