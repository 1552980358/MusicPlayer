package projekt.cloud.piece.music.player.database.itemDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import projekt.cloud.piece.music.player.database.item.ColorItem
import projekt.cloud.piece.music.player.database.item.ColorItem.Companion.TYPE_AUDIO

@Dao
interface ColorItemDao {
    
    @Query("select * from color")
    fun query(): List<ColorItem>

    @Query("select * from color where id=:id")
    fun query(id: String): ColorItem

    @Query("select * from color where id=:id and type=${TYPE_AUDIO}")
    fun queryForAudio(id: String): ColorItem?
    
    @Query("select * from color where id=:audioId or id=:albumId order by type limit 1")
    fun query(audioId: String, albumId: String): ColorItem
    
    @Insert
    fun insert(vararg colorItem: ColorItem)
    
    @Delete
    fun delete(vararg colorItems: ColorItem)
    
}