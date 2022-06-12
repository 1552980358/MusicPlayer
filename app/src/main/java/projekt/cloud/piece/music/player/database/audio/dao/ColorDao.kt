package projekt.cloud.piece.music.player.database.audio.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import projekt.cloud.piece.music.player.database.audio.item.ColorItem

/**
 * [ColorDao]
 *
 * Methods:
 * [hasItem]
 * [queryDefault]
 * [queryAudio]
 * [queryAlbum]
 * [insert]
 * [update]
 * [delete]
 * [query]
 **/
@Dao
interface ColorDao {

    @Query("select count(*) from color where audio = :audio or album = :album group by id")
    fun hasItem(audio: String, album: String): Int

    @Query("select * from color where audio is null and album is null")
    fun queryDefault(): ColorItem

    @Query("select * from color where audio is not null and audio = :audio")
    fun queryAudio(audio: String): ColorItem?

    @Query("select * from color where album is not null and album = :album")
    fun queryAlbum(album: String): ColorItem

    @Insert(onConflict = REPLACE)
    fun insert(vararg colorItems: ColorItem)

    @Update
    fun update(colorItem: ColorItem)

    @Update
    fun update(colorList: List<ColorItem>)

    @Delete
    fun delete(vararg colorItems: ColorItem)

    fun query(audio: String, album: String) =
        if (hasItem(audio, album) == 0) queryDefault()
        else queryAudio(audio) ?: queryAlbum(album)

}