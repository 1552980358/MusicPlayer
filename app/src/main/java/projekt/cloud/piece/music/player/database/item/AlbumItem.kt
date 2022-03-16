package projekt.cloud.piece.music.player.database.item

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import projekt.cloud.piece.c2pinyin.pinyin
import java.io.Serializable

@Entity(tableName = "album")
data class AlbumItem(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "pinyin") val pinyin: String = title.pinyin,
): Serializable {
    @Ignore
    var size = 0
}