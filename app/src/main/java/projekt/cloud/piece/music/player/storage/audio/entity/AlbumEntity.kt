package projekt.cloud.piece.music.player.storage.audio.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import projekt.cloud.piece.music.player.storage.audio.entity.AlbumEntity.ArtistEntityConstants.ALBUM_TABLE_NAME

@Entity(tableName = ALBUM_TABLE_NAME)
data class AlbumEntity(
    @PrimaryKey
    @ColumnInfo(name = ALBUM_COLUMN_ID)
    val id: String,
    @ColumnInfo(name = ALBUM_COLUMN_TITLE)
    val title: String
) {

    companion object ArtistEntityConstants {
        const val ALBUM_TABLE_NAME = "album"
        const val ALBUM_COLUMN_ID = "id"
        const val ALBUM_COLUMN_TITLE = "title"
    }

}