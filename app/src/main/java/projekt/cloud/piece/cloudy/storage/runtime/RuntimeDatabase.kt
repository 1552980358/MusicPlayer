package projekt.cloud.piece.cloudy.storage.runtime

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import projekt.cloud.piece.cloudy.storage.runtime.RuntimeDatabase.RuntimeDatabaseUtil.RUNTIME_DATABASE_VERSION
import projekt.cloud.piece.cloudy.storage.runtime.dao.PlaylistDao
import projekt.cloud.piece.cloudy.storage.runtime.entity.PlaylistEntity
import projekt.cloud.piece.cloudy.storage.util.SingleDatabaseInstance

@Database(
    entities = [PlaylistEntity::class],
    version = RUNTIME_DATABASE_VERSION
)
abstract class RuntimeDatabase: RoomDatabase() {

    companion object RuntimeDatabaseUtil {

        const val RUNTIME_DATABASE_VERSION = 1

        val Context.runtimeDatabase: RuntimeDatabase
            get() = instance.getInstance(this)

        private val instance = SingleDatabaseInstance(::createDatabase)
        private val RUNTIME_DATABASE_CLASS: Class<RuntimeDatabase>
            get() = RuntimeDatabase::class.java
        private fun createDatabase(context: Context): RuntimeDatabase {
            return Room.inMemoryDatabaseBuilder(context, RUNTIME_DATABASE_CLASS)
                .build()
        }

    }

    val playlist: PlaylistDao
        get() = playlist()
    protected abstract fun playlist(): PlaylistDao

}