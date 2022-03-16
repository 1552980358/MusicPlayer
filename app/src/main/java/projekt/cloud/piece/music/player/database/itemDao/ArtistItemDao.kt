package projekt.cloud.piece.music.player.database.itemDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import projekt.cloud.piece.music.player.database.item.ArtistItem

@Dao
interface ArtistItemDao {

    @Query("select * from artist")
    fun query(): List<ArtistItem>
    
    @Query("select * from artist where id=:id")
    fun query(id: String): ArtistItem

    @Insert
    fun insert(vararg artistItems: ArtistItem)

    @Delete
    fun delete(artistItem: ArtistItem)

}