package app.skynight.musicplayer.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import app.skynight.musicplayer.MainApplication
import com.github.promeg.pinyinhelper.Pinyin
import java.io.Serializable

/**
 * @FILE:   MusicInfo
 * @AUTHOR: 1552980358
 * @DATE:   21 Jul 2019
 * @TIME:   7:50 PM
 **/

class MusicInfo(p: String, t: String?, ar: String?, al: String?, d: Int?) : Serializable {
    companion object {
        val preLoadedAlbumPic by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            BitmapFactory.decodeStream(
                MainApplication.getMainApplication().assets.open("unknown.png")
            )!!
        }

        const val PATH = "PATH"
        const val TITLE = "TITLE"
        const val ARTIST = "ARTIST"
        const val ALBUM = "ALBUM"
        const val DURATION = "DURATION"
        const val TitlePY = "TitlePY"
        const val ArtistPY = "ArtistPY"
        const val AlbumPY = "AlbumPY"
    }

    private val infoMap = mutableMapOf<String, String>()

    init {
        infoMap[PATH] = p
        infoMap[TITLE] = t ?: "-"
        infoMap[ARTIST] = ar ?: "-"
        infoMap[ALBUM] = al ?: "-"
        infoMap[DURATION] = d?.toString() ?: "0"
        infoMap[TitlePY] = Pinyin.toPinyin(infoMap[TITLE], "").toUpperCase()
        infoMap[ArtistPY] = Pinyin.toPinyin(infoMap[ARTIST], "").toUpperCase()
        infoMap[AlbumPY] = Pinyin.toPinyin(infoMap[ALBUM], "").toUpperCase()
    }

    @Suppress("unused")
    fun albumPic(): Bitmap {
        try {
            MediaMetadataRetriever().apply { setDataSource(infoMap[PATH]) }.embeddedPicture.apply {
                val b1 = BitmapFactory.decodeByteArray(this, 0, size)
                if (b1 != null) {
                    return b1
                }
                MediaMetadataRetriever().apply { setDataSource(infoMap[PATH]) }.embeddedPicture.apply {
                    val b2 = BitmapFactory.decodeByteArray(this, 0, size)
                    if (b2 != null) {
                        return b2
                    }
                    return preLoadedAlbumPic
                }
            }
        } catch (e: Exception) {
            return preLoadedAlbumPic
        }
    }

    fun path(): String = infoMap[PATH]!!

    fun title(): String = infoMap[TITLE]!!

    fun artist(): String = infoMap[ARTIST]!!

    fun album(): String = infoMap[ALBUM]!!

    fun duration(): Int = infoMap[DURATION]!!.toInt() / 1000

    fun titlePY(): String = infoMap[TitlePY]!!

    fun artistPY(): String = infoMap[ArtistPY]!!

    fun albumPY(): String = infoMap[AlbumPY]!!

    @Suppress("unused")
    fun bitRate(): Int {
        //return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE).toInt()
        return try {
            MediaMetadataRetriever().apply { setDataSource(infoMap[PATH]) }
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE).toInt()
        } catch (e: Exception) {
            0
        }
    }
}