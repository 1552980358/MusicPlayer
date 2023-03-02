package projekt.cloud.piece.music.player.storage.runtime.databaseView

import androidx.room.DatabaseView
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView.AlbumViewConstant.ARTIST_VIEW_COLUMN_ID
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView.AlbumViewConstant.ARTIST_VIEW_COLUMN_NAME
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView.AlbumViewConstant.ARTIST_VIEW_NAME
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_ARTIST_NAME
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_ARTIST
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_TABLE_NAME

@DatabaseView(
    value = "SELECT " +
            "DISTINCT($AUDIO_METADATA_COLUMN_ARTIST) AS $ARTIST_VIEW_COLUMN_ID, " +
            "$AUDIO_METADATA_COLUMN_ARTIST_NAME AS $ARTIST_VIEW_COLUMN_NAME " +
            "FROM $AUDIO_METADATA_TABLE_NAME ",
    viewName = ARTIST_VIEW_NAME
)
data class ArtistView(
    val id: String,
    val title: String
) {

    companion object AlbumViewConstant {
        const val ARTIST_VIEW_NAME = "artist_view"
        const val ARTIST_VIEW_COLUMN_ID = "id"
        const val ARTIST_VIEW_COLUMN_NAME = "name"
    }

}