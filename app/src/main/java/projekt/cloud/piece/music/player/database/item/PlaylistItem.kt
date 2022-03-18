package projekt.cloud.piece.music.player.database.item

import androidx.room.Entity
import projekt.cloud.piece.music.player.database.base.BaseTitledItem
import java.io.Serializable
import java.lang.System.currentTimeMillis

@Entity(tableName = "playlist")
class PlaylistItem(
    id: String = currentTimeMillis().toString(),
    title: String
): BaseTitledItem(id, title), Serializable