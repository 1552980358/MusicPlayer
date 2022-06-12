package projekt.cloud.piece.music.player.database.audio.item.base

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * [BaseItem]
 * implement to [Serializable]
 *
 * Variable:
 * [id]
 *
 * Method:
 * [idLong]
 **/
open class BaseItem(@PrimaryKey @ColumnInfo(name = "id", index = true) var id: String): Serializable {
    
    @Ignore
    constructor(id: Long): this(id.toString())

    val idLong get() = id.toLong()
}