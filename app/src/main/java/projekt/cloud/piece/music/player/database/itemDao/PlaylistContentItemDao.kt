package projekt.cloud.piece.music.player.database.itemDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import projekt.cloud.piece.music.player.database.item.PlaylistContentItem

@Dao
interface PlaylistContentItemDao {

    @Query("select * from `playlist-content`")
    fun query(): List<PlaylistContentItem>

    @Query("select * from `playlist-content` where playlist=:playlist")
    fun query(playlist: String): List<PlaylistContentItem>

    @Insert
    fun insert(vararg playlistContentItems: PlaylistContentItem)

    @Delete
    fun delete(playlistContentItem: PlaylistContentItem)

}