package projekt.cloud.piece.music.player.storage.runtime.databaseView

import androidx.room.DatabaseView
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView.AlbumViewConstant.ALBUM_VIEW_COLUMN_ID
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView.AlbumViewConstant.ALBUM_VIEW_COLUMN_TITLE
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView.AlbumViewConstant.ALBUM_VIEW_NAME
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_ALBUM
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_ALBUM_TITLE
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_TABLE_NAME

@DatabaseView(
    value = "SELECT " +
            "DISTINCT($AUDIO_METADATA_COLUMN_ALBUM) AS $ALBUM_VIEW_COLUMN_ID, " +
            "$AUDIO_METADATA_COLUMN_ALBUM_TITLE AS $ALBUM_VIEW_COLUMN_TITLE " +
            "FROM $AUDIO_METADATA_TABLE_NAME",
    viewName = ALBUM_VIEW_NAME
)
data class AlbumView(
    val id: String,
    val title: String
) {

    companion object AlbumViewConstant {
        const val ALBUM_VIEW_NAME = "album_view"
        const val ALBUM_VIEW_COLUMN_ID = "id"
        const val ALBUM_VIEW_COLUMN_TITLE = "title"
    }

}