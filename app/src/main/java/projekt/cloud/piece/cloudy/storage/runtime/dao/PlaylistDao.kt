package projekt.cloud.piece.cloudy.storage.runtime.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import projekt.cloud.piece.cloudy.storage.runtime.entity.PlaylistEntity
import projekt.cloud.piece.cloudy.storage.runtime.entity.PlaylistEntity.PlaylistEntityConstant.PLAYLIST_HAS_NEXT
import projekt.cloud.piece.cloudy.storage.runtime.entity.PlaylistEntity.PlaylistEntityConstant.PLAYLIST_INDEX
import projekt.cloud.piece.cloudy.storage.runtime.entity.PlaylistEntity.PlaylistEntityConstant.TABLE_PLAYLIST

@Dao
interface PlaylistDao {

    @Insert
    suspend fun insert(playlistEntities: List<PlaylistEntity>)

    @Query("SELECT $PLAYLIST_HAS_NEXT " +
            "FROM $TABLE_PLAYLIST " +
            "WHERE `$PLAYLIST_INDEX` = :index"
    )
    suspend fun hasNext(index: Int): Boolean

    @Query("SELECT * " +
            "FROM $TABLE_PLAYLIST " +
            "WHERE `$PLAYLIST_INDEX` = :index"
    )
    suspend fun query(index: Int): PlaylistEntity

}