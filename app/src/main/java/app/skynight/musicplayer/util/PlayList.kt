package app.skynight.musicplayer.util

import android.os.Environment
import app.skynight.musicplayer.util.MusicClass.Companion.TargetDir
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.io.Serializable
import java.lang.Exception

/**
 * @File    : PlayList
 * @Author  : 1552980358
 * @Date    : 6 Aug 2019
 * @TIME    : 9:58 PM
 **/

class PlayList(val playListName: String): Serializable {
    companion object {
        private const val PlayLists = "PlayLists"
        val playListList = mutableListOf<PlayList>()
        @Synchronized
        fun addPlayList(playList: PlayList) {
            playListList.add(playList)
        }

        fun loadAllPlayLists(): Int {
            val file = File(TargetDir, PlayLists)
            if (!file.exists()) {
                file.mkdirs()
                return 0
            }
            if (file.listFiles().isEmpty()) {
                return 0
            }
            file.listFiles().forEach {
                try {
                    val inputStream = FileInputStream(it)
                    val list: Any

                    ObjectInputStream(inputStream).apply {
                        list = readObject()
                        inputStream.close()
                        close()
                    }

                    @Suppress("UNCHECKED_CAST")
                    playListList.add(list as PlayList)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return playListList.size
        }
    }
    private var musicInfoList: MutableList<MusicInfo>
    lateinit var date: String

    init {
        this.musicInfoList = mutableListOf()
    }

    @Synchronized
    fun addMusicInfo(musicInfo: MusicInfo) {
        musicInfoList.add(musicInfo)
    }
    @Synchronized
    fun clearAllMusicInfo() {
        musicInfoList.clear()
    }

    fun getPlayList(): MutableList<MusicInfo> {
        return musicInfoList
    }
}