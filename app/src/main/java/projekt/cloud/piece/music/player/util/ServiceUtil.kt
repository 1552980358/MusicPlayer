package projekt.cloud.piece.music.player.util

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle

object ServiceUtil {

    inline fun <reified S: Service> S.startSelfForeground(extras: Bundle): ComponentName? {
        return Intent(this, S::class.java)
            .putExtras(extras)
            .let { intent ->
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                        startForegroundService(intent)
                    }
                    else -> {
                        startService(intent)
                    }
                }
            }
    }

    inline fun <reified S: Service> S.startSelf(extras: Bundle): ComponentName? {
        return startService(Intent(this, S::class.java).putExtras(extras))
    }

}