package projekt.cloud.piece.music.player.util

data class LyricItem(val time: Long) {

    private val _lyricLines = arrayListOf<String>()
    
    operator fun plusAssign(lyricLine: String) {
        _lyricLines.add(lyricLine)
    }
    
    operator fun minusAssign(index: Int) {
        _lyricLines.removeAt(index)
    }
    
    operator fun minusAssign(lyricLine: String) {
        _lyricLines.removeAll { it == lyricLine }
    }
    
    operator fun get(index: Int) = _lyricLines[index]
    
    operator fun set(index: Int, lyricLine: String) {
        _lyricLines[index] = lyricLine
    }
    
    val lyricLines: List<String> get() = _lyricLines
    
    val size get() = _lyricLines.size
    
    override fun toString() = _lyricLines.joinToString(separator = "\n")
    
}