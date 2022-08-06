package projekt.cloud.piece.music.player.item.base

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.io.Serializable

abstract class BaseItem(@PrimaryKey @ColumnInfo(name = "id") val id: String): Serializable {
    
    val idLong: Long
        get() = id.toLong()
    
}
