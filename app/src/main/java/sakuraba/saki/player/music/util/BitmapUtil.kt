package sakuraba.saki.player.music.util

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.fragment.app.Fragment
import lib.github1552980358.ktExtension.android.graphics.getByteArray
import lib.github1552980358.ktExtension.android.java.readAsBitmap
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import java.io.File

object BitmapUtil {
    
    private val ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart")
    
    val Long.getURI get() = ContentUris.withAppendedId(ALBUM_ART_URI, this)
    
    @Suppress("HasPlatformType")
    fun Context.loadAlbumArt(albumId: Long) = loadAlbumArt(albumId.getURI)
    
    @Suppress("HasPlatformType")
    fun Context.loadAlbumArt(uri: String) = loadAlbumArt(Uri.parse(uri))
    
    @Suppress("HasPlatformType")
    fun Context.loadAlbumArt(uri: Uri) =
        BitmapFactory.decodeFileDescriptor(contentResolver.openFileDescriptor(uri, "r")?.fileDescriptor)
    
    fun Fragment.loadAlbumArt(albumId: Long) = requireContext().loadAlbumArt(albumId)

    private const val ALBUM_ART_40DP_DIR = "album_art_40dp"
    private val Context.albumArt40DpDir get() = getExternalFilesDir(ALBUM_ART_40DP_DIR)

    private fun Bitmap.writeBitmap(file: File) =
            getByteArray(format = Bitmap.CompressFormat.JPEG)?.apply { file.writeBytes(this) }

    fun Context.readAlbumArt(albumId: Long) = tryRun {
        val byteArray = File(albumArt40DpDir, albumId.toString()).readBytes()
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun Context.writeAlbumArt40Dp(albumId: Long, bitmap: Bitmap?) =
            bitmap?.writeBitmap(File(albumArt40DpDir, albumId.toString()))

    fun Context.loadAlbumArts40Dp(bitmapMap: MutableMap<Long, Bitmap?>) {
        albumArt40DpDir?.listFiles()?.forEach { file ->
            bitmapMap[file.name.toLong()] = file.readAsBitmap()
        }
    }

    private const val ALBUM_ART_RAW_DIR = "album_art_raw"
    private val Context.albumArtRawDir get() = getExternalFilesDir(ALBUM_ART_RAW_DIR)

    fun Context.writeAlbumArtRaw(albumId: Long, bitmap: Bitmap?) =
            bitmap?.writeBitmap(File(albumArtRawDir, albumId.toString()))

    fun Context.loadAlbumArtRaw(byteArrayMap: MutableMap<Long, ByteArray>) {
        albumArtRawDir?.listFiles()?.forEach { file ->
            byteArrayMap[file.name.toLong()] = file.readBytes()
            // bitmapMap[file.name.toLong()] = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
    }

    fun Context.loadAlbumArtRaw(albumId: Long) = loadAlbumArtRaw(albumId.toString())

    fun Context.loadAlbumArtRaw(albumId: String): Bitmap? {
        albumArtRawDir?.listFiles()?.forEach { file ->
            if (file.name.contains(albumId)) {
                return file.readAsBitmap()
            }
        }
        return null
    }
    
}