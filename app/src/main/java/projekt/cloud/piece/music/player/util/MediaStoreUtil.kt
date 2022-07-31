package projekt.cloud.piece.music.player.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import projekt.cloud.piece.music.player.item.Album
import projekt.cloud.piece.music.player.item.Artist
import projekt.cloud.piece.music.player.item.Audio

object MediaStoreUtil {

    fun Context.queryMediaStore(audioList: ArrayList<Audio>, artistList: ArrayList<Artist>, albumList: ArrayList<Album>) =
        contentResolver.queryMediaStore(audioList, artistList, albumList)
    
    private fun ContentResolver.queryMediaStore(audioList: ArrayList<Audio>, artistList: ArrayList<Artist>, albumList: ArrayList<Album>) {
        query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null, null, null,
            MediaStore.Audio.AudioColumns.IS_MUSIC)?.apply {
            var artist: String
            var album: String
            while (moveToNext()) {
                artist = getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST_ID))
                if (artistList.find { it.id == artist } == null) {
                    artistList.add(Artist(artist, getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST))))
                }
                album = getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID))
                if (albumList.find { it.id == album } == null) {
                    albumList.add(Album(album, getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM))))
                }
                audioList.add(
                    Audio(
                        getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)),
                        getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)),
                        artist,
                        album,
                        getLong(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)),
                        getLong(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE)),
                        getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA))
                    )
                )
            }
        }?.close()
    }
    
    private fun Context.requestAlbumArt(album: Album) = contentResolver.requestAlbumArt(album.uri)
    
    private const val COVER_ART_OPEN_MODE = "r"
    private fun ContentResolver.requestAlbumArt(uri: Uri) = try {
        openFileDescriptor(uri, COVER_ART_OPEN_MODE)?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }
    } catch (e: Exception) { null }

}