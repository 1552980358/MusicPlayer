package projekt.cloud.piece.music.player.database.item

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "playlist-content")
data class PlaylistContentItem(
    @PrimaryKey(autoGenerate = true) val primaryKey: Int? = null,
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "playlist") val playlist: String
): Serializable