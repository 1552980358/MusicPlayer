package projekt.cloud.piece.cloudy.storage.audio.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import projekt.cloud.piece.cloudy.storage.audio.entity.AlbumEntity.AlbumEntityConstants.TABLE_ALBUM
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView

@Entity(TABLE_ALBUM)
data class AlbumEntity(
    @PrimaryKey
    @ColumnInfo(ALBUM_ID)
    val id: String,
    @ColumnInfo(ALBUM_TITLE)
    val title: String
) {

    companion object AlbumEntityConstants {

        const val TABLE_ALBUM = "album"

        const val ALBUM_ID = "id"
        const val ALBUM_TITLE = "title"

    }

    @Ignore
    constructor(metadataView: MetadataView): this(
        metadataView.album, metadataView.albumTitle
    )

}