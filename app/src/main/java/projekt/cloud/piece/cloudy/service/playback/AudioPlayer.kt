package projekt.cloud.piece.cloudy.service.playback

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import projekt.cloud.piece.cloudy.util.Releasable

class AudioPlayer: Releasable {

    /**
     * [AudioPlayer._exoPlayer]
     * @type [androidx.media3.exoplayer.ExoPlayer]
     **/
    private var _exoPlayer: ExoPlayer? = null

    /**
     * [AudioPlayer.setupExoPlayer]
     * @param context [android.content.Context]
     * @return [androidx.media3.common.Player]
     **/
    fun setupExoPlayer(context: Context): Player {
        return setupExoPlayer(
            buildExoPlayer(context, audioAttributes)
        )
    }

    /**
     * [AudioPlayer.setupExoPlayer]
     * @param exoPlayer [androidx.media3.exoplayer.ExoPlayer]
     * @return [androidx.media3.exoplayer.ExoPlayer]
     **/
    private fun setupExoPlayer(exoPlayer: ExoPlayer): ExoPlayer {
        _exoPlayer = exoPlayer
        return exoPlayer
    }

    /**
     * [AudioPlayer.audioAttributes]
     * @type [androidx.media3.common.AudioAttributes]
     **/
    private val audioAttributes: AudioAttributes
        get() = AudioAttributes.Builder()
            .setContentType(AUDIO_CONTENT_TYPE_MUSIC)
            .build()

    /**
     * [AudioPlayer.buildExoPlayer]
     * @param context [android.content.Context]
     * @param audioAttributes [androidx.media3.common.AudioAttributes]
     * @return [androidx.media3.exoplayer.ExoPlayer]
     **/
    private fun buildExoPlayer(context: Context, audioAttributes: AudioAttributes): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes,  true)
            .setHandleAudioBecomingNoisy(true)
            .build()
    }

    /**
     * [Releasable.release]
     **/
    override fun release() {
        _exoPlayer?.release()
        _exoPlayer = null
    }

}