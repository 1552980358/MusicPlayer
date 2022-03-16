package projekt.cloud.piece.music.player.database.item

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import projekt.cloud.piece.c2pinyin.pinyin
import java.io.Serializable
import java.lang.System.currentTimeMillis

@Entity(tableName = "playlist")
data class PlaylistItem(
    @PrimaryKey @ColumnInfo(name = "id") val id: Long = currentTimeMillis(),
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "pinyin") val pinyin: String = title.pinyin
): Serializable