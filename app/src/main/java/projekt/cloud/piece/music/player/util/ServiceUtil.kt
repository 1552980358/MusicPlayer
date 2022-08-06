package projekt.cloud.piece.music.player.util

import android.app.Service
import android.content.Intent

object ServiceUtil {
    
    inline fun <reified S: Service> S.startSelf(intentBlock: Intent.() -> Unit = {}) =
        startService(Intent(this, S::class.java).apply(intentBlock))
    
}