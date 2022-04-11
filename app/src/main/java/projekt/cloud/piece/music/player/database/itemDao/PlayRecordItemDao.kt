package projekt.cloud.piece.music.player.database.itemDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.database.item.PlayRecordItem

@Dao
interface PlayRecordItemDao {

    @Insert
    fun insert(vararg playRecordItems: PlayRecordItem)

    fun insert(audio: String) = insert(PlayRecordItem(audio))

    @Query("select id from `play-record` where audio=:audio")
    fun queryId(audio: String): List<String>

    @Query("select count(audio) from `play-record` where audio=:audio ")
    fun count(audio: String): Int

    @Query("select aud.title as audio, alb.title as album, art.title as artist, count(pr.id) as count " +
        "from `play-record` pr " +
        "inner join audio aud on pr.audio = aud.id " +
        "inner join album alb on alb.id = aud.album " +
        "inner join artist art on art.id = aud.artist " +
        "group by pr.audio " +
        "order by count desc")
    fun queryItems(): List<AudioItemCount>
    data class AudioItemCount(val audio: String, val album: String, val artist: String, val count: Int)

    fun getPlayRecord(audioItem: AudioItem) = arrayListOf<Long>().apply {
        queryId(audioItem.id).forEach { add(it.toLong()) }
    }

}