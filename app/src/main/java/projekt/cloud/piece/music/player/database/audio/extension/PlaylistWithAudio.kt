package projekt.cloud.piece.music.player.database.audio.extension

import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.database.audio.item.PlaylistItem

/**
 * [PlaylistWithAudio]
 *
 * Variables:
 * [playlistItem]
 * [audioList]
 *
 * Getters:
 * [title]
 * [size]
 * [sizeStr]
 * [plusAssign]
 * [minusAssign]
 *
 **/
data class PlaylistWithAudio(val playlistItem: PlaylistItem, val audioList: ArrayList<AudioItem> = ArrayList()) {

    constructor(playlistItem: PlaylistItem, audioList: List<AudioItem>): this(
        playlistItem,
        if (audioList is ArrayList<AudioItem>) audioList
        else audioList.toMutableList() as ArrayList<AudioItem>
    )

    val title get() = playlistItem.title

    val size get() = audioList.size

    val sizeStr get() = size.toString()

    operator fun plusAssign(audioItem: AudioItem) {
        audioList.add(audioItem)
    }

    operator fun minusAssign(audioItem: AudioItem) {
        audioList.remove(audioItem)
    }

    operator fun minusAssign(index: Int) {
        if (index in 0 .. audioList.lastIndex) {
            audioList.removeAt(index)
        }
    }

}