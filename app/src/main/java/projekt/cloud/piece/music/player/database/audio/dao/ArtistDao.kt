package projekt.cloud.piece.music.player.database.audio.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import projekt.cloud.piece.music.player.database.audio.item.ArtistItem

@Dao
interface ArtistDao {

    @Query("select * from artist")
    fun queryAll(): List<ArtistItem>

    @Query("select * from artist where id=:id")
    fun query(id: String): ArtistItem

    @Insert
    fun insert(vararg artists: ArtistItem)

    @Update
    fun update(artist: ArtistItem)

    @Update
    fun update(artists: List<ArtistItem>)

    @Delete
    fun delete(vararg artists: ArtistItem)

}