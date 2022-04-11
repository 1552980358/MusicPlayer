package projekt.cloud.piece.music.player.database.base

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.io.Serializable

open class BaseItem(
    @PrimaryKey @ColumnInfo(name = "id") var id: String
): Serializable