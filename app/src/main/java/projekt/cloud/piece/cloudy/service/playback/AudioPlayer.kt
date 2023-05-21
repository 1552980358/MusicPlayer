package projekt.cloud.piece.cloudy.service.playback

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import projekt.cloud.piece.cloudy.util.Releasable

class AudioPlayer: Releasable {

    private var _exoPlayer: ExoPlayer? = null

    fun setupExoPlayer(context: Context): Player {
        return setupExoPlayer(
            buildExoPlayer(context, audioAttributes)
        )
    }

    private fun setupExoPlayer(exoPlayer: ExoPlayer): ExoPlayer {
        _exoPlayer = exoPlayer
        return exoPlayer
    }

    private val audioAttributes: AudioAttributes
        get() = AudioAttributes.Builder()
            .setContentType(AUDIO_CONTENT_TYPE_MUSIC)
            .build()

    private fun buildExoPlayer(context: Context, audioAttributes: AudioAttributes): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes,  true)
            .setHandleAudioBecomingNoisy(true)
            .build()
    }

    override fun release() {
        _exoPlayer?.release()
        _exoPlayer = null
    }

}