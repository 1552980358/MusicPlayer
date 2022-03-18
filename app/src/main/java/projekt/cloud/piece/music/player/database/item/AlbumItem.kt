package projekt.cloud.piece.music.player.database.item

import androidx.room.Entity
import androidx.room.Ignore
import projekt.cloud.piece.music.player.database.base.BaseTitledItem
import java.io.Serializable

@Entity(tableName = "album")
class AlbumItem(
    id: String,
    title: String
): BaseTitledItem(id, title), Serializable {
    @Ignore
    var size = 0
}