package sakuraba.saki.player.music.web.util

import sakuraba.saki.player.music.service.util.AudioInfo
import java.io.Serializable

class WebControlUtil: Serializable {

    private fun interface OnPlay: Serializable {
        fun onPlay()
    }

    private fun interface OnPause: Serializable {
        fun onPause()
    }

    private fun interface OnSkipToPrevious: Serializable {
        fun onSkipToPrevious()
    }

    private fun interface OnSkipToNext: Serializable {
        fun onSkipToNext()
    }

    private fun interface OnPlayFromMediaId: Serializable {
        fun onPlayFromMediaId(pos: Int, mediaId: String, audioInfoList: ArrayList<AudioInfo>)
    }

    private var onPlay: OnPlay? = null
    private var onPause: OnPause? = null
    private var onSkipToPrevious: OnSkipToPrevious? = null
    private var onSkipToNext: OnSkipToNext? = null
    private var onPlayFromMediaId: OnPlayFromMediaId? = null
    var playServiceStarted = false
        set(value) {
            field = value
            if (!value) {
                // Clear all content prevent cannot serialized with intent
                onPlay = null
                onPause = null
                onSkipToPrevious = null
                onSkipToNext = null
                onPlayFromMediaId = null
            }
        }

    fun onPlay(play: () -> Unit) {
        this.onPlay = OnPlay(play)
    }

    fun onPause(pause: () -> Unit) {
        this.onPause = OnPause(pause)
    }

    fun onSkipToPrevious(skipToPrevious: () -> Unit) {
        this.onSkipToPrevious = OnSkipToPrevious(skipToPrevious)
    }

    fun onSkipToNext(skipToNext: () -> Unit) {
        this.onSkipToNext = OnSkipToNext(skipToNext)
    }

    fun onPlayFromMediaId(playFromMediaId: (pos: Int, mediaId: String, audioInfoLis: ArrayList<AudioInfo>) -> Unit) {
        this.onPlayFromMediaId = OnPlayFromMediaId(playFromMediaId)
    }

    fun set(block: WebControlUtil.() -> Unit) {
        block()
        playServiceStarted = true
    }

    val play get() = onPlay?.onPlay()
    val pause get() = onPause?.onPause()
    val skipToNext get() = onSkipToNext?.onSkipToNext()
    val skipToPrevious get() = onSkipToPrevious?.onSkipToPrevious()
    fun playFromMediaId(pos: Int, mediaId: String, audioInfoList: ArrayList<AudioInfo>) = onPlayFromMediaId?.onPlayFromMediaId(pos, mediaId, audioInfoList)

}