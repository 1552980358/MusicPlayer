package sakuraba.saki.player.music.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.R
import android.view.Display
import android.view.WindowManager
import androidx.drawerlayout.widget.DrawerLayout
import sakuraba.saki.player.music.MainActivity

object SystemUtil {
    
    val Activity.pixelHeight get(): Int {
        /**
         * Running [Context.getResources.getDisplayMetrics.getHeightPixels] cannot get correct heightPixels.
         * e.g. on my Xiaomi Mi 10 Ultra, height pixel is 2340, but calling [Context.getResources.getDisplayMetrics.getHeightPixels]
         * will return 2206, where is much greater than the [Rect.bottom] of [DrawerLayout] of [MainActivity].
         * So, we should use the [WindowManager.getDefaultDisplay] (API < 29), [Display.getRealSize] (API = 29), or [WindowManager.getCurrentWindowMetrics.getBounds.height]
         * to get real heightPixel.
         **/
        return  when {
            SDK_INT > R -> windowManager.currentWindowMetrics.bounds.height()
            SDK_INT == R -> Point().apply { @Suppress("DEPRECATION") display?.getRealSize(this) }.y
            else -> Point().apply { @Suppress("DEPRECATION") windowManager.defaultDisplay.getRealSize(this) }.y
        }
    }
    
    val Resources.navigationBarHeight get(): Int {
        val resId = getIdentifier("navigation_bar_height", "dimen", "android")
        if (resId > 0) {
            return getDimensionPixelSize(resId)
        }
        return 0
    }
    
    val Activity.navigationBarHeight get() = resources.navigationBarHeight
    
}