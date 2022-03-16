package projekt.cloud.piece.music.player.database.item

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import projekt.cloud.piece.c2pinyin.pinyin
import java.io.Serializable

@Entity(tableName = "artist")
data class ArtistItem(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "pinyin") val pinyin: String = name.pinyin
): Serializable