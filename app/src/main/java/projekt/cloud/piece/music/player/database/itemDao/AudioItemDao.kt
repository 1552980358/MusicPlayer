package projekt.cloud.piece.music.player.database.itemDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import projekt.cloud.piece.music.player.database.item.AudioItem

@Dao
interface AudioItemDao {

    @Query("select * from audio")
    fun query(): List<AudioItem>

    @Insert
    fun insert(vararg audioItem: AudioItem)

    @Delete
    fun delete(audioItem: AudioItem)

}