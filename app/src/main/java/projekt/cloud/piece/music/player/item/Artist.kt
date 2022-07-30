package projekt.cloud.piece.music.player.item

import androidx.room.Entity
import java.io.Serializable
import projekt.cloud.piece.music.player.item.base.BaseTitledItem

@Entity
class Artist(id: String, title: String): BaseTitledItem(id, title), Serializable