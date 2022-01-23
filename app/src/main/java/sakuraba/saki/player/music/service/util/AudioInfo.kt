package sakuraba.saki.player.music.service.util

import projekt.cloud.piece.c2pinyin.pinyin
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
            audioTitle.pinyin,
            audioArtist,
            audioArtist.pinyin,
            audioAlbum,
            audioAlbum.pinyin,
            audioAlbumId,
            audioDuration,
            audioSize,
            audioPath
        )
    var index = -1
}