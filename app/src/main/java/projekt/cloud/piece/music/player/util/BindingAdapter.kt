package projekt.cloud.piece.music.player.util

import android.graphics.Bitmap
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter

object BindingAdapter {
    
    @JvmStatic
    @BindingAdapter("android:src")
    fun AppCompatImageView.setBitmapSrc(bitmap: Bitmap?) {
        if (bitmap != null) {
            setImageBitmap(bitmap)
        }
    }
    
}