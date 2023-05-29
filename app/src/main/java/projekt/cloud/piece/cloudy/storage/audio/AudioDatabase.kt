package projekt.cloud.piece.cloudy.storage.audio

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import projekt.cloud.piece.cloudy.storage.audio.AudioDatabase.AudioDatabaseUtil.AUDIO_DATABASE_VERSION
import projekt.cloud.piece.cloudy.storage.audio.dao.LibraryDao
import projekt.cloud.piece.cloudy.storage.audio.dao.MetadataDao
import projekt.cloud.piece.cloudy.storage.audio.dao.StatisticsDao
import projekt.cloud.piece.cloudy.storage.audio.entity.AlbumEntity
import projekt.cloud.piece.cloudy.storage.audio.entity.ArtistEntity
import projekt.cloud.piece.cloudy.storage.audio.entity.AudioEntity
import projekt.cloud.piece.cloudy.storage.audio.view.ArtistView
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView
import projekt.cloud.piece.cloudy.storage.audio.view.StatisticsView
import projekt.cloud.piece.cloudy.storage.util.SingleDatabaseInstance

@Database(
    entities = [AudioEntity::class, ArtistEntity::class, AlbumEntity::class],
    views = [MetadataView::class, StatisticsView::class, ArtistView::class],
    version = AUDIO_DATABASE_VERSION
)
abstract class AudioDatabase: RoomDatabase() {

    companion object AudioDatabaseUtil {

        const val AUDIO_DATABASE_VERSION = 1

        val Context.audioDatabase: AudioDatabase
            get() = instance.getInstance(this)

        private val instance = SingleDatabaseInstance(::createDatabase)

        private const val AUDIO_DATABASE_NAME = "audio.db"
        private val AUDIO_DATABASE_CLASS: Class<AudioDatabase>
            get() = AudioDatabase::class.java
        private fun createDatabase(context: Context): AudioDatabase {
            return Room.databaseBuilder(context, AUDIO_DATABASE_CLASS, AUDIO_DATABASE_NAME)
                .build()
        }

    }

    val metadata: MetadataDao
        get() = metadata()
    protected abstract fun metadata(): MetadataDao

    val statistics: StatisticsDao
        get() = statistics()
    protected abstract fun statistics(): StatisticsDao

    /**
     * [AudioDatabase.library]
     * @type [LibraryDao]
     **/
    val library: LibraryDao
        get() = library()
    /**
     * [AudioDatabase.library]
     * @return [LibraryDao]
     **/
    protected abstract fun library(): LibraryDao

}