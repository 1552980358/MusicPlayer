package projekt.cloud.piece.music.player.item.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import projekt.cloud.piece.music.player.item.Album

@Dao
interface AlbumDao {
    
    @Query("select * from album")
    fun query(): List<Album>
    
    @Insert(onConflict = REPLACE)
    fun insert(vararg audios: Album)
    
}