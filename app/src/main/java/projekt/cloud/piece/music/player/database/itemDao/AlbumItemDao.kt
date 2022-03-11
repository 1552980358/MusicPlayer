package projekt.cloud.piece.music.player.database.itemDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import projekt.cloud.piece.music.player.database.item.AlbumItem

@Dao
interface AlbumItemDao {

    @Query("select * from album")
    fun query(): List<AlbumItem>
    
    @Query("select * from album where id=:id")
    fun query(id: String): AlbumItem

    @Insert
    fun insert(vararg albumItem: AlbumItem)

    @Delete
    fun delete(albumItem: AlbumItem)

}