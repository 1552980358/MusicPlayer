package sakuraba.saki.player.music.util

import android.content.Context
import java.io.File
import lib.github1552980358.ktExtension.jvm.io.writingLn

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

    fun Context.readLyric(id: String, lyricList: ArrayList<String>, timeList: ArrayList<Long>) {
        File(lyricDirFile, id.plus(LYRIC_EXT)).apply {
            if (!exists()) {
                return
            }
            readLines().forEach { line -> line.decodeLine(lyricList, timeList) }
        }
    }

    fun Context.writeLyric(id: String, lyricList: ArrayList<String>, timeList: ArrayList<Long>) {
        File(lyricDirFile, id.plus(LYRIC_EXT)).apply {
            if (!exists()) {
                createNewFile()
            }
        }.bufferedWriter().use { bufferedWriter ->
            timeList.forEachIndexed { index, l ->
                bufferedWriter.writingLn(l.timeStrWithBracket + lyricList[index])
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

    fun String.decodeLine(lyricList: ArrayList<String>, timeList: ArrayList<Long>) {
        /**
         * Example: [00:00.00]lyric content...; or
         *          [00:00.000]lyric content...
         **/
        if (length < 11) {
            return
        }
        val indexBracket = indexOf(']')
        val time = getTimeLong(indexBracket) ?: return
        val lyric = substring(indexBracket + 1)
        if (lyric.isNotEmpty() && !lyric.startsWith('[') && !lyric.endsWith(']')) {
            lyricList.add(lyric)
            timeList.add(time)
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

    private val Long.with3DigitLeadingZero get() = when (this) {
        in (100 .. 999) -> this.toString()
        in (10 .. 99) -> "0$this"
        in (0 .. 9) -> "00$this"
        else -> "000"
    }

    private val Long.with2DigitLeadingZero get() = if (this > 9) this.toString() else "0$this"

    private val Long.timeStrWithBracket get() = "[$timeStr]"

    val Long.timeStr get(): String {
        val secMils = this % 60000
        val sec = secMils / 1000
        return (this / 60000).with2DigitLeadingZero + ':' +
                sec.with2DigitLeadingZero + '.' +
                (secMils - sec * 1000).with3DigitLeadingZero
    }

}