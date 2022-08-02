package projekt.cloud.piece.music.player.item.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import projekt.cloud.piece.music.player.item.Audio

@Dao
interface AudioDao {

    @Query("select * from audio")
    fun query(): List<Audio>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg audios: Audio)

}