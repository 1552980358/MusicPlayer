package sakuraba.saki.player.music.util

import projekt.cloud.piece.c2pinyin.pinyin
import sakuraba.saki.player.music.service.util.AudioInfo
import java.io.Serializable

data class Playlist(var title: String, var titlePinyin: String, var description: String): Serializable {
    constructor(title: String, description: String = ""): this(title, title.pinyin, description)

    val audioInfoList = arrayListOf<AudioInfo>()

    var size = 0

    fun forEach(forEachBlock: (AudioInfo) -> Unit) = audioInfoList.forEach(forEachBlock)

    operator fun plusAssign(audioInfo: AudioInfo) {
        if (!audioInfoList.contains(audioInfo) && audioInfoList.add(audioInfo)) {
            size = audioInfoList.size
        }
    }

    operator fun get(audioId: String) = audioInfoList.find { it.audioId == audioId }

    operator fun get(index: Int) = audioInfoList[index]

    operator fun set(index: Int, audioInfo: AudioInfo) {
        audioInfoList[index] = audioInfo
    }

}