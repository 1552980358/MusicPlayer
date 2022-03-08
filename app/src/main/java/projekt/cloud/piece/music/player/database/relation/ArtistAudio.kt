package projekt.cloud.piece.music.player.database.relation

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import androidx.room.Relation
import projekt.cloud.piece.music.player.database.item.ArtistItem
import projekt.cloud.piece.music.player.database.item.AudioItem

object ArtistAudio {

    data class ArtistAudioRelation(
        @Embedded val artistItem: ArtistItem,
        @Relation(
            parentColumn = "id",
            entityColumn = "artist"
        )
        val audioItems: List<AudioItem>
    )

    @Dao
    interface ArtistAudioDao {

        @Query("select * from audio, artist")
        fun query(): List<ArtistAudioRelation>

    }

}