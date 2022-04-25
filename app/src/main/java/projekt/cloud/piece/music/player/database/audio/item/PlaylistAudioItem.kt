package projekt.cloud.piece.music.player.database.audio.item

import androidx.room.ColumnInfo
import androidx.room.Entity
import projekt.cloud.piece.music.player.database.audio.item.base.BaseItem
import java.io.Serializable
import java.lang.System.currentTimeMillis

@Entity(tableName = "playlist-audio")
class PlaylistAudioItem(@ColumnInfo(name = "audio") val audio: String,
                        @ColumnInfo(name = "playlist") val playlist: String): BaseItem(currentTimeMillis()), Serializable