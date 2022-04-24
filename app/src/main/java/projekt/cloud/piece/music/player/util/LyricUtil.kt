package projekt.cloud.piece.music.player.util

import android.content.Context
import projekt.cloud.piece.music.player.util.TryUtil.tryRun
import projekt.cloud.piece.music.player.widget.lyric.Lyric
import projekt.cloud.piece.music.player.widget.lyric.LyricItem
import java.io.File

/**
 * Object [LyricUtil]
 *
 * Constant
 *  [LYRIC_DIR]
 *
 * Getters
 *  [lyricDir]
 *  [blanketedTimeStr]
 *  [timeStr]
 *  [minuteStr]
 *  [secondStr]
 *  [millisecondStr]
 *  [decodeLines]
 *  [decodeLine]
 *  [time]
 *  [second]
 *  [millisecond]
 *  [lyricLine]
 *
 * Methods
 *  [getLyricFile]
 *  [storeLyric]
 *  [readLyric]
 *  [writeLyric]
 *  [implementLyric]
 *
 **/
object LyricUtil {

    private const val LYRIC_DIR = "lyric"
    private val Context.lyricDir get() = getExternalFilesDir(LYRIC_DIR)
    private fun Context.getLyricFile(audio: String) = File(lyricDir, audio)

    fun Lyric.storeLyric(context: Context, audio: String) =
        context.getLyricFile(audio).writeLyric(this)

    fun Context.readLyric(audio: String) = tryRun {
        getLyricFile(audio).readLines().decodeLines
    }

    private fun File.writeLyric(lyric: Lyric) = StringBuilder().run {
        var time: String
        lyric.forEach { lyricItem ->
            time = lyricItem.time.blanketedTimeStr
            lyricItem.forEach { append(time.plus(it)) }
        }
        writeText(toString())
    }

    private val Long.blanketedTimeStr get() = "[${timeStr}]"

    private val Long.timeStr get() = "${minuteStr}:${secondStr}.${millisecondStr}"

    private val Long.minuteStr get() = this / 60000

    private val Long.secondStr get() = (this / 1000) % 60

    private val Long.millisecondStr get() = this % 1000

    private val List<String>.decodeLines: Lyric? get() =
        if (isEmpty()) null
        else implementLyric(Lyric())

    private fun List<String>.implementLyric(lyric: Lyric): Lyric {
        forEach { line ->
            line.decodeLine?.let {
                when {
                    lyric.isEmpty -> lyric += LyricItem(it, line.lyricLine)
                    lyric.last.time == it -> lyric.last += line.lyricLine
                    else -> lyric += LyricItem(it, line.lyricLine)
                }
            }
        }
        return lyric
    }

    private val String.decodeLine get() =
        if (matches(Regex("\\[[0-9]{2}:[0-9]{2}\\.[0-9]{2,3}].+"))) time
        else null

    private val String.time get() =
        replace("[^0-9]".toRegex(), "").run { second + millisecond }

    private val String.second get() =
        (substring(0, 2).toLong() * 60 + substring(2, 4).toLong()) * 1000

    private val String.millisecond get() = 
        substring(4).run { 
            if (length == 3) this 
            else this.plus('0')
        }.toLong()

    private val String.lyricLine get() = substring(indexOf(']') + 1)

}