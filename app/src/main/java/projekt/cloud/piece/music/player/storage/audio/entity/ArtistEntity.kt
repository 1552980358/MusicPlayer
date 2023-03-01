package projekt.cloud.piece.music.player.storage.audio.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import projekt.cloud.piece.music.player.storage.audio.entity.ArtistEntity.ArtistEntityConstants.ARTIST_TABLE_NAME

@Entity(tableName = ARTIST_TABLE_NAME)
data class ArtistEntity(
    @PrimaryKey
    @ColumnInfo(name = ARTIST_COLUMN_ID)
    val id: String,
    @ColumnInfo(name = ARTIST_COLUMN_NAME)
    val name: String
) {

    companion object ArtistEntityConstants {
        const val ARTIST_TABLE_NAME = "artist"
        const val ARTIST_COLUMN_ID = "id"
        const val ARTIST_COLUMN_NAME = "name"
    }

}