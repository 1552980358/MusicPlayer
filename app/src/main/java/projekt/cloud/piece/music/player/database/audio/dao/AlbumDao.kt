package projekt.cloud.piece.music.player.database.audio.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import projekt.cloud.piece.music.player.database.audio.item.AlbumItem

@Dao
interface AlbumDao {

    @Query("select * from album")
    fun queryAll(): List<AlbumItem>

    @Query("select * from album where id=:id")
    fun query(id: String): AlbumItem

    @Insert
    fun insert(vararg artists: AlbumItem)

    @Update
    fun update(artist: AlbumItem)

    @Update
    fun update(artists: List<AlbumItem>)

    @Delete
    fun delete(vararg artists: AlbumItem)

}