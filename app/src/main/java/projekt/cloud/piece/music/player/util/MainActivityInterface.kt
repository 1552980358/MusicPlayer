package projekt.cloud.piece.music.player.util

import android.graphics.Bitmap
import android.support.v4.media.MediaBrowserCompat
import projekt.cloud.piece.music.player.database.item.AlbumItem
import projekt.cloud.piece.music.player.database.item.ArtistItem
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.database.item.PlaylistItem

class MainActivityInterface(val itemClick: (Int) -> Unit, val requestRefresh: () -> Unit) {

    lateinit var audioList: List<AudioItem>
    lateinit var playlistList: List<PlaylistItem>
    lateinit var albumList: List<AlbumItem>
    lateinit var artistList: List<ArtistItem>

    val albumBitmap40DpMap = mutableMapOf<String, Bitmap>()
    val audioBitmap40DpMap = mutableMapOf<String, Bitmap>()
    val playlistBitmap40DpMap = mutableMapOf<String, Bitmap>()

    lateinit var refreshStageChanged: () -> Unit
    lateinit var refreshCompleted: () -> Unit

    fun setRefreshListener(refreshStageChanged: () -> Unit, refreshCompleted: () -> Unit) {
        this.refreshStageChanged = refreshStageChanged
        this.refreshCompleted = refreshCompleted
    }
    
    private lateinit var mediaBrowserCompat: MediaBrowserCompat
    fun setMediaBrowserCompat(mediaBrowserCompat: MediaBrowserCompat) {
        this.mediaBrowserCompat = mediaBrowserCompat
    }
    
    lateinit var defaultAudioImage: Bitmap

}