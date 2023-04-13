package projekt.cloud.piece.music.player.base.interfaces

import android.graphics.Rect

interface WindowInsetsInterface {

    val windowInsetsRequireListener: (Rect) -> Unit
        get() = onSetupRequireWindowInsets()

    fun onSetupRequireWindowInsets(): (Rect) -> Unit

}