package projekt.cloud.piece.music.player.database.audio.item

import androidx.room.Entity
import projekt.cloud.piece.music.player.database.audio.item.base.BaseTitledItem
import java.io.Serializable

/**
 * [AlbumItem]
 * inherit to [BaseTitledItem]
 * implement [Serializable]
 **/
@Entity(tableName = "album", inheritSuperIndices = true)
class AlbumItem(id: String, title: String): BaseTitledItem(id, title), Serializable