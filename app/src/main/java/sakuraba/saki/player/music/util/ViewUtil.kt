package sakuraba.saki.player.music.util

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.createBitmap
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.os.Handler
import android.os.Looper.getMainLooper
import android.view.PixelCopy.SUCCESS
import android.view.PixelCopy.request
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi

object ViewUtil {

    fun View.screenshot(window: Window, complete: (Bitmap) -> Unit) {
        when {
            SDK_INT >= O -> screenshotApi28Impl(window, complete)
            else -> screenshotApi21(complete)
        }
    }

    @RequiresApi(O)
    private fun View.screenshotApi28Impl(window: Window, complete: (Bitmap) -> Unit) {
        createBitmap(width, height, ARGB_8888, false).also {
            request(window,
                it, { result ->
                    if (result == SUCCESS) {
                        complete(it)
                    }
                }, Handler(getMainLooper())
            )
        }
    }

    @Suppress("DEPRECATION")
    private fun View.screenshotApi21(complete: (Bitmap) -> Unit) {
        isDrawingCacheEnabled = true
        buildDrawingCache(true)
        complete(createBitmap(drawingCache))
        isDrawingCacheEnabled = false
    }

}