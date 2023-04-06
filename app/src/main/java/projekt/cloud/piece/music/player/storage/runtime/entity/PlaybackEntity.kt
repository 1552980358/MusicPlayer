package projekt.cloud.piece.music.player.storage.runtime.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import projekt.cloud.piece.music.player.storage.runtime.entity.PlaybackEntity.PlaybackEntityConstants.PLAYBACK_TABLE_NAME

@Entity(tableName = PLAYBACK_TABLE_NAME)
class PlaybackEntity(
    @PrimaryKey
    @ColumnInfo(name = PLAYBACK_COLUMN_ORDER)
    var order: Int,
    @ColumnInfo(name = PLAYBACK_COLUMN_ID)
    val id: String
) {

    companion object PlaybackEntityConstants {
        const val PLAYBACK_TABLE_NAME = "playback"

        const val PLAYBACK_COLUMN_ORDER = "order"
        const val PLAYBACK_COLUMN_ID = "id"
    }

}