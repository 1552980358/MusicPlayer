package sakuraba.saki.player.music.util

import java.io.Serializable

class MediaAlbum(val albumId: Long, val title: String, val titlePinyin: String): Serializable {
    constructor(albumId: Long, title: String, titlePinyin: String, numberOfAudio: Int): this(albumId, title, titlePinyin) {
        this.numberOfAudio = numberOfAudio
    }
    var numberOfAudio = 1
}