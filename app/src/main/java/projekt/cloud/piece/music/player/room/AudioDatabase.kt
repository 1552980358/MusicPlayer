package projekt.cloud.piece.music.player.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import projekt.cloud.piece.music.player.item.Album
import projekt.cloud.piece.music.player.item.Artist
import projekt.cloud.piece.music.player.item.Audio
import projekt.cloud.piece.music.player.item.dao.AlbumDao
import projekt.cloud.piece.music.player.item.dao.ArtistDao
import projekt.cloud.piece.music.player.item.dao.AudioDao
import projekt.cloud.piece.music.player.item.dao.AudioMetadataDao

@Database(entities = [ Audio::class, Artist::class, Album::class ], version = 1)
abstract class AudioDatabase: RoomDatabase() {
    
    companion object {
        
        private const val NAME = "AudioDatabase.db"
        
        private var instance: AudioDatabase? = null
        fun initial(context: Context) {
            instance = Room.databaseBuilder(context, AudioDatabase::class.java, NAME)
                .build()
        }
        
        val audioDatabase: AudioDatabase
            get() = instance!!
        
    }

    abstract fun audioDao(): AudioDao
    val audioDao: AudioDao
        get() = audioDao()
    
    abstract fun artistDao(): ArtistDao
    val artistDao: ArtistDao
        get() = artistDao()
    
    abstract fun albumDao(): AlbumDao
    val albumDao: AlbumDao
        get() = albumDao()
    
    abstract fun audioMetadataDao(): AudioMetadataDao
    val audioMetadataDao: AudioMetadataDao
        get() = audioMetadataDao()
    
}