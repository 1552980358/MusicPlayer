package projekt.cloud.piece.music.player.database.itemDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import projekt.cloud.piece.music.player.database.item.ArtistItem

@Dao
interface ArtistItemDao {

    @Query("select * from artist")
    fun query(): List<ArtistItem>
    
    @Query("select * from artist where id=:id")
    fun query(id: String): ArtistItem

    @Insert(onConflict = REPLACE)
    fun insert(vararg artistItems: ArtistItem)

    @Delete
    fun delete(vararg artistItems: ArtistItem)

    @Update
    fun update(artistItems: List<ArtistItem>)

}