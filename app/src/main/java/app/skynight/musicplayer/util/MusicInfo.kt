package app.skynight.musicplayer.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import app.skynight.musicplayer.MainApplication

/**
 * @FILE:   MusicInfo
 * @AUTHOR: 1552980358
 * @DATE:   21 Jul 2019
 * @TIME:   7:50 PM
 **/

class MusicInfo {
    constructor(path: String) : super() {
        val mediaMetadataRetriever = MediaMetadataRetriever().apply {
            setDataSource(path)
        }

        this.path = path
        this.title =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        this.artist =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        this.album =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        this.albumPic = try {
            if (mediaMetadataRetriever.embeddedPicture.size > 1) BitmapFactory.decodeByteArray(
                mediaMetadataRetriever.embeddedPicture,
                0,
                mediaMetadataRetriever.embeddedPicture.size
            ) else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        this.bitRate =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                .toInt()
        this.duration =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                .toInt()
        Log.e(TAG, "\n\n$title $artist $album $bitRate $duration\n$path")
    }

    constructor(
        path: String?,
        title: String?,
        artist: String?,
        album: String?,
        albumPic: Bitmap?,
        bitRate: Int?,
        duration: Int?
    ) : super() {
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
        Log.e(TAG, "\n\n$title $artist $album $bitRate $duration\n$path")
    }

    companion object {
        const val TAG = "MusicInfo"
        val preLoadedAlbumPic =
            BitmapFactory.decodeStream(MainApplication.getMainApplication().assets.open("unknown.png"))!!
    }

    var path = "PATH"
    var title = "TITLE"
    var artist = "-"
    var album = "-"
    var albumPic = null as Bitmap?
    var bitRate = 0
    var duration = 0

    @Suppress("unused")
    fun albumPic(): Bitmap {
        albumPic ?: return preLoadedAlbumPic

        return albumPic as Bitmap
    }

}