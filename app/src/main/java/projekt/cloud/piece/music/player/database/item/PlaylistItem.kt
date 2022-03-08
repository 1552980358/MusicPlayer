package projekt.cloud.piece.music.player.database.item

import androidx.room.ColumnInfo
import androidx.room.Entity
import projekt.cloud.piece.c2pinyin.pinyin

@Entity(tableName = "playlist", primaryKeys = ["title", "pinyin"])
data class PlaylistItem(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "pinyin") val pinyin: String = title.pinyin
)