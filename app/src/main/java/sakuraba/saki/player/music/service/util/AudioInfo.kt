package sakuraba.saki.player.music.service.util

import com.github.promeg.pinyinhelper.Pinyin
import java.io.Serializable

class AudioInfo(
    val audioId: String,
    val audioTitle: String,
    val audioTitlePinyin: String,
    val audioArtist: String,
    val audioArtistPinyin: String,
    val audioAlbum: String,
    val audioAlbumPinyin: String,
    val audioAlbumId: Long,
    val audioDuration: Long,
    val audioSize: Long,
    val audioPath: String
    ): Serializable {
    constructor(audioId: String, audioTitle: String, audioArtist: String, audioAlbum: String, audioAlbumId: Long, audioDuration: Long, audioSize: Long, audioPath: String):
        this(
            audioId,
            audioTitle,
            Pinyin.toPinyin(audioTitle, ""),
            audioArtist,
            Pinyin.toPinyin(audioArtist, ""),
            audioAlbum,
            Pinyin.toPinyin(audioAlbum, ""),
            audioAlbumId,
            audioDuration,
            audioSize,
            audioPath
        )
    var index = -1
}