package projekt.cloud.piece.music.player.item.base

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

abstract class BaseItem(@PrimaryKey @ColumnInfo(name = "id") val id: String) {
    
    val idLong: Long
        get() = id.toLong()
    
}
