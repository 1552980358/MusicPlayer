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
         * Example: [00:00.00]lyric content...
         **/
        if (length < 11) {
            return
        }
        val time = getTimeLong ?: return
        val lyric = substring(10)
        if (lyric.isNotEmpty()) {
            lyricList.add(lyric)
            timeList.add(time)
        }
    }

    private val String.getTimeLong get(): Long? {
        if (!startsWith('[') || indexOf(']') != 9) {
            return null
        }
        return (substring(1, 3).toLong() * 60 + substring(4, 6).toLong()) * 1000 + substring(7, 9).toLong() * 10
    }

    private val Long.withLeadingZero get() = if (this > 9) "$this" else "0$this"

    val Long.timeStrWithBracket get() = "[$timeStr]"

    val Long.timeStr get(): String {
        val secMils = this % 60000
        val sec = secMils / 1000
        return (this / 60000).withLeadingZero + ':' +
                sec.withLeadingZero + '.' +
                ((secMils - sec * 1000) / 10).withLeadingZero
    }

}