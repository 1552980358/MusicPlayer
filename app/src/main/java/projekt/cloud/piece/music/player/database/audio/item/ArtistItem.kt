package projekt.cloud.piece.music.player.database.audio.item

import androidx.room.Entity
import projekt.cloud.piece.music.player.database.audio.item.base.BaseTitledItem
import java.io.Serializable

/**
 * [ArtistItem]
 * inherit to [BaseTitledItem]
 * implement [Serializable]
 **/
@Entity(tableName = "artist", inheritSuperIndices = true)
class ArtistItem(id: String, title: String): BaseTitledItem(id, title), Serializable