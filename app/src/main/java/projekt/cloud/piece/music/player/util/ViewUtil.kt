package projekt.cloud.piece.music.player.util

import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi

object ViewUtil {

    fun View.screenshot(window: Window, complete: (Bitmap) -> Unit) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> screenshotApi28Impl(window, complete)
            else -> screenshotApi21(complete)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun View.screenshotApi28Impl(window: Window, complete: (Bitmap) -> Unit) {
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888, false).also {
            PixelCopy.request(window,
                it, { result ->
                    if (result == PixelCopy.SUCCESS) {
                        complete(it)
                    }
                }, Handler(Looper.getMainLooper())
            )
        }
    }

    @Suppress("DEPRECATION")
    private fun View.screenshotApi21(complete: (Bitmap) -> Unit) {
        isDrawingCacheEnabled = true
        buildDrawingCache(true)
        complete(Bitmap.createBitmap(drawingCache))
        isDrawingCacheEnabled = false
    }

}