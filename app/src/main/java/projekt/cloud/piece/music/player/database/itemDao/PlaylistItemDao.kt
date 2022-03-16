package projekt.cloud.piece.music.player.database.itemDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import projekt.cloud.piece.music.player.database.item.PlaylistItem

@Dao
interface PlaylistItemDao {

    @Query("select * from playlist")
    fun query(): List<PlaylistItem>

    @Insert
    fun insert(vararg playlistContentItems: PlaylistItem)

    @Delete
    fun delete(playlistContentItem: PlaylistItem)

}