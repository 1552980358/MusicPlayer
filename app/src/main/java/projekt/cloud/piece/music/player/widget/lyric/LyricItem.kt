package projekt.cloud.piece.music.player.widget.lyric

import java.io.Serializable

class LyricItem(val time: Long): Serializable {

    private val _lyricLineList = arrayListOf<String>()

    constructor(time: Long, lyric: String): this(time) {
        _lyricLineList.add(lyric)
    }

    operator fun plusAssign(lyric: String) {
        _lyricLineList.add(lyric)
    }

    override fun toString() = _lyricLineList.joinToString("\n")

}