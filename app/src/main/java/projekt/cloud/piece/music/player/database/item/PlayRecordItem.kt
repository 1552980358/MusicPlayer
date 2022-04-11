package projekt.cloud.piece.music.player.database.item

import androidx.room.ColumnInfo
import androidx.room.Entity
import projekt.cloud.piece.music.player.database.base.BaseItem
import java.lang.System.currentTimeMillis

@Entity(tableName = "play-record")
class PlayRecordItem(@ColumnInfo val audio: String): BaseItem(
    currentTimeMillis().toString(),
)