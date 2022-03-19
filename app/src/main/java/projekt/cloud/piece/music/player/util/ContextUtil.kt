package projekt.cloud.piece.music.player.util

import android.content.Context
import android.content.res.Resources
import androidx.fragment.app.Fragment

object ContextUtil {

    val Resources.navigationBarHeight get(): Int {
        val resId = getIdentifier("navigation_bar_height", "dimen", "android")
        if (resId > 0) {
            return getDimensionPixelSize(resId)
        }
        return 0
    }

    val Context.navigationBarHeight get() = resources.navigationBarHeight

    val Fragment.navigationBarHeight get() = requireContext().navigationBarHeight

}