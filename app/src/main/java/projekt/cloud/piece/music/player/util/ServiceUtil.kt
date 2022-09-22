package projekt.cloud.piece.music.player.util

import android.app.Service
import android.app.Service.STOP_FOREGROUND_DETACH
import android.app.Service.STOP_FOREGROUND_REMOVE
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N

object ServiceUtil {
    
    inline fun <reified S: Service> S.startSelf(intentBlock: Intent.() -> Unit = {}) =
        startService(Intent(this, S::class.java).apply(intentBlock))
    
    fun Service.setStopForeground(removeNotification: Boolean) = when {
        SDK_INT >= N -> stopForeground(if (removeNotification) STOP_FOREGROUND_REMOVE else STOP_FOREGROUND_DETACH)
        else -> @Suppress("DEPRECATION") stopForeground(removeNotification)
    }
    
}