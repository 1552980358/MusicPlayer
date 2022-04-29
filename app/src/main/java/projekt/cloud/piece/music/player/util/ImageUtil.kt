package projekt.cloud.piece.music.player.util

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import projekt.cloud.piece.music.player.util.ImageUtil.saveAlbumArt
import projekt.cloud.piece.music.player.util.TryUtil.tryRun
import projekt.cloud.piece.music.player.util.UnitUtil.dp2PxF
import java.io.File

/**
 * Object [ImageUtil]
 *
 * Constants:
 *  [ART_OPEN_MODE]
 *  [ALBUM_ART_URI]
 *  [SMALL_ART_SIZE_DP]
 *  [FLAG_LARGE]
 *  [FLAG_SMALL]
 *  [DIR_ALBUM_ART_LARGE]
 *  [DIR_ALBUM_ART_SMALL]
 *  [DIR_PLAYLIST_ART_LARGE]
 *  [DIR_PLAYLIST_ART_SMALL]
 *
 * Getters:
 *  [artUri]
 *
 * Methods:
 *  [extractArtBitmap]
 *
 *  [saveArt]
 *  [readArt]
 *
 *  [saveAlbumArt]
 *  [readAlbumArt]
 *  [savePlaylistArt]
 *  [readPlaylistArt]
 *
 **/
object ImageUtil {

    private const val ART_OPEN_MODE = "r"
    private const val ALBUM_ART_URI = "content://media/external/audio/albumart"
    private val Long.artUri get() = ContentUris.withAppendedId(Uri.parse(ALBUM_ART_URI), this)
    fun Context.extractArtBitmap(album: Long) = tryRun {
        contentResolver.openFileDescriptor(album.artUri, ART_OPEN_MODE)?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }
    }

    private const val SMALL_ART_SIZE_DP = 40

    private fun Context.saveArt(largeDir: String, smallDir: String, id: String, bitmap: Bitmap) {
        File(getExternalFilesDir(largeDir), id).outputStream().use {
            bitmap.compress(JPEG, 100, it)
        }
        File(getExternalFilesDir(smallDir), id).outputStream().use {
            Bitmap.createBitmap(
                bitmap, 0, 0,
                bitmap.width, bitmap.height,
                Matrix().apply {
                    (SMALL_ART_SIZE_DP.dp2PxF(this@saveArt) / bitmap.width).apply {
                        setScale(this, this)
                    }
                },
                false
            ).compress(JPEG, 100, it)
        }
    }

    private fun Context.readArt(dir: String, id: String) =
        tryRun { File(getExternalFilesDir(dir), id).inputStream().use { BitmapFactory.decodeStream(it) } }

    const val FLAG_LARGE = true
    const val FLAG_SMALL = false

    private const val DIR_ALBUM_ART_LARGE = "album_large"
    private const val DIR_ALBUM_ART_SMALL = "album_small"
    fun Context.saveAlbumArt(album: String, bitmap: Bitmap) =
        saveArt(DIR_ALBUM_ART_LARGE, DIR_ALBUM_ART_SMALL, album, bitmap)
    fun Context.readAlbumArt(album: String, isLarge: Boolean) =
        readArt(if (isLarge) DIR_ALBUM_ART_LARGE else DIR_ALBUM_ART_SMALL, album)

    private const val DIR_PLAYLIST_ART_LARGE = "playlist_large"
    private const val DIR_PLAYLIST_ART_SMALL = "playlist_small"
    fun Context.savePlaylistArt(playlist: String, bitmap: Bitmap) =
        saveArt(DIR_PLAYLIST_ART_LARGE, DIR_PLAYLIST_ART_SMALL, playlist, bitmap)
    fun Context.readPlaylistArt(playlist: String, isLarge: Boolean) =
        readArt(if (isLarge) DIR_PLAYLIST_ART_LARGE else DIR_PLAYLIST_ART_SMALL, playlist)

}