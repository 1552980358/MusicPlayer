package projekt.cloud.piece.music.player.database.audio.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import projekt.cloud.piece.music.player.database.audio.item.PlaylistItem

@Dao
interface PlaylistDao {

    @Query("select * from playlist")
    fun query(): List<PlaylistItem>

    @Insert
    fun insert(vararg playlists: PlaylistItem)

    @Update
    fun update(playlistItem: PlaylistItem)

    @Delete
    fun delete(vararg playlists: PlaylistItem)

}