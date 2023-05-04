package projekt.cloud.piece.music.player.storage.runtime.databaseView

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Ignore
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView.AlbumViewConstant.ARTIST_VIEW_COLUMN_DURATION
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView.AlbumViewConstant.ARTIST_VIEW_COLUMN_ID
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView.AlbumViewConstant.ARTIST_VIEW_COLUMN_NAME
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView.AlbumViewConstant.ARTIST_VIEW_COLUMN_SONG_COUNT
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView.AlbumViewConstant.ARTIST_VIEW_NAME
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_ARTIST_NAME
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_ARTIST
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_DURATION
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_COLUMN_ID
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity.AudioMetadataEntityUtil.AUDIO_METADATA_TABLE_NAME

@DatabaseView(
    value = "SELECT " +
            "$AUDIO_METADATA_COLUMN_ARTIST AS $ARTIST_VIEW_COLUMN_ID, " +
            "$AUDIO_METADATA_COLUMN_ARTIST_NAME AS $ARTIST_VIEW_COLUMN_NAME, " +
            "COUNT($AUDIO_METADATA_COLUMN_ID) AS $ARTIST_VIEW_COLUMN_SONG_COUNT, " +
            "SUM($AUDIO_METADATA_COLUMN_DURATION) AS $ARTIST_VIEW_COLUMN_DURATION " +
            "FROM $AUDIO_METADATA_TABLE_NAME " +
            "GROUP BY $AUDIO_METADATA_COLUMN_ARTIST",
    viewName = ARTIST_VIEW_NAME
)
data class ArtistView(
    @ColumnInfo(ARTIST_VIEW_COLUMN_ID)
    val id: String,
    @ColumnInfo(ARTIST_VIEW_COLUMN_NAME)
    val name: String,
    @ColumnInfo(ARTIST_VIEW_COLUMN_SONG_COUNT)
    val songCount: Int,
    @ColumnInfo(ARTIST_VIEW_COLUMN_DURATION)
    val duration: Long
) {

    companion object AlbumViewConstant {
        const val ARTIST_VIEW_NAME = "artist_view"
        const val ARTIST_VIEW_COLUMN_ID = "id"
        const val ARTIST_VIEW_COLUMN_NAME = "name"
        const val ARTIST_VIEW_COLUMN_SONG_COUNT = "song_count"
        const val ARTIST_VIEW_COLUMN_DURATION = "duration"
    }

    @Ignore
    private var _albums: List<String>? = null
    val albums: List<String>
        get() = _albums!!
    fun setAlbums(albums: List<String>?) {
        _albums = albums
    }

}