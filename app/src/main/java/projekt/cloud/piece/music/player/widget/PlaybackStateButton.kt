package projekt.cloud.piece.music.player.widget

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import projekt.cloud.piece.music.player.R

class PlaybackStateButton(context: Context, attributeSet: AttributeSet? = null): FloatingActionButton(context, attributeSet) {
    
    companion object {
        
        @JvmStatic
        @BindingAdapter("playbackState")
        fun PlaybackStateButton.updatePlaybackState(playbackState: Int?) {
            if (playbackState != null) {
                this.playbackState = playbackState
            }
        }
        
    }
    
    @Volatile
    private var playbackState: Int? = null
        set(value) {
            if (value != STATE_BUFFERING) {
                val prev = field
                field = value
                updatePlaybackState(prev, field!!)
            }
        }
    
    private fun updatePlaybackState(prev: Int?, current: Int) {
        when {
            
            (prev == STATE_PAUSED && current == STATE_PLAYING) ->
                setImageResource(R.drawable.anim_baseline_play_24)
            
            (prev == STATE_PLAYING && current == STATE_PAUSED) ->
                setImageResource(R.drawable.anim_baseline_pause_24)
            
            current == STATE_PLAYING ->
                setImageResource(R.drawable.ic_baseline_pause_24)
            
            current == STATE_PAUSED ->
                setImageResource(R.drawable.ic_baseline_play_arrow_24)
            
        }
        val drawable = drawable
        if (drawable is AnimatedVectorDrawable) {
            drawable.start()
        }
    }
    
    fun switchPlaybackState(): Int = when (playbackState) {
        STATE_PAUSED -> STATE_PLAYING
        else -> STATE_PAUSED
    }
    
}