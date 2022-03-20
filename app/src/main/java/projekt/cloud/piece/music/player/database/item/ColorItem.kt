package projekt.cloud.piece.music.player.database.item

import android.graphics.Color.BLACK
import android.graphics.Color.parseColor
import androidx.room.ColumnInfo
import androidx.room.Entity
import projekt.cloud.piece.music.player.database.base.BaseItem
import java.io.Serializable

@Entity(tableName = "color")
class ColorItem(
    id: String,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "backgroundColor") var backgroundColor: Int = parseColor("#FFE1E1E1"),
    @ColumnInfo(name = "primaryColor") var primaryColor: Int = BLACK,
    @ColumnInfo(name = "secondaryColor") var secondaryColor: Int = BLACK): BaseItem(id), Serializable {
    
    companion object {
        const val TYPE_AUDIO = 0
        const val TYPE_ALBUM = 1
        const val TYPE_ARTIST = 2
        const val TYPE_PLAYLIST = 3
    }
    
}