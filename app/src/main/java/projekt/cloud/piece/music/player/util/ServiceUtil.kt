package projekt.cloud.piece.music.player.util

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O

object ServiceUtil {
    
    fun <S: Service> S.startSelf(intent: Intent.() -> Unit) =
        Intent(this, this::class.java).apply(intent).run { startServiceIntent(this) }
    
    private fun Context.startServiceIntent(intent: Intent) = when {
        SDK_INT >= O -> startForegroundService(intent)
        else -> startService(intent)
    }
    
    @JvmStatic
    inline fun <reified S: Service> Context.serviceIntent(intent: Intent.() -> Unit) =
        Intent(this, S::class.java).apply(intent)
    
}