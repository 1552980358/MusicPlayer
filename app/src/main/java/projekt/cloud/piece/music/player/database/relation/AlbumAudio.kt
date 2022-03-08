package projekt.cloud.piece.music.player.database.relation

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import projekt.cloud.piece.music.player.database.item.AlbumItem
import projekt.cloud.piece.music.player.database.item.AudioItem

object AlbumAudio {

    data class AlbumAudioRelation(
        @Embedded val albumItem: AlbumItem,
        @Relation(
            parentColumn = "id",
            entityColumn = "album"
        )
        val audioItems: List<AudioItem>
    )

    @Dao
    interface AlbumAudioDao {

        @Transaction
        @Query("select * from audio, album")
        fun queryAlbumAudio(): List<AlbumAudioRelation>

    }

}