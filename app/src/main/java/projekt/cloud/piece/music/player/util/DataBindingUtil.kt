package projekt.cloud.piece.music.player.util

import android.animation.ValueAnimator.ofArgb
import android.animation.ValueAnimator.ofFloat
import android.graphics.Bitmap
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION_HALF_LONG
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION_LONG
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
    
    @JvmStatic
    @BindingAdapter("app:color")
    fun CoordinatorLayout.setColor(@ColorInt color: Int) {
        if (background == null) {
            return setBackgroundColor(color)
        }
    
        when (val backgroundDrawable = background) {
            is ColorDrawable -> ofArgb(backgroundDrawable.color, color).apply {
                duration = ANIMATION_DURATION_LONG
                addUpdateListener { setBackgroundColor(animatedValue as Int) }
            }.start()
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
    
}