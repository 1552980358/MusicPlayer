package sakuraba.saki.player.music.util

class Lyric {

    private data class LyricItem(var timeLong: Long, var lyricStr: String)

    private val itemList = arrayListOf<LyricItem>()

    fun add(timeLong: Long, lyricStr: String) {
        itemList.add(LyricItem(timeLong, lyricStr))
        itemList.sortBy { it.timeLong }
    }

    fun remove(index: Int) {
        itemList.removeAt(index)
    }

    fun at(index: Int, block: (timeLong: Long, lyricStr: String) -> Unit) =
        itemList[index].let { block(it.timeLong, it.lyricStr) }

    fun forEach(block: (timeLong: Long, lyricStr: String) -> Unit) =
        itemList.forEach { block(it.timeLong, it.lyricStr) }

    fun forEachIndexed(block: (index: Int, timeLong: Long, lyricStr: String) -> Unit) =
        itemList.forEachIndexed { index, lyricItem -> block(index, lyricItem.timeLong, lyricItem.lyricStr) }

    fun timeAt(index: Int) = itemList[index].timeLong

    fun lyricAt(index: Int) = itemList[index].lyricStr

    val firstTime get() = itemList.first().timeLong

    val size get() = itemList.size

    val isEmpty get() = itemList.isEmpty()

    val isNotEmpty get() = itemList.isNotEmpty()

    val lastIndex get() = itemList.lastIndex

}
