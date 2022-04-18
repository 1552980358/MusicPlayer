package projekt.cloud.piece.music.player.util

import android.content.Context
import android.provider.MediaStore.Audio.AudioColumns
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
import androidx.annotation.MainThread
import projekt.cloud.piece.music.player.database.Database.audioRoom
import projekt.cloud.piece.music.player.database.audio.item.AlbumItem
import projekt.cloud.piece.music.player.database.audio.item.ArtistItem
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

/**
 * Object [AudioUtil]
 *
 * Methods:
 * [initialApplication]
 * [launchApplication]
 * [applicationLauncher]
 * [setUpApplication]
 * [loadApplication]
 * [scanSystemDatabase]
 * [storeToAudioRoom]
 *
 **/
object AudioUtil {

    fun Context.initialApplication(@MainThread callback: (List<AudioItem>) -> Unit) = io {
        setUpApplication()
        applicationLauncher(callback)
    }

    fun Context.launchApplication(@MainThread callback: (List<AudioItem>) -> Unit) = io {
        applicationLauncher(callback)
    }

    private fun Context.applicationLauncher(@MainThread callback: (List<AudioItem>) -> Unit) {
        val list = loadApplication()
        ui { callback(list) }
    }

    private fun Context.setUpApplication() {
        val audioList = ArrayList<AudioItem>()
        val artistList = ArrayList<ArtistItem>()
        val albumList = ArrayList<AlbumItem>()
        scanSystemDatabase(audioList, artistList, albumList)
        storeToAudioRoom(
            audioList.toTypedArray(),
            artistList.toTypedArray(),
            albumList.toTypedArray()
        )

    }

    private fun Context.loadApplication() = audioRoom.queryAudio

    private fun Context.scanSystemDatabase(audioList: ArrayList<AudioItem>,
                                   artistList: ArrayList<ArtistItem>,
                                   albumList: ArrayList<AlbumItem>) {
        contentResolver.query(EXTERNAL_CONTENT_URI, null, null, null, AudioColumns.IS_MUSIC)?.apply {
            var album: String
            var artist: String
            while (moveToNext()) {
                artist = getString(getColumnIndexOrThrow(AudioColumns.ARTIST_ID))
                if (artistList.find { it.id == artist } == null) {
                    artistList.add(
                        ArtistItem(artist, getString(getColumnIndexOrThrow(AudioColumns.ARTIST)))
                    )
                }
                album = getString(getColumnIndexOrThrow(AudioColumns.ALBUM_ID))
                if (albumList.find { it.id == album } == null) {
                    albumList.add(
                        AlbumItem(album, getString(getColumnIndexOrThrow(AudioColumns.ALBUM)))
                    )
                }
                @Suppress("DEPRECATION")
                audioList.add(
                    AudioItem(
                        getString(getColumnIndexOrThrow(AudioColumns._ID)),
                        getString(getColumnIndexOrThrow(AudioColumns.TITLE)),
                        artist,
                        album,
                        getLong(getColumnIndexOrThrow(AudioColumns.DURATION)),
                        getLong(getColumnIndexOrThrow(AudioColumns.SIZE)),
                        getString(getColumnIndexOrThrow(AudioColumns.DATA))
                    )
                )
            }
        }?.close()
    }

    private fun Context.storeToAudioRoom(audios: Array<AudioItem>,
                                         artists: Array<ArtistItem>,
                                         albums: Array<AlbumItem>) {
        with(audioRoom) {
            artistDao.insert(*artists)
            albumDao.insert(*albums)
            audioDao.insert(*audios)
        }
    }

}