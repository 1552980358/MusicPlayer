package projekt.cloud.piece.music.player.util

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import projekt.cloud.piece.music.player.util.TryUtil.tryRun
import projekt.cloud.piece.music.player.util.UnitUtil.dp2PxF
import java.io.File

object ImageUtil {

    private const val ART_OPEN_MODE = "r"
    private const val ALBUM_ART_URI = "content://media/external/audio/albumart"
    private val Long.artUri get() = ContentUris.withAppendedId(Uri.parse(ALBUM_ART_URI), this)
    fun Context.extractArtBitmap(album: Long) = contentResolver.openFileDescriptor(album.artUri, ART_OPEN_MODE)?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }

    private const val SMALL_ART_SIZE_DP = 40

    private const val DIR_ALBUM_ART_LARGE = "album_large"
    private fun Context.getAlbumArtLargeFile(album: String) = File(getExternalFilesDir(DIR_ALBUM_ART_LARGE), album)
    private fun Context.saveAlbumArtLarge(album: String, bitmap: Bitmap) =
        getAlbumArtLargeFile(album).outputStream().use {
            bitmap.compress(JPEG, 100, it)
        }
    fun Context.readAlbumArtLarge(album: String) =
        tryRun { getAlbumArtLargeFile(album).inputStream().use { BitmapFactory.decodeStream(it) } }

    private const val DIR_ALBUM_ART_SMALL = "album_small"
    private fun Context.getAlbumArtSmallFile(album: String) = File(getExternalFilesDir(DIR_ALBUM_ART_SMALL), album)
    private fun Context.saveAlbumArtSmall(album: String, bitmap: Bitmap) =
        getAlbumArtSmallFile(album).outputStream().use {
            bitmap.compress(JPEG, 100, it)
        }
    fun Context.readAlbumArtSmall(album: String) =
        tryRun { getAlbumArtSmallFile(album).inputStream().use { BitmapFactory.decodeStream(it) } }

    fun Context.saveAlbumArt(album: String, bitmap: Bitmap) {
        saveAlbumArtLarge(album, bitmap)
        saveAlbumArtSmall(
            album,
            Bitmap.createBitmap(
                bitmap, 0, 0,
                bitmap.width, bitmap.height,
                Matrix().apply {
                    (SMALL_ART_SIZE_DP.dp2PxF(this@saveAlbumArt) / bitmap.width).apply {
                        setScale(this, this)
                    }
                },
                false
            )
        )
    }

}