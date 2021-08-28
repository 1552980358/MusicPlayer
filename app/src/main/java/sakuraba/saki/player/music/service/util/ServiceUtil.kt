package sakuraba.saki.player.music.service.util

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

fun Context.startService(clazz: Class<out Service>, intent: Intent.(Intent) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startServiceApi26(clazz, intent)
    } else {
        startService(Intent(this, clazz).apply { intent(this) })
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Context.startServiceApi26(service: Class<out Service>, intent: Intent.(Intent) -> Unit) =
    startForegroundService(Intent(this, service).apply { intent(this) })