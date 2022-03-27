package projekt.cloud.piece.music.player.database.itemDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import projekt.cloud.piece.music.player.database.item.AudioItem

@Dao
interface AudioItemDao {

    @Query("select * from audio")
    fun query(): List<AudioItem>

    @Query("select * from audio where duration > :duration and size > :fileSize")
    fun query(duration: String, fileSize: String): List<AudioItem>
    
    @Query("select * from audio where id=:id")
    fun query(id: String): AudioItem

    @Query("select * from audio where album=:album")
    fun queryAlbum(album: String): List<AudioItem>

    @Query("select aud.* from audio aud inner join artist art on art.id = aud.artist where art.title like '%' || :artistName || '%'")
    fun queryArtist(artistName: String): List<AudioItem>

    @Insert
    fun insert(vararg audioItem: AudioItem)

    @Delete
    fun delete(vararg audioItems: AudioItem)

    @Update
    fun update(audioItems: List<AudioItem>)

}