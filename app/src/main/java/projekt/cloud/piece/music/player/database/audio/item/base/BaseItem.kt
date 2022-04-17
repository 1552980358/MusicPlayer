package projekt.cloud.piece.music.player.database.audio.item.base

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

open class BaseItem(@PrimaryKey @ColumnInfo(name = "id") val id: String): Serializable {
    val idLong get() = id.toLong()
}