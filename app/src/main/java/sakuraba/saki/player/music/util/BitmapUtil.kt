package sakuraba.saki.player.music.util

import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.fragment.app.Fragment

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
    
}