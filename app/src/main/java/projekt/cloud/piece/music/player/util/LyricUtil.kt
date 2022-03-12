package projekt.cloud.piece.music.player.util

import android.content.Context
import java.io.File

object LyricUtil {
    
    private const val LYRIC_DIR = "lyric"
    private val Context.lyricDir get() = getExternalFilesDir(LYRIC_DIR)
    private fun Context.getLyricFile(id: String) = File(lyricDir, id)
    
    fun Context.loadLyric(id: String): Lyric? =getLyricFile(id).run {
        when {
            exists() -> readLines().decodeLines()
            else -> null
        }
    }
    
    private fun List<String>.decodeLines() = Lyric().apply {
        forEach { line ->
            line.decodeLine()?.let {
                when (last.time) {
                    it -> last += line.lyricLine
                    else -> this += LyricItem(it).apply { this += line.lyricLine }
                }
            }
        }
    }
    
    private fun String.decodeLine(): Long? = if (matches(Regex("\\[[0-9]{2}:[0-9]{2}:[0-9]{2,3}]\\S+"))) timeLong else null
    
    private val String.timeLong get() =
        replace("[^0-9]".toRegex(), "").run { divideSec + millisecond }
    
    private val String.divideSec get() = (substring(0, 2).toLong() * 60 + substring(2, 4).toLong()) * 1000
    
    private val String.millisecond get() = substring(4).run {
        if (length == 3) this else this + '0'
    }.toLong()
    
    private val String.lyricLine get() = substring(indexOf(']') + 1)
    
}