package projekt.cloud.piece.cloudy.storage.util

import android.content.Context
import androidx.room.RoomDatabase

class SingleDatabaseInstance<DATABASE: RoomDatabase>(
    private inline val createDatabase: (Context) -> DATABASE
) {

    @Volatile
    private var database: DATABASE? = null

    fun getInstance(context: Context): DATABASE {
        return database ?: syncCreateDatabase(context)
    }

    @Synchronized
    private fun syncCreateDatabase(context: Context): DATABASE {
        return database ?: createDatabase(context)
    }

    @Synchronized
    private fun createDatabase(context: Context): DATABASE {
        return createDatabase.invoke(context)
            .apply(::setDatabase)
    }

    @Synchronized
    private fun setDatabase(instance: DATABASE) {
        database = instance
    }

}