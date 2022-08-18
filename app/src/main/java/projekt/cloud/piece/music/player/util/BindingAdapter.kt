package projekt.cloud.piece.music.player.util

import android.graphics.Bitmap
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import projekt.cloud.piece.music.player.util.Values.ANIMATION_DURATION_LONG

object BindingAdapter {
    
    @JvmStatic
    @BindingAdapter("android:src")
    fun AppCompatImageView.setBitmapSrc(bitmap: Bitmap?) {
        if (bitmap != null) {
            setImageBitmap(bitmap)
        }
    }
    
    @JvmStatic
    @BindingAdapter("animatedAlpha")
    fun View.startAnimatedAlpha(alpha: Float) = animate()
        .alpha(alpha)
        .setDuration(ANIMATION_DURATION_LONG)
        .start()
    
}