package projekt.cloud.piece.music.player.util

import android.graphics.Bitmap
import projekt.cloud.piece.music.player.database.item.AlbumItem
import projekt.cloud.piece.music.player.database.item.ArtistItem
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.database.item.PlaylistItem

class MainActivityInterface {

    lateinit var audioList: List<AudioItem>
    lateinit var playlistList: List<PlaylistItem>
    lateinit var albumList: List<AlbumItem>
    lateinit var artistList: List<ArtistItem>

    val albumByteArrayRawMap = mutableMapOf<String, ByteArray?>()
    val albumBitmap40DpMap = mutableMapOf<String, Bitmap>()

}