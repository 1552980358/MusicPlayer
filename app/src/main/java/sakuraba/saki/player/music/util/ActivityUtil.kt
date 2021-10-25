package sakuraba.saki.player.music.util

import android.app.Activity
import android.content.Context

object ActivityUtil {

    fun Context.fadeAnim() {
        if (this is Activity) {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    fun Activity.fadeAnim() = overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

}