package projekt.cloud.piece.music.player.util

import android.animation.ValueAnimator.ofFloat
import android.graphics.Bitmap
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnEnd
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION_HALF
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION_INT

object BindingAdapter {

    @JvmStatic
    @BindingAdapter("bitmapAnimated")
    fun AppCompatImageView.setBitmapAnimated(bitmap: Bitmap?) {
        if (bitmap != null) {
            return setDrawableAnimated(BitmapDrawable(resources, bitmap))
        }
    }

    @JvmStatic
    @BindingAdapter("drawableAnimated")
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
                startTransition(ANIMATION_DURATION_INT)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("alphaAnimated")
    fun View.setAlphaAnimated(alpha: Float?) {
        if (alpha != null) {
            ofFloat(this.alpha, alpha).apply {
                duration = ANIMATION_DURATION
                addUpdateListener { this@setAlphaAnimated.alpha = animatedValue as Float }
            }.start()
        }
    }

    @JvmStatic
    @BindingAdapter("isPlaying")
    fun FloatingActionButton.updatePlayingState(isPlaying: Boolean?) {
        if (isPlaying != null) {
            if (drawable == null) {
                return setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
            }
            setImageResource(if (isPlaying) R.drawable.ani_play_pause else R.drawable.ani_pause_play)
            when (val imageDrawable = drawable) {
                is AnimatedVectorDrawable -> imageDrawable.start()
            }
        }
    }

    @JvmStatic
    @BindingAdapter("textAnimated")
    fun AppCompatTextView.setTextAnimated(newText: String?) {
        if (newText != null) {
            if (text.isNullOrBlank()) {
                return setText(newText)
            }
            if (text != newText) {
                ofFloat(1F, 0F).apply {
                    duration = ANIMATION_DURATION_HALF
                    addUpdateListener { alpha = animatedValue as Float }
                    doOnEnd {
                        text = newText
                        ofFloat(0F, 1F).apply {
                            duration = ANIMATION_DURATION_HALF
                            addUpdateListener { alpha = animatedValue as Float }
                        }.start()
                    }
                }.start()
            }
        }
    }

}