package projekt.cloud.piece.music.player.item.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import projekt.cloud.piece.music.player.item.Artist

@Dao
interface ArtistDao {
    
    @Query("select * from artist")
    fun query(): List<Artist>
    
    @Insert
    fun insert(vararg audios: Artist)
    
}