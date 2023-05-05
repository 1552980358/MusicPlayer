package projekt.cloud.piece.music.player.storage.runtime.databaseView

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView.AlbumViewConstant.ALBUM_VIEW_COLUMN_DURATION
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView.AlbumViewConstant.ALBUM_VIEW_COLUMN_ID
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView.AlbumViewConstant.ALBUM_VIEW_COLUMN_SONG_COUNT
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView.AlbumViewConstant.ALBUM_VIEW_COLUMN_TITLE
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView.AlbumViewConstant.ALBUM_VIEW_NAME
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_ALBUM
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_ALBUM_TITLE
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_DURATION
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_ID
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_TABLE_NAME

@DatabaseView(
    value = "SELECT " +
            "$AUDIO_METADATA_COLUMN_ALBUM AS $ALBUM_VIEW_COLUMN_ID, " +
            "$AUDIO_METADATA_COLUMN_ALBUM_TITLE AS $ALBUM_VIEW_COLUMN_TITLE, " +
            "COUNT($AUDIO_METADATA_COLUMN_ID) AS $ALBUM_VIEW_COLUMN_SONG_COUNT, " +
            "SUM($AUDIO_METADATA_COLUMN_DURATION) AS $ALBUM_VIEW_COLUMN_DURATION " +
            "FROM $AUDIO_METADATA_TABLE_NAME " +
            "GROUP BY $AUDIO_METADATA_COLUMN_ALBUM",
    viewName = ALBUM_VIEW_NAME
)
data class AlbumView(
    @ColumnInfo(ALBUM_VIEW_COLUMN_ID)
    val id: String,
    @ColumnInfo(ALBUM_VIEW_COLUMN_TITLE)
    val title: String,
    @ColumnInfo(ALBUM_VIEW_COLUMN_SONG_COUNT)
    val songCount: Int,
    @ColumnInfo(ALBUM_VIEW_COLUMN_DURATION)
    val duration: Long
) {

    companion object AlbumViewConstant {
        const val ALBUM_VIEW_NAME = "album_view"
        const val ALBUM_VIEW_COLUMN_ID = "id"
        const val ALBUM_VIEW_COLUMN_TITLE = "title"
        const val ALBUM_VIEW_COLUMN_SONG_COUNT = "song_count"
        const val ALBUM_VIEW_COLUMN_DURATION = "duration"
    }

}