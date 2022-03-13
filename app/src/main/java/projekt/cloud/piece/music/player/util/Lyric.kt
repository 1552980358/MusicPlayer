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
    
    val last get() = _lyricItemList.last()
    
    val lrcFile get() =
        arrayListOf<String>().apply { _lyricItemList.forEach { add(it.lrcLine) } }.joinToString("\n")
    
}