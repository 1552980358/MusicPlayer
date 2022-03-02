package sakuraba.saki.player.music.util

import android.app.Activity
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS

object ActivityUtil {

    fun Activity.setLightNavigationBar() {
        when  {
            SDK_INT > Q -> window.decorView.windowInsetsController?.setSystemBarsAppearance(APPEARANCE_LIGHT_NAVIGATION_BARS, APPEARANCE_LIGHT_NAVIGATION_BARS)
            else -> @Suppress("DEPRECATION", "InlinedApi")
            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR or window.decorView.systemUiVisibility
        }
    }

}