package app.skynight.musicplayer.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaExtractor
import android.media.MediaMetadataRetriever
import android.util.Log
import app.skynight.musicplayer.MainApplication
import java.io.Serializable

/**
 * @FILE:   MusicInfo
 * @AUTHOR: 1552980358
 * @DATE:   21 Jul 2019
 * @TIME:   7:50 PM
 **/

class MusicInfo : Serializable {
    lateinit var path: String
    private var title = "-"
    private var artist = "-"
    private var album = "-"
    private var duration = 0
    //private var bitRate = 0

    constructor(p: String, t: String?, ar: String?, al: String?, d: Int?): super() {
        path = p
        t?.let {
            title = it
        }
        ar?.let {
            artist = it
        }
        al?.let {
            album = it
        }
        d?.let {
            duration = d
        }
        /*
        b?.let {
            b
        }
         */
    }

    @Deprecated("")
    private lateinit var mediaMetadataRetriever: MediaMetadataRetriever
    @Suppress("DEPRECATION")
    @Deprecated("")
    constructor(path: String): super() {
        this.path = path
        mediaMetadataRetriever = MediaMetadataRetriever().apply {
            try {
                setDataSource(path)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MediaMetadataRetrieverE", path)
            }
        }
    }

    @Deprecated("")
    constructor(): super() {
        throw Exception("???")
    }

    companion object {
        val preLoadedAlbumPic by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { BitmapFactory.decodeStream(MainApplication.getMainApplication().assets.open("unknown.png"))!! }
    }

    @Suppress("unused")
    fun albumPic(): Bitmap {
        /*
        return try {
            BitmapFactory.decodeByteArray(mediaMetadataRetriever.embeddedPicture, 0, mediaMetadataRetriever.embeddedPicture.size)
        } catch (e: Exception) {
            preLoadedAlbumPic
        }
        */
        try {
            MediaMetadataRetriever().apply { setDataSource(path) }.embeddedPicture.apply {
                return BitmapFactory.decodeByteArray(this, 0, size)
            }
        } catch (e: Exception) {
            return preLoadedAlbumPic
        }
    }

    fun title(): String {
        //return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        return title
    }

    fun artist(): String {
        //return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        return artist
    }

    fun album(): String {
        //return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        return album
    }

    fun duration(): Int {
        //return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toInt()
        return duration / 1000
    }

    fun bitRate(): Int {
        //return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE).toInt()
        return try {
            MediaMetadataRetriever().apply { setDataSource(path) }.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE).toInt()
        } catch (e: Exception) {
            0
        }
    }
}