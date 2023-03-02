package projekt.cloud.piece.music.player.storage.runtime

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import projekt.cloud.piece.music.player.storage.runtime.dao.AudioMetadataDao
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity

@Database(
    entities = [AudioMetadataEntity::class],
    views = [ArtistView::class, AlbumView::class],
    version = 1
)
abstract class RuntimeDatabase: RoomDatabase() {

    companion object RuntimeDatabaseUtil {
        private fun Context.createRuntimeDatabase() =
            Room.inMemoryDatabaseBuilder(this, RuntimeDatabase::class.java)
                .build()
        private fun Context.returnOrCreate(): RuntimeDatabase {
            return instance ?: synchronized(this@RuntimeDatabaseUtil) {
                instance ?: createRuntimeDatabase().also { instance = it }
            }
        }

        @Volatile
        private var instance: RuntimeDatabase? = null
        val Context.runtimeDatabase: RuntimeDatabase
            get() = returnOrCreate()

    }

    abstract fun audioMetadataDao(): AudioMetadataDao



}