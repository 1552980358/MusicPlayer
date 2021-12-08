package sakuraba.saki.player.music.util

import android.content.Context
import java.io.File
import lib.github1552980358.ktExtension.jvm.io.writingLn
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly

object LyricUtil {

    private const val LYRIC_DIR = "lyric"
    private const val LYRIC_EXT = ".lrc"

    private val Context.lyricDirFile get() = getExternalFilesDir(LYRIC_DIR)!!

    fun Context.checkLyricDir() {
        lyricDirFile.apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    inline fun createLyric(block: Lyric.() -> Unit) = Lyric().apply(block)

    fun Context.readLyric(id: String) = createLyric { readLyric(id, this) }

    fun Context.readLyric(id: String, lyric: Lyric) {
        File(lyricDirFile, id.plus(LYRIC_EXT)).apply {
            if (!exists()) {
                return
            }
            readLines().forEach { line -> line.decodeLine(lyric) }
        }
    }

    fun Context.writeLyric(id: String, lyric: Lyric) {
        if (lyric.isEmpty) {
            return
        }
        File(lyricDirFile, id.plus(LYRIC_EXT)).apply {
            if (!exists()) {
                createNewFile()
            }
        }.bufferedWriter().use { bufferedWriter ->
            lyric.forEach { timeLong, lyricStr ->
                bufferedWriter.writingLn(timeLong.timeStrWithBracket + lyricStr)
            }
            bufferedWriter.flush()
        }
    }

    fun Context.removeLyric(id: String) {
        File(lyricDirFile, id.plus(LYRIC_EXT)).apply {
            if (exists()) {
                delete()
            }
        }
    }

    fun Context.hasLyric(id: String) = File(lyricDirFile, id.plus(LYRIC_EXT)).exists()

    fun String.decodeLine(lyric: Lyric) {
        /**
         * Example: [00:00.00]lyric content...; or
         *          [00:00.000]lyric content...
         **/
        if (length < 11) {
            return
        }
        val indexBracket = indexOf(']')
        tryOnly {
            val timeLong = getTimeLong(indexBracket) ?: return
            val lyricStr = substring(indexBracket + 1)
            if (lyricStr.isNotEmpty() && !lyricStr.startsWith('[') && !lyricStr.endsWith(']')) {
                lyric.add(timeLong, lyricStr)
            }
        }
    }

    private val String.minute get() = substring(1, 3)
    private val String.second get() = substring(4, 6)
    private fun String.millisecond(indexBracket: Int): String {
        val mils = substring(7, indexBracket)
        return mils + (if (mils.length == 3) "" else '0')
    }

    private fun String.getTimeLong(indexBracket: Int): Long? {
        if (!startsWith('[') || indexBracket == -1 || (indexBracket != 9 && indexBracket != 10)) {
            return null
        }
        return (minute.toLong() * 60 + second.toLong()) * 1000 + millisecond(indexBracket).toLong()
    }

    val Long.with3DigitLeadingZero get() = when (this) {
        in (100 .. 999) -> this.toString()
        in (10 .. 99) -> "0$this"
        in (0 .. 9) -> "00$this"
        else -> "000"
    }

    val Long.with2DigitLeadingZero get() = if (this > 9) this.toString() else "0$this"

    private val Long.timeStrWithBracket get() = "[$timeStr]"

    val Long.timeMin get() = this / 60000

    val Long.timeSec get() = (this / 1000) % 60

    val Long.timeMs get() = this % 1000

    val Long.timeStr get() = timeMin.with2DigitLeadingZero + ':' +
            timeSec.with2DigitLeadingZero + '.' +
            timeMs.with3DigitLeadingZero

}