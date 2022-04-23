package projekt.cloud.piece.music.player.widget.lyric

import java.io.Serializable

class Lyric: Serializable {

    private val _lyricItemList = arrayListOf<LyricItem>()

    val size get() = _lyricItemList.size

    val isEmpty get() = _lyricItemList.isEmpty()

    operator fun set(time: Long, str: String) {
        when (val lyricItem = _lyricItemList.find { it.time == time }) {
            null -> _lyricItemList.add(LyricItem(time, str))
            else -> lyricItem += str
        }
    }

    operator fun get(index: Int) = _lyricItemList[index]

    operator fun plusAssign(lyricList: List<LyricItem>) {
        _lyricItemList.addAll(lyricList)
    }

    operator fun plusAssign(lyricItem: LyricItem) {
        _lyricItemList.add(lyricItem)
    }

    val lastIndex get() = _lyricItemList.lastIndex

    fun indexOf(position: Long): Int {
        if (isEmpty) {
            return -2
        }

        if (position < _lyricItemList.first().time) {
            return -2
        }

        if (position > _lyricItemList.last().time) {
            return _lyricItemList.lastIndex
        }

        for (i in 1 until _lyricItemList.size) {
            if (position in _lyricItemList[i - 1].time until _lyricItemList[i].time) {
                return i - 1
            }
        }

        return -2
    }

}