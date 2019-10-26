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
import com.google.gson.JsonParser
import okhttp3.*
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit

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
        
        val fileCover = File(Application.extDataDir_cover, (data[Id] as String).plus(".png"))
        val fileLyric = File(Application.extDataDir_lyric, (data[Id] as String).plus(".lrc"))
        
        /* Take file from storage */
        try {
            fileCover.apply {
                if (exists()) {
                    // Empty file
                    if (this.length() == 0L) {
                        data[AlbumCover] = false
                        return null
                    }
                    BitmapFactory.decodeFile(path).apply {
                        data[AlbumCover] = this
                        return this
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
                    if (this != null && isNotEmpty()) {
                        data[AlbumCover] = BitmapFactory.decodeByteArray(this, 0, size)
                        fileCover.writeBytes(this)
                    }
                }
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        if (data[AlbumCover] != null) {
            return data[AlbumCover] as Bitmap
        }
        
        try {
            /* Get From 163 server */
            JsonParser().parse(Request.Builder().header(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36"
            ).url("http://music.163.com/api/search/pc").post(FormBody.Builder().apply {
                add("s", title())
                add("offset", "0")
                add("limit", "1")
                add("type", "1")
            }.build()).build().run {
                OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS).build().newCall(this)
                    .execute().body!!.string()
            }).asJsonObject.get("result").asJsonObject.get("songs").asJsonArray.apply {
                if (this.size() == 0) {
                    data[AlbumCover] = false
                    return null
                }
                
                first().asJsonObject.apply {
                    /* Fetch and save album cover */
                    BitmapFactory.decodeStream(URL(get("album").asJsonObject.get("picUrl").asString).openStream())
                        .apply {
                            // Empty content
                            if (this == null || byteCount == 0)
                                data[AlbumCover] = false
                            
                            // Save
                            data[AlbumCover] = this
                            fileCover.outputStream().apply {
                                compress(Bitmap.CompressFormat.PNG, 100, this)
                                flush()
                                close()
                            }
                        }
                    
                    if (!fileLyric.exists() || fileLyric.length() == 0L) {
                        fileLyric.createNewFile()
                        
                        val text: String
                        /* Decode JSON code */
                        URL(
                            "http://music.163.com/api/song/media?id=${first().asJsonObject.get(
                                "id"
                            ).asString}"
                        ).openStream().apply {
                            bufferedReader().apply {
                                text = JsonParser().parse(readText())
                                    .asJsonObject.get("lyric").asString
                                close()
                            }
                            close()
                        }
                        
                        fileLyric.createNewFile()
                        
                        // Do nothing when empty
                        if (text.isNotEmpty()) {
                            fileLyric.writeText(StringBuilder().apply {
                                // Output into lrc file
                                text.reader().buffered().apply {
                                    readLines().forEach {
                                        if (it.isEmpty() || !it.startsWith('[') ||
                                            it[1] in 'a'..'z' || it[1] in 'A'..'Z' ||
                                            !it.contains(']') ||
                                            it.substring(it.lastIndexOf(']') + 1).isEmpty()
                                        ) {
                                            append(it.plus("\n"))
                                        }
                                    }
                                    close()
                                }
                            }.toString())
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.getStack()
        }
        
        if (data[AlbumCover] == null) {
            data[AlbumCover] = false
            fileCover.createNewFile()
        }
        
        if (!fileLyric.exists()) {
            fileLyric.createNewFile()
        }
        
        if (data[AlbumCover] is Boolean)
            return null
        
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