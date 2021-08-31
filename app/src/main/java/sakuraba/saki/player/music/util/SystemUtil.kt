package sakuraba.saki.player.music.util

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.R
import android.view.Display
import android.view.WindowManager
import androidx.drawerlayout.widget.DrawerLayout
import sakuraba.saki.player.music.MainActivity

object SystemUtil {
    
    val Activity.pixelHeight get(): Int = Point().apply {
        /**
         * Running [Context.getResources.getDisplayMetrics.getHeightPixels] cannot get correct heightPixels.
         * e.g. on my Xiaomi Mi 10 Ultra, height pixel is 2340, but calling [Context.getResources.getDisplayMetrics.getHeightPixels]
         * will return 2206, where is much greater than the [Rect.bottom] of [DrawerLayout] of [MainActivity].
         * So, we should use the [WindowManager.getDefaultDisplay] (API < 29) or [Display.getRealSize] (API >= 29)
         * to get real heightPixel.
         **/
        @Suppress("DEPRECATION")
        when {
            SDK_INT >= R ->
                // Require API 29+
                display?.getRealSize(this)
            // Deprecated on API 29
            else -> windowManager.defaultDisplay.getRealSize(this)
        }
    }.y
    
}