package projekt.cloud.piece.music.player.item.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import projekt.cloud.piece.music.player.item.Album

@Dao
interface AlbumDao {
    
    @Query("select * from album")
    fun query(): List<Album>
    
    @Insert
    fun insert(vararg audios: Album)
    
}