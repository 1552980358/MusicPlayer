package projekt.cloud.piece.music.player.database.audio.item

import androidx.room.Entity
import projekt.cloud.piece.music.player.database.audio.item.base.BaseTitledItem
import java.io.Serializable

@Entity(tableName = "artist")
class ArtistItem(id: String, title: String): BaseTitledItem(id, title), Serializable