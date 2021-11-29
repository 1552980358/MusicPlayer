package sakuraba.saki.player.music.util

class Lyric {
    val timeList = arrayListOf<Long>()
    val lyricList = arrayListOf<String>()
    val size get() = timeList.size
    val lastIndex get() = timeList.lastIndex
}