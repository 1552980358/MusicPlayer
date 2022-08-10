package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_NONE
import android.util.AttributeSet
import android.view.ViewPropertyAnimator
import androidx.appcompat.widget.AppCompatImageButton
import androidx.databinding.BindingAdapter
import projekt.cloud.piece.music.player.R

class ShuffleButton(context: Context, attributeSet: AttributeSet? = null): AppCompatImageButton(context, attributeSet) {
    
    companion object {
        
        @JvmStatic
        @BindingAdapter("shuffleMode")
        fun ShuffleButton.updateShuffleMode(@PlaybackStateCompat.ShuffleMode shuffleMode: Int?) {
            if (shuffleMode != null) {
                this.shuffleMode = shuffleMode
            }
        }
        
        private const val ALPHA_SHUFFLE_ALL = 1F
        private const val ALPHA_SHUFFLE_NONE = 0.5F
        private const val ALPHA_ANIMATION_DURATION = 200L
        
    }
    
    init {
        setImageResource(R.drawable.ic_round_shuffle_24)
    }
    
    @PlaybackStateCompat.ShuffleMode
    private var shuffleMode: Int? = null
        set(value) {
            val prev = field
            if (field != value) {
                field = value
                updateShuffleMode(prev != null)
            }
        }
    
    private var animator: ViewPropertyAnimator? = null
    
    private fun updateShuffleMode(requireAnimation: Boolean) {
        when {
            requireAnimation -> {
                animator?.cancel()
                animator = animate()
                    .alpha(shuffleModeAlpha)
                    .setDuration(ALPHA_ANIMATION_DURATION)
                    .withEndAction { animator = null }
                animator?.start()
            }
            else -> alpha = shuffleModeAlpha
        }
    }
    
    private val shuffleModeAlpha: Float
        get() = when (shuffleMode) {
            SHUFFLE_MODE_ALL -> ALPHA_SHUFFLE_ALL
            SHUFFLE_MODE_NONE -> ALPHA_SHUFFLE_NONE
            else -> ALPHA_SHUFFLE_ALL
        }
    
}