package projekt.cloud.piece.music.player.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import projekt.cloud.piece.music.player.database.audio.dao.AlbumDao
import projekt.cloud.piece.music.player.database.audio.dao.ArtistDao
import projekt.cloud.piece.music.player.database.audio.dao.AudioDao
import projekt.cloud.piece.music.player.database.audio.dao.ColorDao
import projekt.cloud.piece.music.player.database.audio.dao.PlaylistAudioDao
import projekt.cloud.piece.music.player.database.audio.dao.PlaylistDao
import projekt.cloud.piece.music.player.database.audio.extension.PlaylistWithAudio
import projekt.cloud.piece.music.player.database.audio.item.AlbumItem
import projekt.cloud.piece.music.player.database.audio.item.ArtistItem
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.database.audio.item.ColorItem
import projekt.cloud.piece.music.player.database.audio.item.PlaylistAudioItem
import projekt.cloud.piece.music.player.database.audio.item.PlaylistItem

@Database(
    entities = [
        AudioItem::class,
        ArtistItem::class,
        AlbumItem::class,
        ColorItem::class,
        PlaylistItem::class,
        PlaylistAudioItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AudioRoom: RoomDatabase() {

    companion object {

        private const val NAME = "AudioRoom"

        @JvmStatic
        fun get(context: Context) = Room.databaseBuilder(context, AudioRoom::class.java, NAME)
            .build()

    }

    protected abstract fun audioDao(): AudioDao
    val audioDao get() = audioDao()

    protected abstract fun artistDao(): ArtistDao
    val artistDao get() = artistDao()

    protected abstract fun albumDao(): AlbumDao
    val albumDao get() = albumDao()

    protected abstract fun colorDao(): ColorDao
    val colorDao get() = colorDao()

    protected abstract fun playlistDao(): PlaylistDao
    val playlistDao get() = playlistDao()

    protected abstract fun playlistAudioDao(): PlaylistAudioDao
    val playlistAudioDao get() = playlistAudioDao()

    val queryAudio get() = audioDao.queryAll().onEach {
        it.artistItem = artistDao.query(it.artist)
        it.albumItem = albumDao.query(it.album)
    }
    
    fun queryAudio(id: String) = audioDao.query(id).apply {
        artistItem = artistDao.query(artist)
        albumItem = albumDao.query(album)
    }

    fun queryColor(audio: String, album: String) =
        colorDao.query(audio, album)

    val playlist get() = ArrayList<PlaylistWithAudio>().apply {
        playlistDao.query().forEach { playlistItem ->
            add(PlaylistWithAudio(playlistItem, playlistAudioDao.query(playlistItem.id)))
        }
    }
    
    fun queryPlaylistAudio(playlist: String) = playlistAudioDao.query(playlist).onEach {
        it.albumItem = albumDao.query(it.album)
        it.artistItem = artistDao.query(it.artist)
    }

}