package projekt.cloud.piece.music.player.storage.audio

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import projekt.cloud.piece.music.player.storage.audio.dao.MetadataDao
import projekt.cloud.piece.music.player.storage.audio.databaseView.AudioMetadata
import projekt.cloud.piece.music.player.storage.audio.entity.AlbumEntity
import projekt.cloud.piece.music.player.storage.audio.entity.ArtistEntity
import projekt.cloud.piece.music.player.storage.audio.entity.AudioEntity

@Database(
    entities = [AudioEntity::class, ArtistEntity::class, AlbumEntity::class],
    views = [AudioMetadata::class],
    version = 1
)
abstract class AudioDatabase: RoomDatabase() {

    companion object AudioDatabaseUtil {
        private const val AUDIO_DATABASE_FILE = "audio.db"

        @JvmStatic
        val Context.audioDatabase: AudioDatabase
            get() = Room.databaseBuilder(this, AudioDatabase::class.java, AUDIO_DATABASE_FILE)
                .build()
    }

    abstract fun metadataDao(): MetadataDao

}