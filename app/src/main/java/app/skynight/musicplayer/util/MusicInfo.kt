package app.skynight.musicplayer.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
    //private var albumId = null as String?
    private var duration = 0
    //private var bitRate = 0

    constructor(p: String, t: String?, ar: String?, al: String?, /*alId: String?,*/ d: Int?) : super() {
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
        /*
        alId?.let {
            albumId = alId
        }
         */
    }

    @Deprecated("")
    private lateinit var mediaMetadataRetriever: MediaMetadataRetriever

    @Suppress("DEPRECATION")
    @Deprecated("")
    constructor(path: String) : super() {
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
    constructor() : super() {
        throw Exception("???")
    }

    companion object {
        val preLoadedAlbumPic by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            BitmapFactory.decodeStream(
                MainApplication.getMainApplication().assets.open("unknown.png")
            )!!
        }
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
                val b1 =  BitmapFactory.decodeByteArray(this, 0, size)
                if (b1 != null) {
                    return b1
                }
                MediaMetadataRetriever().apply { setDataSource(path) }.embeddedPicture.apply {
                    val b2 =  BitmapFactory.decodeByteArray(this, 0, size)
                    if (b2 != null) {
                        return b2
                    }
                    return preLoadedAlbumPic
                }
            }
        } catch (e: Exception) {
            return preLoadedAlbumPic
        }

        /*
        albumId ?: return preLoadedAlbumPic
        try {
            var path = null as String?
            MainApplication.getMainApplication().contentResolver.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART),
                MediaStore.Audio.Albums._ID + "=?",
                arrayOf(albumId),
                null
            ).apply {
                this?:return preLoadedAlbumPic

                if (moveToNext()) {
                    path = getStringOrNull(getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
                }
                close()
            }
            path?:return preLoadedAlbumPic
            val bitmap = BitmapFactory.decodeFile(path)
            //bitmap?:return preLoadedAlbumPic
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return preLoadedAlbumPic
        }
         */
    }

    fun title(): String {
        //return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        return title
    }

    fun artist(): String {
        //return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        return artist
    }

    @Suppress("unused")
    fun album(): String {
        //return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        return album
    }

    fun duration(): Int {
        //return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toInt()
        return duration / 1000
    }

    @Suppress("unused")
    fun bitRate(): Int {
        //return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE).toInt()
        return try {
            MediaMetadataRetriever().apply { setDataSource(path) }
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE).toInt()
        } catch (e: Exception) {
            0
        }
    }
}