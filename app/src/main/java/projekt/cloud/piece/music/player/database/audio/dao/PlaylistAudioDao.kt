package projekt.cloud.piece.music.player.database.audio.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import projekt.cloud.piece.music.player.database.audio.item.AudioItem
import projekt.cloud.piece.music.player.database.audio.item.PlaylistAudioItem

/**
 * [PlaylistAudioDao]
 *
 * Methods:
 * [query]
 * [insert]
 * [update]
 * [delete]
 **/
@Dao
interface PlaylistAudioDao {

    @Query("select * from audio where id in (select audio from `playlist-audio` where playlist = :playlist)")
    fun query(playlist: String): List<AudioItem>

    @Insert
    fun insert(vararg playlistAudios: PlaylistAudioItem)

    @Update
    fun update(playlistAudio: PlaylistAudioItem)

    @Delete
    fun delete(vararg playlistAudios: PlaylistAudioItem)

}