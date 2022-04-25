package projekt.cloud.piece.music.player.database.audio.item

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import projekt.cloud.piece.music.player.database.audio.item.base.BaseTitledItem
import java.lang.System.currentTimeMillis

@Entity(tableName = "playlist")
class PlaylistItem(id: String,
                   title: String,
                   @ColumnInfo(name = "description") var description: String? = null): BaseTitledItem(id, title) {

    @Ignore
    constructor(title: String, description: String? = null): this(currentTimeMillis().toString(), title, description)

}