package projekt.cloud.piece.music.player.database.item

import androidx.room.ColumnInfo
import androidx.room.Entity
import projekt.cloud.piece.music.player.database.base.BaseItem
import java.io.Serializable
import java.lang.System.currentTimeMillis

@Entity(tableName = "playlist-content")
class PlaylistContentItem(
    id: String = currentTimeMillis().toString(),
    @ColumnInfo(name = "audio") val audio: String,
    @ColumnInfo(name = "playlist") val playlist: String
): BaseItem(id), Serializable