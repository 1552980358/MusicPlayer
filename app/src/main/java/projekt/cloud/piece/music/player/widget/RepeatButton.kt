package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_NONE
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ONE
import android.util.AttributeSet
import android.view.ViewPropertyAnimator
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.databinding.BindingAdapter
import projekt.cloud.piece.music.player.R

class RepeatButton(context: Context, attributeSet: AttributeSet? = null): AppCompatImageButton(context, attributeSet) {
    
    companion object {
        
        @JvmStatic
        @BindingAdapter("repeatMode")
        fun RepeatButton.updateRepeatMode(@PlaybackStateCompat.RepeatMode repeatMode: Int?) {
            this.repeatMode = repeatMode
        }
        
        private const val ALPHA_FULL = 1F
        private const val ALPHA_REPEAT_ONE = ALPHA_FULL
        private const val ALPHA_REPEAT_ALL = ALPHA_FULL
        private const val ALPHA_REPEAT_NONE = 0.5F
    
        private const val ALPHA_ANIMATION_DURATION = 200L
        
    }
    
    private var animator: ViewPropertyAnimator? = null
    
    @PlaybackStateCompat.RepeatMode
    private var repeatMode: Int? = null
        set(value) {
            if (value != null && value != field) {
                field = value
                setImageWithAlpha(getImageResId(value), getAlpha(value))
            }
        }
    
    private fun setImageWithAlpha(@DrawableRes resId: Int, alpha: Float) {
        animator?.cancel()
        setImageResource(resId)
        if (this.alpha != alpha) {
            animator = animate()
                .alpha(alpha)
                .setDuration(ALPHA_ANIMATION_DURATION)
                .withEndAction { animator = null }
            animator?.start()
        }
    }
    
    @DrawableRes
    private fun getImageResId(@PlaybackStateCompat.RepeatMode repeatMode: Int) = when (repeatMode) {
        REPEAT_MODE_ALL, REPEAT_MODE_NONE -> R.drawable.ic_round_repeat_24
        REPEAT_MODE_ONE -> R.drawable.ic_round_repeat_one_24
        else -> R.drawable.ic_round_repeat_24
    }
    
    private fun getAlpha(repeatMode: Int) = when (repeatMode) {
        REPEAT_MODE_ALL,  -> ALPHA_REPEAT_ALL
        REPEAT_MODE_NONE -> ALPHA_REPEAT_NONE
        REPEAT_MODE_ONE -> ALPHA_REPEAT_ONE
        else -> ALPHA_REPEAT_ALL
    }
    
}