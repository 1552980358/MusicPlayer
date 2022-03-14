package projekt.cloud.piece.music.player.util

import android.animation.ValueAnimator.ofFloat
import android.graphics.Bitmap
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnEnd
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION_HALF_LONG
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION_LONG
import projekt.cloud.piece.music.player.widget.Seekbar

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
    fun AppCompatImageView.playbackStateDrawable(@DrawableRes drawableRes: Int?) {
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
            if (text != newText) {
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
    }
    
    @JvmStatic
    @BindingAdapter("app:bitmapAnimated")
    fun AppCompatImageView.setBitmapAnimated(bitmap: Bitmap?) {
        if (bitmap != null) {
            return setDrawableAnimated(BitmapDrawable(resources, bitmap))
        }
    }
    
    @JvmStatic
    @BindingAdapter("app:drawableAnimated")
    fun AppCompatImageView.setDrawableAnimated(newDrawable: Drawable?) {
        if (newDrawable != null) {
            if (drawable == null) {
                return setImageDrawable(newDrawable)
            }
            TransitionDrawable(
                arrayOf(
                    when (val lastDrawable = drawable) {
                        is TransitionDrawable -> lastDrawable.getDrawable(1)
                        else -> drawable
                    },
                    newDrawable)
            ).apply {
                setImageDrawable(this)
                startTransition(ANIMATION_DURATION)
            }
        }
    }
    
    @JvmStatic
    @BindingAdapter("app:isPlaying")
    fun FloatingActionButton.updatePlayingState(isPlaying: Boolean?) {
        if (isPlaying != null) {
            setImageResource(if (isPlaying) R.drawable.ani_play_pause else R.drawable.ani_pause_play)
            when (val imageDrawable = drawable) {
                is AnimatedVectorDrawable -> imageDrawable.start()
            }
        }
    }
    
    @JvmStatic
    @BindingAdapter("app:primaryColor")
    fun Seekbar.setPrimaryColor(@ColorInt color: Int?) {
        if (color != null) {
            setProgressColor(color)
        }
    }
    
    @JvmStatic
    @BindingAdapter("app:secondaryColor")
    fun Seekbar.setSecondaryColor(@ColorInt color: Int?) {
        if (color != null) {
            setRemainColor(color)
        }
    }
    
    @JvmStatic
    @BindingAdapter("app:circleColor")
    fun Seekbar.setCircleColor(@ColorInt color: Int?) {
        if (color != null) {
            setCircleColor(color)
        }
    }
    
    @JvmStatic
    @BindingAdapter("app:progress")
    fun Seekbar.setProgress(progress: Long?) {
        if (progress != null) {
            this.progress = progress
        }
    }
    
    @JvmStatic
    @BindingAdapter("app:duration")
    fun Seekbar.setDuration(duration: Long?) {
        if (duration != null) {
            this.duration = duration
        }
    }
    
    @JvmStatic
    @BindingAdapter("app:alphaAnimated")
    fun View.setAlphaAnimated(alpha: Float?) {
        if (alpha != null) {
            ofFloat(this.alpha, alpha).apply {
                duration = ANIMATION_DURATION_LONG
                addUpdateListener { this@setAlphaAnimated.alpha = animatedValue as Float }
            }.start()
        }
    }
    
}