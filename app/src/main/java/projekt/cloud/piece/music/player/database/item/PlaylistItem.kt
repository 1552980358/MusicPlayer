package projekt.cloud.piece.music.player.database.item

import androidx.room.ColumnInfo
import androidx.room.Entity
import projekt.cloud.piece.music.player.database.base.BaseTitledItem
import java.io.Serializable
import java.lang.System.currentTimeMillis

@Entity(tableName = "playlist")
class PlaylistItem(
    id: String = currentTimeMillis().toString(),
    title: String,
    @ColumnInfo(name = "description") val description: String? = null
): BaseTitledItem(id, title), Serializable