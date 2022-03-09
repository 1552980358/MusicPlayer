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
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION_HALF_LONG
import projekt.cloud.piece.music.player.widget.ProgressBar

object DataBindingUtil {
    
    @JvmStatic
    @BindingAdapter("app:isRefreshing")
    fun SwipeRefreshLayout.setRefresh(isRefreshing: Boolean?) {
        if (isRefreshing != null) {
            this.isRefreshing = isRefreshing
        }
    }
    
    @JvmStatic
    @BindingAdapter("app:drawable")
    fun AppCompatImageButton.playbackStateDrawable(@DrawableRes drawableRes: Int?) {
        if (drawableRes != null) {
            setImageResource(drawableRes)
            if (drawable != null) {
                (drawable as AnimatedVectorDrawable).start()
            }
        }
    }
    
    @JvmStatic
    @BindingAdapter("app:textAnimated")
    fun AppCompatTextView.setTextAnimated(newText: String?) {
        if (newText != null) {
            if (text.isNullOrBlank()) {
                return setText(newText)
            }
            ofFloat(1F, 0F).apply {
                duration = ANIMATION_DURATION_HALF_LONG
                addUpdateListener { alpha = animatedValue as Float }
                doOnEnd {
                    text = newText
                    ofFloat(0F, 1F).apply {
                        duration = ANIMATION_DURATION_HALF_LONG
                        addUpdateListener { alpha = animatedValue as Float }
                    }.start()
                }
            }.start()
        }
    }
    
    @JvmStatic
    @BindingAdapter("app:bitmapAnimated")
    fun AppCompatImageView.setBitmapAnimated(bitmap: Bitmap?) {
        if (bitmap != null) {
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
    
    @JvmStatic
    @BindingAdapter("app:progress")
    fun ProgressBar.setProgress(progress: Long? = null) {
        if (progress != null) {
            this.progress = progress
        }
    }
    
    @JvmStatic
    @BindingAdapter("app:duration")
    fun ProgressBar.setDuration(duration: Long? = null) {
        if (duration != null) {
            this.duration = duration
        }
    }
    
}