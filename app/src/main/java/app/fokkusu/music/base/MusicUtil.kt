package app.fokkusu.music.base

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import app.fokkusu.music.base.Constants.Companion.Album
import app.fokkusu.music.base.Constants.Companion.AlbumCover
import app.fokkusu.music.base.Constants.Companion.AlbumPY
import app.fokkusu.music.base.Constants.Companion.Artist
import app.fokkusu.music.base.Constants.Companion.ArtistPY
import app.fokkusu.music.base.Constants.Companion.BitRate
import app.fokkusu.music.base.Constants.Companion.Duration
import app.fokkusu.music.base.Constants.Companion.Path
import app.fokkusu.music.base.Constants.Companion.Title
import app.fokkusu.music.base.Constants.Companion.TitlePY
import com.github.promeg.pinyinhelper.Pinyin
import java.lang.Exception

/**
 * @File    : MusicUtil
 * @Author  : 1552980358
 * @Date    : 4 Oct 2019
 * @TIME    : 7:36 PM
 **/

class MusicUtil(path: String, title: String, artist: String?, album: String?, duration: Int) {
    private val data = mutableMapOf<String, Any?>()
    var loc = -1
    
    init {
        data[Path] = path
        data[Title] = title
        data[Artist] = artist
        data[Album] = album
        data[Duration] = duration
        data[TitlePY] = Pinyin.toPinyin(title, "").toUpperCase()
        data[ArtistPY] = Pinyin.toPinyin(artist, "").toUpperCase()
        data[AlbumPY] = Pinyin.toPinyin(album, "").toUpperCase()
    }
    
    fun getDataMap() = data
    
    fun <T> getData(topic: String): T {
        @Suppress("UNCHECKED_CAST") return (data[topic] as T)
    }
    
    fun path() = data[Path] as String
    
    fun title() = data[Title] as String
    fun titlePY() = data[TitlePY] as String
    
    fun artist() = data[Artist] as String? ?: ""
    fun artistPY() = data[ArtistPY] as String
    
    fun album() = data[Album] ?: ""
    fun albumPY() = data[AlbumPY] ?: ""
    
    fun albumCover(): Bitmap? {
        return try {
            if (data[AlbumCover] == null && data[BitRate] == null) {
                MediaMetadataRetriever().apply {
                    setDataSource(path())
                    if (data[BitRate] == null) {
                        data[BitRate] =
                            extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE).toInt()
                    }
                }.embeddedPicture.run {
                    if (this == null || this.isEmpty()) {
                        null
                    } else {
                        BitmapFactory.decodeByteArray(this, 0, this.size)
                            .apply { data[AlbumCover] = this }
                    }
                }
            } else {
                data[AlbumCover] as Bitmap?
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun bitRate(): Int? {
        return try {
            if (data[AlbumCover] == null && data[BitRate] == null) {
                MediaMetadataRetriever().apply {
                    setDataSource(path())
                    embeddedPicture.apply {
                        if (this != null || this!!.isNotEmpty() ) {
                            data[AlbumCover] = BitmapFactory.decodeByteArray(this, 0, size)
                        }
                    }
                }.run {
                    extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE).toInt()
                        .apply { data[BitRate] = this }
                }
            } else {
                data[BitRate] as Int?
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}