package sakuraba.saki.player.music.util

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import lib.github1552980358.ktExtension.android.graphics.getByteArray
import lib.github1552980358.ktExtension.android.java.readAsBitmap
import java.io.File

object BitmapUtil {
    
    private val ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart")
    val Long.getURI get() = ContentUris.withAppendedId(ALBUM_ART_URI, this)
    @Suppress("HasPlatformType")
    fun Context.loadAlbumArt(albumId: Long) = BitmapFactory.decodeFileDescriptor(contentResolver.openFileDescriptor(albumId.getURI, "r")?.fileDescriptor)

    private const val ALBUM_ART_40DP_DIR = "album_art_40dp"
    private val Context.albumArt40DpDir get() = getExternalFilesDir(ALBUM_ART_40DP_DIR)

    private fun Bitmap.writeBitmap(file: File) =
            getByteArray(format = Bitmap.CompressFormat.JPEG)?.apply { file.writeBytes(this) }

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

    private const val AUDIO_ART_RAW_40DP = "audio_art_40dp"
    private val Context.audioArt40DpDir get() = getExternalFilesDir(AUDIO_ART_RAW_40DP)
    private const val AUDIO_ART_RAW_DIR = "audio_art_raw"
    private val Context.audioArtRawDir get() = getExternalFilesDir(AUDIO_ART_RAW_DIR)

    fun Context.writeAudioArt40Dp(audioId: String, bitmap: Bitmap?) =
        bitmap?.writeBitmap(File(audioArt40DpDir, audioId))

    fun Context.writeAudioArtRaw(audioId: String, bitmap: Bitmap?) =
        bitmap?.writeBitmap(File(audioArtRawDir, audioId))

    fun Context.loadAudioArtRaw(audioId: String): Bitmap? {
        audioArtRawDir?.listFiles()?.forEach { file ->
            if (file.name.startsWith(audioId)) {
                return file.readAsBitmap()
            }
        }
        return null
    }

    fun Context.loadAudioArt40Dp(bitmapMap: MutableMap<String, Bitmap?>) {
        audioArt40DpDir?.listFiles()?.forEach { file ->
            bitmapMap[file.name] = file.readAsBitmap()
        }
    }

    fun Context.removeAudioArt(audioId: String) {
        audioArtRawDir?.listFiles()?.forEach { file ->
            if (file.name.startsWith(audioId)) {
                file.delete()
            }
        }
        audioArt40DpDir?.listFiles()?.forEach { file ->
            if (file.name.startsWith(audioId)) {
                file.delete()
            }

        }
    }

    val Bitmap.cutAsCube get(): Bitmap = when {
        width > height -> Bitmap.createBitmap(this, (width - height) / 2, 0, height, height)
        width < height -> Bitmap.createBitmap(this, 0, (height - width) / 2, width, width)
        else -> this
    }
    
}