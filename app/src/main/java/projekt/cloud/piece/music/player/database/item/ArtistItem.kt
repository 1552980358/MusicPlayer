package projekt.cloud.piece.music.player.database.item

import androidx.room.Entity
import projekt.cloud.piece.c2pinyin.pinyin
import projekt.cloud.piece.music.player.database.base.BaseTitledItem
import java.io.Serializable

@Entity(tableName = "artist")
class ArtistItem(
    id: String,
    title: String
): BaseTitledItem(id, title, title.pinyin), Serializable