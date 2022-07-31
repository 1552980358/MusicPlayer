package projekt.cloud.piece.music.player.util

import android.graphics.Bitmap
import android.graphics.Matrix

object BitmapUtil {
    
    val Bitmap.heightF: Float
        get() = height.toFloat()
    
    val Bitmap.widthF: Float
        get() = width.toFloat()
    
    fun Bitmap.resize(width: Int) = resize(width, width)
    
    fun Bitmap.resize(width: Int, height: Int) = Bitmap.createBitmap(
        this, 0, 0, getWidth(), getWidth(), Matrix().apply { setScale(width / widthF, height / heightF) }, false
    )
    
}