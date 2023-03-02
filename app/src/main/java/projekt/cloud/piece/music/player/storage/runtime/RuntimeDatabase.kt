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

    companion object {
        val Context.runtimeDatabase: RuntimeDatabase
            get() = Room.inMemoryDatabaseBuilder(this, RuntimeDatabase::class.java)
                .build()
    }

    abstract fun audioMetadataDao(): AudioMetadataDao

}