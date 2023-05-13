package projekt.cloud.piece.cloudy.storage.audio

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import projekt.cloud.piece.cloudy.storage.audio.AudioDatabase.AudioDatabaseUtil.AUDIO_DATABASE_VERSION
import projekt.cloud.piece.cloudy.storage.audio.dao.MetadataDao
import projekt.cloud.piece.cloudy.storage.audio.entity.AlbumEntity
import projekt.cloud.piece.cloudy.storage.audio.entity.ArtistEntity
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity

@Database(
    entities = [AudioEntity::class, ArtistEntity::class, AlbumEntity::class],
    views = [MetadataDao::class],
    version = AUDIO_DATABASE_VERSION
)
abstract class AudioDatabase: RoomDatabase() {

    companion object AudioDatabaseUtil {

        const val AUDIO_DATABASE_VERSION = 1

        private val AUDIO_DATABASE_CLASS: Class<AudioDatabase>
            get() = AudioDatabase::class.java
        private const val AUDIO_DATABASE_NAME = "audio.db"

        val Context.audioDatabase: AudioDatabase
            get() = instance ?: syncInstance

        @Volatile
        private var instance: AudioDatabase? = null
        private val Context.syncInstance: AudioDatabase
            get() = synchronized(this@AudioDatabaseUtil) {
                instance ?: setInstance(newInstance)
            }
        private val Context.newInstance: AudioDatabase
            get() = Room.databaseBuilder(this, AUDIO_DATABASE_CLASS, AUDIO_DATABASE_NAME)
                .build()
        private fun setInstance(audioDatabase: AudioDatabase): AudioDatabase {
            instance = audioDatabase
            return audioDatabase
        }

    }

    val metadata: MetadataDao
        get() = metadata()
    protected abstract fun metadata(): MetadataDao

}