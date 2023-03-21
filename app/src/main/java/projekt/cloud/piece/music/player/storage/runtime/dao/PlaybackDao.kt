package projekt.cloud.piece.music.player.storage.runtime.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_ID
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_TABLE_NAME
import projekt.cloud.piece.music.player.storage.runtime.entity.PlaybackEntity
import projekt.cloud.piece.music.player.storage.runtime.entity.PlaybackEntity.PlaybackEntityConstants.PLAYBACK_COLUMN_ID
import projekt.cloud.piece.music.player.storage.runtime.entity.PlaybackEntity.PlaybackEntityConstants.PLAYBACK_COLUMN_ORDER
import projekt.cloud.piece.music.player.storage.runtime.entity.PlaybackEntity.PlaybackEntityConstants.PLAYBACK_TABLE_NAME

@Dao
interface PlaybackDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(playbackEntityList: List<PlaybackEntity>)

    suspend fun isLastOrder(order: Int): Boolean {
        return order == lastOrder()
    }

    @Query("SELECT MAX(`${PLAYBACK_COLUMN_ORDER}`) FROM $PLAYBACK_TABLE_NAME")
    suspend fun lastOrder(): Int

    @Query("SELECT $PLAYBACK_COLUMN_ID FROM $PLAYBACK_TABLE_NAME WHERE `$PLAYBACK_COLUMN_ORDER` = :order")
    suspend fun queryId(order: Int): String

    @Query("SELECT `$PLAYBACK_COLUMN_ORDER` FROM $PLAYBACK_TABLE_NAME WHERE $PLAYBACK_COLUMN_ID = :id")
    suspend fun queryOrder(id: String): Int

    @Query("SELECT * " +
            "FROM $AUDIO_METADATA_TABLE_NAME " +
            "WHERE $AUDIO_METADATA_COLUMN_ID in " +
            "(SELECT $PLAYBACK_COLUMN_ID FROM $PLAYBACK_TABLE_NAME)")
    suspend fun query(): List<AudioMetadataEntity>

    @Query("DELETE FROM $PLAYBACK_TABLE_NAME")
    suspend fun clear()

}