package projekt.cloud.piece.music.player.database.audio.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import projekt.cloud.piece.music.player.database.audio.item.AudioItem

@Dao
interface AudioDao {

    @Query("select * from audio")
    fun queryAll(): List<AudioItem>
    
    @Query("select * from audio where id = :id")
    fun query(id: String): AudioItem

    @Insert
    fun insert(vararg audios: AudioItem)

    @Update
    fun update(audio: AudioItem)

    @Update
    fun update(audios: List<AudioItem>)

    @Delete
    fun delete(vararg audios: AudioItem)

}