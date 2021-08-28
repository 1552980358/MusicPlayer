package sakuraba.saki.player.music.util

import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.fragment.app.Fragment

object BitmapUtil {
    
    private val ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart")
    fun Context.loadAlbumArt(albumId: Long) = BitmapFactory.decodeFileDescriptor(
        contentResolver
            .openFileDescriptor(ContentUris.withAppendedId(ALBUM_ART_URI, albumId), "r")
            ?.fileDescriptor
    )
    
    fun Fragment.loadAlbumArt(albumId: Long) = requireContext().loadAlbumArt(albumId)
    
}