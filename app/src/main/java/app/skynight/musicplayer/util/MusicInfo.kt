package app.skynight.musicplayer.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import app.skynight.musicplayer.MainApplication

/**
 * @FILE:   MusicInfo
 * @AUTHOR: 1552980358
 * @DATE:   21 Jul 2019
 * @TIME:   7:50 PM
 **/

class MusicInfo(
    path: String?,
    title: String?,
    artist: String?,
    album: String?,
    albumPic: Bitmap?,
    bitRate: String?,
    duration: Int?
) {
    companion object {
        val preLoadedAlbumPic = BitmapFactory.decodeStream(MainApplication.getMainApplication().assets.open("unknown.png"))!!
    }

    var path = "PATH"
    var title = "TITLE"
    var artist = "ARTIST"
    var album = "ALBUM"
    var albumPic = null as Bitmap?
    var bitRate = "UNKNOWN"
    var duration = 0

    init {
        path?.let {
            this.path = it
        }
        title?.let {
            this.title = it
        }
        artist?.let {
            this.artist = it
        }
        album?.let {
            this.album = it
        }
        albumPic?.let {
            this.albumPic = it
        }
        bitRate?.let {
            this.bitRate = it
        }
        duration?.let {
            this.duration = it
        }
    }

    @Suppress("unused")
    fun albumPic(): Bitmap {
        albumPic?: return preLoadedAlbumPic

        return albumPic as Bitmap
    }

}