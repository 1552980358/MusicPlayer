package projekt.cloud.piece.music.player.database.audio.refs

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import projekt.cloud.piece.music.player.database.audio.item.PlaylistItem
import java.io.Serializable

object PlaylistWithCountRef {

    data class PlaylistWithCount(val id: String, var title: String, var pinyin: String, var description: String?, val count: Int): Serializable {
        val playlistItem get() = PlaylistItem(id, title, description)
    }

    @Dao
    interface PlaylistWithCountDao {

        @Transaction
        @Query("select p.id as id, p.title as title, p.pinyin as pinyin, p.description as description, count(pa.audio) as count " +
               "from playlist p " +
               "inner join `playlist-audio` pa on p.id = pa.playlist " +
               "group by p.id"
        )
        fun query(): List<PlaylistWithCount>

    }

}