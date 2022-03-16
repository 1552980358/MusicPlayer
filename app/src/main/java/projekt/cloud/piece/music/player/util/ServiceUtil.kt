package projekt.cloud.piece.music.player.util

import android.app.Service
import android.content.Intent
import lib.github1552980358.ktExtension.android.content.intent

object ServiceUtil {
    
    fun <S: Service> S.startService(intent: Intent.(Intent) -> Unit) =
        startService(intent(this, this::class.java, intent))
    
}