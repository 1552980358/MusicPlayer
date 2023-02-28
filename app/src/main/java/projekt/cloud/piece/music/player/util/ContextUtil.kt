package projekt.cloud.piece.music.player.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type

private typealias WindowInsetListener = (Rect) -> Unit

object ContextUtil {

    @JvmStatic
    fun Context.requireWindowInsets(listener: WindowInsetListener) {
        if (this is Activity) {
            ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, windowInsetsCompat ->
                listener.invoke(
                    windowInsetsCompat.getInsets(Type.systemBars()).let { inset ->
                        Rect(inset.left, inset.top, inset.right, inset.bottom)
                    }
                )
                windowInsetsCompat
            }
        }
    }

}