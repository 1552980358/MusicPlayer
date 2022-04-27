package projekt.cloud.piece.music.player.util

import android.graphics.Bitmap

object BitmapUtil {

    @JvmStatic
    val Bitmap.widthF get() = width.toFloat()

    @JvmStatic
    val Bitmap.heightF get() = height.toFloat()

}