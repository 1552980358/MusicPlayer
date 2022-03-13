package projekt.cloud.piece.music.player.database.item

import android.graphics.Color.BLACK
import android.graphics.Color.parseColor
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "color")
data class ColorItem(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "backgroundColor") val backgroundColor: Int = parseColor("#FFE1E1E1"),
    @ColumnInfo(name = "primaryColor") val primaryColor: Int = BLACK,
    @ColumnInfo(name = "secondaryColor") val secondaryColor: Int = BLACK) {
    
    companion object {
        const val TYPE_AUDIO = 0
        const val TYPE_ALBUM = 1
        const val TYPE_ARTIST = 1
    }
    
}