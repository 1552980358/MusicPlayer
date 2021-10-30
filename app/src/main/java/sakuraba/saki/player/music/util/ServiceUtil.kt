package sakuraba.saki.player.music.util

import android.app.ActivityManager
import android.app.Service
import android.content.Context.ACTIVITY_SERVICE

object ServiceUtil {

    val Service.isForegroundService get(): Boolean {
        @Suppress("DEPRECATION")
        (getSystemService(ACTIVITY_SERVICE) as ActivityManager).getRunningServices(10).forEach { serviceInfo ->
            if (serviceInfo.service.className == this::class.java.name) {
                return serviceInfo.foreground
            }
        }
        return false
    }

}