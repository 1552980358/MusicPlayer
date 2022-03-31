package projekt.cloud.piece.music.player.service.play

import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_MUSIC
import android.media.AudioAttributes.USAGE_MEDIA
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.AUDIOFOCUS_GAIN
import android.media.AudioManager.AUDIOFOCUS_LOSS
import android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
import android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.AudioManager.STREAM_MUSIC
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.annotation.RequiresApi

class AudioFocus(context: Context,
                 private val onGain: () -> Unit,
                 private val onLoss: () -> Unit,
                 private val onLossTransient: () -> Unit,
                 private val onLossTransientCanDuck: () -> Unit): OnAudioFocusChangeListener {

    private abstract class AudioFocusRequestImpl {
        abstract fun request(): Int
        abstract fun release()
    }

    @RequiresApi(O)
    private inner class AudioFocusRequestImpl26: AudioFocusRequestImpl() {

        private val audioFocusRequest = AudioFocusRequest.Builder(AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(USAGE_MEDIA)
                    .setContentType(CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAcceptsDelayedFocusGain(false)
            .setOnAudioFocusChangeListener(this@AudioFocus)
            .build()

        override fun request() = audioManager.requestAudioFocus(audioFocusRequest)

        override fun release() {
            audioManager.abandonAudioFocusRequest(audioFocusRequest)
        }

    }

    private inner class AudioFocusRequestImpl21: AudioFocusRequestImpl() {

        override fun request() =
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(this@AudioFocus, STREAM_MUSIC, AUDIOFOCUS_GAIN)

        @Suppress("DEPRECATION")
        override fun release() {
            audioManager.abandonAudioFocus(this@AudioFocus)
        }

    }

    private val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager

    private var audioFocusRequestImpl: AudioFocusRequestImpl = when {
        SDK_INT >= O -> AudioFocusRequestImpl26()
        else -> AudioFocusRequestImpl21()
    }

    var isGained = false

    var needRelease = true

    fun request(): Int {
        isGained = true
        return audioFocusRequestImpl.request()
    }

    fun release() {
        when {
            needRelease -> {
                isGained = false
                audioFocusRequestImpl.release()
            }
            else -> needRelease = true
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AUDIOFOCUS_GAIN -> onGain()
            AUDIOFOCUS_LOSS -> onLoss()
            AUDIOFOCUS_LOSS_TRANSIENT -> onLossTransient()
            AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> onLossTransientCanDuck()
        }
    }

}