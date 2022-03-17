package projekt.cloud.piece.music.player.util

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.view.Display
import android.view.WindowManager
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

object ActivityUtil {
    
    val Activity.pixelHeight get(): Int {
        /**
         * Running [Context.getResources.getDisplayMetrics.getHeightPixels] cannot get correct heightPixels.
         * e.g. on my Xiaomi Mi 10 Ultra, height pixel is 2340, but calling [Context.getResources.getDisplayMetrics.getHeightPixels]
         * will return 2206, where is much greater than the [Rect.bottom] of [DrawerLayout] of [MainActivity].
         * So, we should use the [WindowManager.getDefaultDisplay] (API < 29), [Display.getRealSize] (API = 29), or [WindowManager.getCurrentWindowMetrics.getBounds.height]
         * to get real heightPixel.
         **/
        return  when {
            Build.VERSION.SDK_INT > Build.VERSION_CODES.R -> windowManager.currentWindowMetrics.bounds.height()
            Build.VERSION.SDK_INT == Build.VERSION_CODES.R -> Point().apply { @Suppress("DEPRECATION") display?.getRealSize(this) }.y
            else -> Point().apply { @Suppress("DEPRECATION") windowManager.defaultDisplay.getRealSize(this) }.y
        }
    }
    
    val Fragment.pixelHeight get() = requireActivity().pixelHeight
    
}