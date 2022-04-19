package projekt.cloud.piece.music.player.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import projekt.cloud.piece.music.player.database.audio.dao.AlbumDao
import projekt.cloud.piece.music.player.database.audio.dao.ArtistDao
import projekt.cloud.piece.music.player.database.audio.dao.AudioDao
import projekt.cloud.piece.music.player.database.audio.item.AlbumItem
import projekt.cloud.piece.music.player.database.audio.item.ArtistItem
import projekt.cloud.piece.music.player.database.audio.item.AudioItem

@Database(
    entities = [
        AudioItem::class,
        ArtistItem::class,
        AlbumItem::class
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

    abstract fun audioDao(): AudioDao
    val audioDao get() = audioDao()

    abstract fun artistDao(): ArtistDao
    val artistDao get() = artistDao()

    abstract fun albumDao(): AlbumDao
    val albumDao get() = albumDao()

    val queryAudio get() = audioDao.queryAll().onEach {
        it.artistItem = artistDao.query(it.artist)
        it.albumItem = albumDao.query(it.album)
    }
    
    fun queryAudio(id: String) = audioDao.query(id).apply {
        artistItem = artistDao.query(artist)
        albumItem = albumDao.query(album)
    }

}