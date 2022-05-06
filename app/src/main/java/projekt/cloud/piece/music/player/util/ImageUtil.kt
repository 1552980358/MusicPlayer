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
    private const val ART_SUFFIX_LARGE = "_large"
    private const val ART_SUFFIX_SMALL = "_small"

    private fun Context.saveArt(type: String, id: String, bitmap: Bitmap) {
        File(getExternalFilesDir(type + ART_SUFFIX_LARGE), id).outputStream().use {
            bitmap.compress(JPEG, 100, it)
        }
        File(getExternalFilesDir(type + ART_SUFFIX_SMALL), id).outputStream().use {
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

    private fun Context.readArt(type: String, isLarge: Boolean, id: String) = tryRun {
        File(getExternalFilesDir(type + if (isLarge) ART_SUFFIX_LARGE else ART_SUFFIX_SMALL), id)
            .inputStream()
            .use { BitmapFactory.decodeStream(it) }
    }

    const val FLAG_LARGE = true
    const val FLAG_SMALL = false
    
    private const val ART_ARTIST = "artist"
    fun Context.saveArtistArt(artist: String, bitmap: Bitmap) =
        saveArt(ART_ARTIST, artist, bitmap)
    fun Context.readArtistArt(artist: String, isLarge: Boolean) =
        readArt(ART_ARTIST, isLarge, artist)
    
    private const val ART_ALBUM = "album"
    fun Context.saveAlbumArt(album: String, bitmap: Bitmap) =
        saveArt(ART_ALBUM, album, bitmap)
    fun Context.readAlbumArt(album: String, isLarge: Boolean) =
        readArt(ART_ALBUM, isLarge, album)

    private const val ART_PLAYLIST = "playlist"
    fun Context.savePlaylistArt(playlist: String, bitmap: Bitmap) =
        saveArt(ART_PLAYLIST, playlist, bitmap)
    fun Context.readPlaylistArt(playlist: String, isLarge: Boolean) =
        readArt(ART_PLAYLIST, isLarge, playlist)

}