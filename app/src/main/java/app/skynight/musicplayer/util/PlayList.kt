package app.skynight.musicplayer.util

/**
 * @File    : PlayList
 * @Author  : 1552980358
 * @Date    : 6 Aug 2019
 * @TIME    : 9:58 PM
 **/
class PlayList(val playListName: String, val date: String) {
    private var musicInfoList: MutableList<MusicInfo>
    var isInitialCompleted = false

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
    @Synchronized
    fun setMusicInfoList(musicInfoList: MutableList<MusicInfo>) {
        this.musicInfoList = musicInfoList
    }

    fun getPlayList(): MutableList<MusicInfo> {
        return musicInfoList
    }
}