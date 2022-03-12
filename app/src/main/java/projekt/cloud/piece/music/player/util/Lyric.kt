package projekt.cloud.piece.music.player.util

class Lyric {
    
    private val _lyricItemList = arrayListOf<LyricItem>()
    
    operator fun get(index: Int) = _lyricItemList[index]
    
    operator fun set(index: Int, lyricItem: LyricItem) {
        _lyricItemList[index] = lyricItem
    }
    
    operator fun plusAssign(lyricItem: LyricItem) {
        _lyricItemList.add(lyricItem)
    }
    
    operator fun minusAssign(lyricItem: LyricItem) {
        _lyricItemList.removeAll { it == lyricItem }
    }
    
    val lyricItemList: List<LyricItem> get() = _lyricItemList
    
    val size get() = _lyricItemList.size
    
    val isEmpty get() = _lyricItemList.isEmpty()
    
    fun sort() = _lyricItemList.sortBy { it.time }
    
    fun indexOf(position: Long) =
        _lyricItemList.indexOfFirst { it.time < position } - 1
    
    val last get() = _lyricItemList.last()
    
    val lrcFile get() =
        arrayListOf<String>().apply { _lyricItemList.forEach { add(it.lrcLine) } }.joinToString("\n")
    
}