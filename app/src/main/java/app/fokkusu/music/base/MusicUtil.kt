package app.fokkusu.music.base

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import app.fokkusu.music.Application
import app.fokkusu.music.base.Constants.Companion.Album
import app.fokkusu.music.base.Constants.Companion.AlbumCover
import app.fokkusu.music.base.Constants.Companion.AlbumPY
import app.fokkusu.music.base.Constants.Companion.Artist
import app.fokkusu.music.base.Constants.Companion.ArtistPY
import app.fokkusu.music.base.Constants.Companion.BitRate
import app.fokkusu.music.base.Constants.Companion.Duration
import app.fokkusu.music.base.Constants.Companion.Id
import app.fokkusu.music.base.Constants.Companion.Path
import app.fokkusu.music.base.Constants.Companion.Title
import app.fokkusu.music.base.Constants.Companion.TitlePY
import com.github.promeg.pinyinhelper.Pinyin
import java.io.File
import java.lang.Exception

/**
 * @File    : MusicUtil
 * @Author  : 1552980358
 * @Date    : 4 Oct 2019
 * @TIME    : 7:36 PM
 **/

@Suppress("DuplicatedCode")
class MusicUtil(
    path: String, id: String, title: String, artist: String?, album: String?, duration: Int
) {
    private val data = mutableMapOf<String, Any?>()
    var loc = -1
    
    init {
        data[Path] = path
        data[Id] = id
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
    
    fun id() = data[Id] as String
    
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        Application.getContext().contentResolver.openAssetFileDescriptor(
                            Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id()),
                            "r"
                        )?.apply {
                            setDataSource(fileDescriptor)
                            close()
                        }
                    } else {
                        setDataSource(path())
                    }
                    
                    if (data[BitRate] == null) {
                        data[BitRate] =
                            extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE).toInt()
                    }
                }.run {
                    val pic = embeddedPicture
                    if (embeddedPicture == null) {
                        return@run null
                    }
                    BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.size).apply {
                        data[AlbumCover] = this
                        release()
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        Application.getContext().contentResolver.openAssetFileDescriptor(
                            Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id()),
                            "r"
                        )?.apply {
                            setDataSource(fileDescriptor)
                            close()
                        }
                    } else {
                        setDataSource(path())
                    }
                    embeddedPicture.apply {
                        if (this != null && this.isEmpty()) {
                            data[AlbumCover] = BitmapFactory.decodeByteArray(this, 0, this.size)
                                .apply { data[AlbumCover] = this }
                        }
                    }
                    
                }.run {
                    extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE).toInt()
                        .apply { data[BitRate] = this }.apply { release() }
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