package projekt.cloud.piece.music.player.util

import android.animation.ValueAnimator.ofFloat
import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnEnd
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION
import projekt.cloud.piece.music.player.util.Constant.ANIMATION_DURATION_HALF

object BindingAdapter {

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