package projekt.cloud.piece.music.player.item.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import projekt.cloud.piece.music.player.item.AudioMetadata

@Dao
interface AudioMetadataDao {

    @Transaction
    @Query("select * from audio")
    fun query(): List<AudioMetadata>

}