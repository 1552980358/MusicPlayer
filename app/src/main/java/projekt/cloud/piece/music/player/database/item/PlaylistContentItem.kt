package projekt.cloud.piece.music.player.database.item

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.lang.System.currentTimeMillis

@Entity(tableName = "playlist-content")
data class PlaylistContentItem(
    @PrimaryKey @ColumnInfo(name = "id") val primaryKey: Long = currentTimeMillis(),
    @ColumnInfo(name = "audio") val audio: String,
    @ColumnInfo(name = "playlist") val playlist: String
): Serializable