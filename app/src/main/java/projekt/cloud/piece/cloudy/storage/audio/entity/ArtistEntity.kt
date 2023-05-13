package projekt.cloud.piece.cloudy.storage.audio.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import projekt.cloud.piece.cloudy.storage.audio.entity.ArtistEntity.ArtistEntityConstants.TABLE_ARTIST
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView

@Entity(TABLE_ARTIST)
data class ArtistEntity(
    @PrimaryKey
    @ColumnInfo(ARTIST_ID)
    val id: String,
    @ColumnInfo(ARTIST_NAME)
    val name: String
) {

    companion object ArtistEntityConstants {

        const val TABLE_ARTIST = "artist"

        const val ARTIST_ID = "id"
        const val ARTIST_NAME = "name"

    }

    @Ignore
    constructor(metadataView: MetadataView): this(
        metadataView.artist, metadataView.artistName
    )

}