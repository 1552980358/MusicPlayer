package projekt.cloud.piece.music.player.util

import android.content.Context
import android.util.Log
import java.io.File

object LyricUtil {
    
    private const val LYRIC_DIR = "lyric"
    private val Context.lyricDir get() = getExternalFilesDir(LYRIC_DIR)
    private fun Context.getLyricFile(id: String) = File(lyricDir, id)
    
    fun Context.writeLyric(id: String, lyric: Lyric) =
        getLyricFile(id).writeText(lyric.lrcFile)
    
    fun Context.loadLyric(id: String): Lyric? = getLyricFile(id).run {
        when {
            exists() -> readLines().decodeLines()
            else -> null
        }
    }
    
    val List<String>.decodeLyric get() = decodeLines()
    
    private fun List<String>.decodeLines() = Lyric().apply {
        forEach { line ->
            line.decodeLine()?.let {
                when {
                    isEmpty -> this += LyricItem(it).apply { this += line.lyricLine }
                    else -> when (last.time) {
                        it -> last += line.lyricLine
                        else -> this += LyricItem(it).apply { this += line.lyricLine }
                    }
                }
            }
        }
    }
    
    private fun String.decodeLine(): Long? = if (matches(Regex("\\[[0-9]{2}:[0-9]{2}\\.[0-9]{2,3}].+"))) timeLong else null
    
    private val String.timeLong get() =
        replace("[^0-9]".toRegex(), "").run { divideSec + millisecond }
    
    private val String.divideSec get() = (substring(0, 2).toLong() * 60 + substring(2, 4).toLong()) * 1000
    
    private val String.millisecond get() = substring(4).run {
        if (length == 3) this else this + '0'
    }.toLong()
    
    val Long.timeStrWithBracket get() = "[$timeStr]"
    
    private val Long.timeMin get() = this / 60000
    
    private val Long.timeSec get() = (this / 1000) % 60
    
    private val Long.timeMs get() = this % 1000
    
    private val Long.timeStr get() = timeMin.with2DigitLeadingZero + ':' +
        timeSec.with2DigitLeadingZero + '.' +
        timeMs.with3DigitLeadingZero
    
    private val String.lyricLine get() = substring(indexOf(']') + 1)
    
    private val Long.with3DigitLeadingZero get() = when (this) {
        in (100 .. 999) -> this.toString()
        in (10 .. 99) -> "0$this"
        in (0 .. 9) -> "00$this"
        else -> "000"
    }
    
    private val Long.with2DigitLeadingZero get() = if (this > 9) this.toString() else "0$this"
    
}