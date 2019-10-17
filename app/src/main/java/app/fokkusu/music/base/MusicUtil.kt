package app.fokkusu.music.base

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import app.fokkusu.music.Application
import app.fokkusu.music.base.Constants.Companion.Album
import app.fokkusu.music.base.Constants.Companion.AlbumCover
import app.fokkusu.music.base.Constants.Companion.AlbumPY
import app.fokkusu.music.base.Constants.Companion.Artist
import app.fokkusu.music.base.Constants.Companion.ArtistPY
import app.fokkusu.music.base.Constants.Companion.BitRate
import app.fokkusu.music.base.Constants.Companion.Dir_Cover
import app.fokkusu.music.base.Constants.Companion.Duration
import app.fokkusu.music.base.Constants.Companion.Id
import app.fokkusu.music.base.Constants.Companion.Path
import app.fokkusu.music.base.Constants.Companion.Title
import app.fokkusu.music.base.Constants.Companion.TitlePY
import com.github.promeg.pinyinhelper.Pinyin
import java.io.File

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
    
    fun duration() = data[Duration] as Int
    
    fun addAlbumCover(bitmap: Bitmap) {
        data[AlbumCover] = bitmap
    }
    
    fun albumCover(): Bitmap? {
        /* Check from map */
        if (data[AlbumCover] != null) {
            if (data[AlbumCover]!! is Boolean) {
                return null
            }
            return data[AlbumCover] as Bitmap
        }
        
        val file = File(
            Application.getContext().externalCacheDir!!.absolutePath + File.separator + Dir_Cover,
            (data[Id] as String).plus(".jpg")
        )
        
        /* Take file from storage */
        if (Environment.isExternalStorageEmulated()) {
            try {
                file.apply{
                    if (exists()) {
                        BitmapFactory.decodeFile(path).apply {
                            data[AlbumCover] = this
                            return this
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    
        /* Take file from music file */
        try {
            MediaMetadataRetriever().apply {
                Application.getContext().contentResolver.openAssetFileDescriptor(
                    Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id()),
                    "r"
                )?.apply {
                    setDataSource(fileDescriptor)
                    close()
                }
        
                data[AlbumCover] = embeddedPicture.apply {
                    if (this == null || isEmpty()) {
                        data[AlbumCover] = false
                    }
            
                    data[AlbumCover] = BitmapFactory.decodeByteArray(this, 0, size)
                    file.writeBytes(this)
                }
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return data[AlbumCover] as Bitmap
    }
    
    @Deprecated("Seems Useless")
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