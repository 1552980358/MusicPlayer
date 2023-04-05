package projekt.cloud.piece.music.player.util

import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import projekt.cloud.piece.music.player.R

class PlaybackStateManager {

    @PlaybackStateCompat.State
    var playbackState = STATE_NONE
        @Synchronized
        private set

    fun updatePlaybackState(@PlaybackStateCompat.State newState: Int): Int {
        return getPlaybackDrawableId(newState, playbackState)
    }

    private fun getPlaybackDrawableId(state: Int, lastState: Int): Int {
        playbackState = state
        return when (lastState) {
            STATE_NONE -> when (state) {
                STATE_PLAYING -> { R.drawable.ic_round_pause_24 }
                STATE_BUFFERING -> { R.drawable.av_round_play_to_pause_24 }
                else -> { R.drawable.ic_round_play_24 }
            }
            STATE_PLAYING -> when (state) {
                // STATE_BUFFERING -> { R.drawable.ic_round_pause_24 }
                STATE_PAUSED -> { R.drawable.av_round_pause_to_play_24 }
                // Keep unchanged
                else -> { R.drawable.ic_round_pause_24 }
            }
            STATE_PAUSED -> when (state) {
                STATE_BUFFERING, STATE_PLAYING -> { R.drawable.av_round_play_to_pause_24 }
                else -> { R.drawable.ic_round_play_24 }
            }
            STATE_BUFFERING -> when (state) {
                // STATE_PLAYING -> { R.drawable.ic_round_pause_24 }
                STATE_PAUSED -> { R.drawable.av_round_pause_to_play_24 }
                // Keep unchanged
                else -> { R.drawable.ic_round_pause_24 }
            }
            else -> R.drawable.ic_round_pause_24
        }
    }

}