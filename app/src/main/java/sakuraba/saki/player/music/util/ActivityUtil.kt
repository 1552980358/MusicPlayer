package sakuraba.saki.player.music.util

import android.app.Activity
import android.content.Context
import sakuraba.saki.player.music.R

object ActivityUtil {

    fun Context.translateEnter() {
        if (this is Activity) {
            translateEnter()
        }
    }

    fun Context.translateExit() {
        if (this is Activity) {
            translateExit()
        }
    }

    fun Activity.translateEnter() = overridePendingTransition(R.anim.translate_enter, R.anim.translate_exit)
    fun Activity.translateExit() = overridePendingTransition(R.anim.translate_pop_enter, R.anim.translate_pop_exit)

}