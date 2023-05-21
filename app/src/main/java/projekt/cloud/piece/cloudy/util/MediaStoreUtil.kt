package projekt.cloud.piece.cloudy.util

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore.Audio.AudioColumns.ARTIST
import android.provider.MediaStore.Audio.AudioColumns.ARTIST_ID
import android.provider.MediaStore.Audio.AudioColumns.ALBUM
import android.provider.MediaStore.Audio.AudioColumns.ALBUM_ID
import android.provider.MediaStore.Audio.AudioColumns.DURATION
import android.provider.MediaStore.Audio.AudioColumns.SIZE
import android.provider.MediaStore.Audio.AudioColumns._ID
import android.provider.MediaStore.Audio.AudioColumns.TITLE
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.Audio.Media.IS_MUSIC
import androidx.core.database.getStringOrNull

object MediaStoreUtil {

    /**
     * Music Metadata
     **/
    val Cursor.musicId: String
        get() = getString(getColumnIndexOrThrow(_ID))
    val Cursor.musicTitle: String
        get() = getString(getColumnIndexOrThrow(TITLE))
    val Cursor.musicArtistId: String?
        get() = getStringOrNull(getColumnIndex(ARTIST_ID))
    val Cursor.musicArtistName: String
        get() = getString(getColumnIndexOrThrow(ARTIST))
    val Cursor.musicAlbumId: String?
        get() = getStringOrNull(getColumnIndex(ALBUM_ID))
    val Cursor.musicAlbumTitle: String
        get() = getString(getColumnIndexOrThrow(ALBUM))
    val Cursor.musicDuration: Long
        get() = getLong(getColumnIndexOrThrow(DURATION))
    val Cursor.musicSize: Long
        get() = getLong(getColumnIndexOrThrow(SIZE))

    inline fun Context.musicCursor(block: (Cursor) -> Unit) {
        contentResolver.query(EXTERNAL_CONTENT_URI, null, null, null, IS_MUSIC)
            ?.use { cursor ->
                while (cursor.moveToNext()) {
                    block.invoke(cursor)
                }
            }
    }

}