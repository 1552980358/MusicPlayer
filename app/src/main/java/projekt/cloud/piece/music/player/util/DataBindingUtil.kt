package projekt.cloud.piece.music.player.util

import android.animation.ValueAnimator.ofFloat
import android.graphics.Bitmap
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.TransitionDrawable
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnEnd
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION_LONG

object DataBindingUtil {
    
    @JvmStatic
    @BindingAdapter("app:isRefreshing")
    fun SwipeRefreshLayout.setRefresh(isRefreshing: Boolean) {
        this.isRefreshing = isRefreshing
    }
    
    @JvmStatic
    @BindingAdapter("app:drawable")
    fun AppCompatImageButton.playbackStateDrawable(@DrawableRes drawableRes: Int) {
        setImageResource(drawableRes)
        (drawable as AnimatedVectorDrawable).start()
    }
    
    @JvmStatic
    @BindingAdapter("app:textAnimated")
    fun AppCompatTextView.setTextAnimated(newText: String) {
        ofFloat(1F, 0F).apply {
            duration = ANIMATION_DURATION_LONG
            addUpdateListener { alpha = animatedValue as Float }
            doOnEnd {
                text = newText
                ofFloat(0F, 1F).apply {
                    duration = ANIMATION_DURATION_LONG
                    addUpdateListener { alpha = animatedValue as Float }
                }.start()
            }
        }.start()
    }
    
    @JvmStatic
    @BindingAdapter("app:bitmapAnimated")
    fun AppCompatImageView.setBitmapAnimated(bitmap: Bitmap) {
        if (drawable == null) {
            return setImageBitmap(bitmap)
        }
        TransitionDrawable(
            arrayOf(
                when (val lastDrawable = drawable) {
                    is TransitionDrawable -> lastDrawable.getDrawable(1)
                    else -> drawable
                },
                BitmapDrawable(resources, bitmap))
        ).apply {
            setImageDrawable(this)
            startTransition(ANIMATION_DURATION)
        }
    }
    
}