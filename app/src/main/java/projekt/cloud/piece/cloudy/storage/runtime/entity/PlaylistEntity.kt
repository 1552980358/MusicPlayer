package projekt.cloud.piece.cloudy.storage.runtime.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import projekt.cloud.piece.cloudy.storage.runtime.entity.PlaylistEntity.PlaylistEntityConstant.TABLE_PLAYLIST

@Entity(TABLE_PLAYLIST)
class PlaylistEntity(
    @PrimaryKey
    @ColumnInfo(PLAYLIST_INDEX)
    val index: Int,
    @ColumnInfo(PLAYLIST_ID)
    val id: Int,
    @ColumnInfo(PLAYLIST_HAS_NEXT)
    val hasNext: Boolean
) {

    companion object PlaylistEntityConstant {

        const val TABLE_PLAYLIST = "playlist"

        const val PLAYLIST_INDEX = "index"
        const val PLAYLIST_ID = "id"
        const val PLAYLIST_HAS_NEXT = "has_next"

    }

}