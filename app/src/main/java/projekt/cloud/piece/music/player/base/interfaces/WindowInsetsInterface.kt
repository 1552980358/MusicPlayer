package projekt.cloud.piece.music.player.base.interfaces

import android.content.Context
import android.graphics.Rect
import projekt.cloud.piece.music.player.util.ContextUtil.requireWindowInsets

interface WindowInsetsInterface {

    fun requireWindowInset(context: Context) {
        context.requireWindowInsets(windowInsetsRequireListener)
    }

    private val windowInsetsRequireListener: (Rect) -> Unit
        get() = onSetupRequireWindowInsets()

    fun onSetupRequireWindowInsets(): (Rect) -> Unit

}